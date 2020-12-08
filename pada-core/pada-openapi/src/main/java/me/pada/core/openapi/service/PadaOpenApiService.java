package me.pada.core.openapi.service;

import com.fasterxml.jackson.databind.Module;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import me.pada.core.openapi.config.PadaOpenApiProperties;
import me.pada.core.openapi.config.PadaOpenApiProperties.Security;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

@Configuration
@RequiredArgsConstructor
public class PadaOpenApiService {
  private final PadaOpenApiProperties properties;

  @Bean
  @ConditionalOnMissingBean
  public SecurityConfiguration securityConfiguration() {
    Security security = properties.getSecurity();

    SecurityConfigurationBuilder builder = SecurityConfigurationBuilder.builder();
    if (!ObjectUtils.isEmpty(security.getClientId())) {
      builder = builder.clientId(security.getClientId());
    }
    if (!ObjectUtils.isEmpty(security.getClientSecret())) {
      builder = builder.clientSecret(security.getClientSecret());
    }
    if (!ObjectUtils.isEmpty(security.getRealm())) {
      builder = builder.realm(security.getRealm());
    }
    if (!ObjectUtils.isEmpty(security.getAppName())) {
      builder = builder.appName(security.getAppName());
    }
    if (!Objects.isNull(security.getScopeSeparator())) {
      builder = builder.scopeSeparator(security.getScopeSeparator());
    }
    if (!ObjectUtils.isEmpty(security.getAdditionalQueryStringParams())) {
      builder = builder.additionalQueryStringParams(security.getAdditionalQueryStringParams());
    }
    if (!ObjectUtils.isEmpty(security.getUseBasicAuthenticationWithAccessCodeGrant())) {
      builder = builder.useBasicAuthenticationWithAccessCodeGrant(security.getUseBasicAuthenticationWithAccessCodeGrant());
    }
    if (!ObjectUtils.isEmpty(security.getEnableCsrfSupport())) {
      builder = builder.enableCsrfSupport(security.getEnableCsrfSupport());
    }
    return builder.build();
  }

  @Bean
  @ConditionalOnMissingBean
  public Docket openApi(List<SecurityScheme> securitySchemes, List<SecurityContext> securityContexts) {
    Docket docket = new Docket(properties.getDocumentationType().getValue());
    docket = docket.apiInfo(apiInfo());
    docket = docket.genericModelSubstitutes(Optional.class);
    if (!ObjectUtils.isEmpty(securitySchemes)) {
      docket = docket.securitySchemes(securitySchemes);
    }
    if (!ObjectUtils.isEmpty(securityContexts)) {
      docket = docket.securityContexts(securityContexts);
    }
    ApiSelectorBuilder apiSelectorBuilder = docket.select();
    String[] basePackageScan = properties.getBasePackageScan();
    if (!ObjectUtils.isEmpty(basePackageScan)) {
      apiSelectorBuilder.apis(Arrays.stream(basePackageScan).map(RequestHandlerSelectors::basePackage).reduce(Predicate::and).orElse(p -> true));
    }
    String[] pathRegexScan = properties.getPathRegexScan();
    if (!ObjectUtils.isEmpty(pathRegexScan)) {
      apiSelectorBuilder.paths(Arrays.stream(pathRegexScan).map(PathSelectors::regex).reduce(Predicate::and).orElse(p -> true));
    }
    return apiSelectorBuilder.build();
  }

  @Bean
  @ConditionalOnMissingBean
  public ApiInfo apiInfo() {
    PadaOpenApiProperties.ApiInfo apiInfo = properties.getApiInfo();
    ApiInfoBuilder builder = new ApiInfoBuilder();
    if (!ObjectUtils.isEmpty(apiInfo.getTitle())) {
      builder = builder.title(apiInfo.getTitle());
    }
    if (!ObjectUtils.isEmpty(apiInfo.getDescription())) {
      builder = builder.description(apiInfo.getDescription());
    }
    if (!ObjectUtils.isEmpty(apiInfo.getTermsOfServiceUrl())) {
      builder = builder.termsOfServiceUrl(apiInfo.getTermsOfServiceUrl());
    }
    if (!ObjectUtils.isEmpty(apiInfo.getContact())) {
      Contact contact = new Contact(apiInfo.getContact().getName(), apiInfo.getContact().getUrl(), apiInfo.getContact().getEmail());
      builder = builder.contact(contact);
    }
    if (!ObjectUtils.isEmpty(apiInfo.getLicense())) {
      builder = builder.license(apiInfo.getLicense());
    }
    if (!ObjectUtils.isEmpty(apiInfo.getLicenseUrl())) {
      builder = builder.license(apiInfo.getLicenseUrl());
    }
    if (!ObjectUtils.isEmpty(apiInfo.getVersion())) {
      builder = builder.version(apiInfo.getVersion());
    }
    return builder.build();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnClass({Module.class, JsonNullableModule.class})
  public Module jsonNullableModule() {
    return new JsonNullableModule();
  }
}