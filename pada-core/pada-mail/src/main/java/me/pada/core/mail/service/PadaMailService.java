package me.pada.core.mail.service;

import java.io.File;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pada.core.mail.config.PadaMailProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean({JavaMailSender.class, SpringTemplateEngine.class})
public class PadaMailService {
  public static final String BASE_URL = "BASE_URL";

  private final JavaMailSender javaMailSender;
  private final SpringTemplateEngine templateEngine;
  private final PadaMailProperties properties;

  @Async
  public void sendFromTemplate(String templateName, String to, String subject, Map<String, Object> variables, boolean isMultipart, File[] files) {
    Context context = new Context();
    context.setVariable(BASE_URL, properties.getBaseUrl());
    context.setVariables(variables);
    String content = templateEngine.process(properties.getPathMailTemplate() + templateName, context);
    this.send(to, subject, content, true, isMultipart, files);
  }

  @Async
  public void send(String to, String subject, String content, boolean isHtml, boolean isMultipart, File[] files) {
    log.info("====START PadaMailService send email [multipart '{}' and html '{}'] to '{}' with subject '{}' and content=[{}]====", isMultipart, isHtml, to, subject, content);
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, isMultipart);
      helper.setTo(to);
      helper.setFrom(properties.getFrom());
      helper.setSubject(subject);
      helper.setText(content, isHtml);
      if (isMultipart) {
        for (File file: files) {
          helper.addAttachment(file.getName(), file);
        }
      }
      javaMailSender.send(message);
    } catch (Exception e) {
      log.info("====ERROR PadaMailService send email could not be sent to '{}': {}", to, e.getMessage());
    }
    log.info("====END PadaMailService sent email to '{}' successfully====", to);
  }
}