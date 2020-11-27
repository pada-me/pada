package me.pada.core.config;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static me.pada.core.utils.PadaConstants.PunctuationConstants.COMMA;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import static org.springframework.http.HttpMethod.OPTIONS;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.pada.core.anotation.ConditionalOnPropertyNotEmpty;
import me.pada.core.properties.PadaConfigProperties;
import me.pada.core.properties.PadaConfigProperties.Cors;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnPropertyNotEmpty("pada-config.cors")
public class PadaCorsFilter extends OncePerRequestFilter {

  private final PadaConfigProperties configProperties;

  public PadaCorsFilter(PadaConfigProperties configProperties) {
    this.configProperties = configProperties;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    Cors cors = configProperties.getCors();
    if (!ObjectUtils.isEmpty(cors.getAllowedOrigins())) {
      response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, String.join(COMMA, cors.getAllowedOrigins()));
    }
    if (!ObjectUtils.isEmpty(cors.getAllowedMethods())) {
      response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, String.join(COMMA, cors.getAllowedMethods()));
    }
    if (!ObjectUtils.isEmpty(cors.getAllowedHeaders())) {
      response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, String.join(COMMA, cors.getAllowedHeaders()));
    }
    if (!ObjectUtils.isEmpty(cors.getAllowCredentials())) {
      response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, cors.getAllowCredentials().toString());
    }
    if (!ObjectUtils.isEmpty(cors.getExposedHeaders())) {
      response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, String.join(COMMA, cors.getExposedHeaders()));
    }
    if (!ObjectUtils.isEmpty(cors.getMaxAge())) {
      response.setHeader(ACCESS_CONTROL_MAX_AGE, String.valueOf(cors.getMaxAge()));
    }
    if (OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
      response.setStatus(SC_OK);
    } else {
      filterChain.doFilter(request, response);
    }
  }
}