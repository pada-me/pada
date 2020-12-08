package me.pada.core.web.service;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import static org.springframework.http.HttpMethod.OPTIONS;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.pada.core.web.config.PadaCorsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass({Filter.class})
public class PadaCorsFilterService extends OncePerRequestFilter {

  private static final String COMMA = ",";
  private final PadaCorsProperties properties;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (!ObjectUtils.isEmpty(properties.getAllowedOrigins())) {
      response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, String.join(COMMA, properties.getAllowedOrigins()));
    }
    if (!ObjectUtils.isEmpty(properties.getAllowedMethods())) {
      response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, String.join(COMMA, properties.getAllowedMethods()));
    }
    if (!ObjectUtils.isEmpty(properties.getAllowedHeaders())) {
      response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, String.join(COMMA, properties.getAllowedHeaders()));
    }
    if (!ObjectUtils.isEmpty(properties.getAllowCredentials())) {
      response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, properties.getAllowCredentials().toString());
    }
    if (!ObjectUtils.isEmpty(properties.getExposedHeaders())) {
      response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, String.join(COMMA, properties.getExposedHeaders()));
    }
    if (!ObjectUtils.isEmpty(properties.getMaxAge())) {
      response.setHeader(ACCESS_CONTROL_MAX_AGE, String.valueOf(properties.getMaxAge()));
    }
    if (OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
      response.setStatus(SC_OK);
    } else {
      filterChain.doFilter(request, response);
    }
  }
}