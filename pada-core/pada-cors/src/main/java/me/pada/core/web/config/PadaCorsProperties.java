package me.pada.core.web.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pada-config.cors")
public class PadaCorsProperties {
  private List<String> allowedOrigins;
  private List<String> allowedMethods;
  private List<HttpMethod> resolvedMethods;
  private List<String> allowedHeaders;
  private List<String> exposedHeaders;
  private Boolean allowCredentials;
  private Long maxAge;
}