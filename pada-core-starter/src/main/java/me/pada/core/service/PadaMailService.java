package me.pada.core.service;

import java.io.File;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pada.core.properties.PadaConfigProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class PadaMailService {
  public static final String BASE_URL = "base-url";
  private static final String PATH_MAIL_TEMPLATE = "mail";

  private final JavaMailSender javaMailSender;
  private final SpringTemplateEngine templateEngine;
  private final PadaConfigProperties configProperties;

  @Async
  public void sendFromTemplate(String templateName, String to, String subject, Map<String, Object> variables, boolean isMultipart, File[] files) {
    Context context = new Context();
    context.setVariable(BASE_URL, configProperties.getMail().getBaseUrl());
    context.setVariables(variables);
    String content = templateEngine.process(PATH_MAIL_TEMPLATE + templateName, context);
    this.send(to, subject, content, true, isMultipart, files);
  }

  @Async
  public void send(String to, String subject, String content, boolean isHtml, boolean isMultipart, File[] files) {
    log.info("====START PadaMailService sendJavaMail Send email [multipart '{}' and html '{}'] to '{}' with subject '{}' and content=[{}]====", isMultipart, isHtml, to, subject, content);
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, isMultipart);
      helper.setTo(to);
      helper.setFrom(configProperties.getMail().getFrom());
      helper.setSubject(subject);
      helper.setText(content, isHtml);
      if (isMultipart) {
        for (File file: files) {
          helper.addAttachment(file.getName(), file);
        }
      }
      javaMailSender.send(message);
    } catch (Exception e) {
      log.info("====ERROR PadaMailService send Email could not be sent to '{}': {}", to, e.getMessage());
    }
    log.info("====END Sent email to '{}' successfully====", to);
  }
}