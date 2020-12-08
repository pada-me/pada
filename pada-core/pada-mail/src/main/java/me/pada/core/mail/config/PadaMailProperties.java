package me.pada.core.mail.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pada-config.mail")
public class PadaMailProperties {
  private String from;
  private String baseUrl;
  private String pathMailTemplate = "/templates/mail";
}