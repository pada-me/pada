package me.pada.core.openapi.config;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pada-config.openapi")
public class PadaOpenApiProperties {
  private Type documentationType = Type.OAS_30;
  private String basePath;
  private String[] basePackageScan;
  private String[] pathRegexScan;
  private final ApiInfo apiInfo = new ApiInfo();
  private final Security security = new Security();
  private String[] urlView = new String[] {"/v*/api-docs", "/swagger-resources/**", "/swagger-ui/**"};

  @Getter
  @AllArgsConstructor
  public enum Type {
    SWAGGER_12(DocumentationType.SWAGGER_12),
    SWAGGER_2(DocumentationType.SWAGGER_2),
    OAS_30(DocumentationType.OAS_30);
    private final DocumentationType value;
  }
  @Getter @Setter
  public static class ApiInfo {
    private String version;
    private String title;
    private String description;
    private String termsOfServiceUrl;
    private String license;
    private String licenseUrl;
    private Contact contact = new Contact();

    @Getter @Setter
    public static class Contact {
      private String name;
      private String url;
      private String email;
    }
  }

  @Getter @Setter
  public static class Security {
    private String clientId;
    private String clientSecret;
    private String realm;
    private String appName;
    private String scopeSeparator = " ";
    private Map<String, Object> additionalQueryStringParams;
    private Boolean useBasicAuthenticationWithAccessCodeGrant;
    private Boolean enableCsrfSupport;
    private String[] pathRegexSecurityScan;
    // OAuth2
    private String tokenUrl;
    private String[] scopes;
  }
}