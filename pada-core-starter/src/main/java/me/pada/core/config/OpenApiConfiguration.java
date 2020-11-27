package me.pada.core.config;

import com.fasterxml.jackson.databind.Module;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import me.pada.core.properties.PadaConfigProperties;
import me.pada.core.properties.PadaConfigProperties.OpenApi;
import me.pada.core.properties.PadaConfigProperties.OpenApi.Security;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
@ConditionalOnMissingBean
@ConditionalOnProperty(value = "pada-config.openapi.enabled", havingValue = "true")
public class OpenApiConfiguration {

  private final List<SecurityScheme> securitySchemes;
  private final List<SecurityContext> securityContexts;
  private final PadaConfigProperties configProperties;

  public OpenApiConfiguration(
      Optional<List<SecurityScheme>> securitySchemes,
      Optional<List<SecurityContext>> securityContexts,
      PadaConfigProperties configProperties) {
    this.securitySchemes = securitySchemes.orElse(null);
    this.securityContexts = securityContexts.orElse(null);
    this.configProperties = configProperties;
  }

  @Bean
  public SecurityConfiguration securityConfiguration() {
    Security security = configProperties.getOpenapi().getSecurity();

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
  public Docket openApi() {
    Docket docket = new Docket(configProperties.getOpenapi().getDocumentationType().getValue());
    docket = docket.apiInfo(apiInfo());
    docket = docket.genericModelSubstitutes(Optional.class);
    if (!ObjectUtils.isEmpty(securitySchemes)) {
      docket = docket.securitySchemes(securitySchemes);
    }
    if (!ObjectUtils.isEmpty(securityContexts)) {
      docket = docket.securityContexts(securityContexts);
    }
    ApiSelectorBuilder apiSelectorBuilder = docket.select();
    String[] basePackageScan = configProperties.getOpenapi().getBasePackageScan();
    if (!ObjectUtils.isEmpty(basePackageScan)) {
      apiSelectorBuilder.apis(Arrays.stream(basePackageScan).map(RequestHandlerSelectors::basePackage).reduce(Predicate::and).orElse(p -> true));
    }
    String[] pathRegexScan = configProperties.getOpenapi().getPathRegexScan();
    if (!ObjectUtils.isEmpty(pathRegexScan)) {
      apiSelectorBuilder.paths(Arrays.stream(pathRegexScan).map(PathSelectors::regex).reduce(Predicate::and).orElse(p -> true));
    }
    return apiSelectorBuilder.build();
  }

  @Bean
  public ApiInfo apiInfo() {
    OpenApi.ApiInfo apiInfo = configProperties.getOpenapi().getApiInfo();
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
  public WebSecurityConfigurerAdapter webConfigurer() {
    return new WebSecurityConfigurerAdapter() {
      @Override
      public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        if (!ObjectUtils.isEmpty(configProperties.getOpenapi().getUrlView())) {
          web.ignoring().antMatchers(configProperties.getOpenapi().getUrlView());
        }
      }
    };
  }

  @Bean
  public Module jsonNullableModule() {
    return new JsonNullableModule();
  }
}