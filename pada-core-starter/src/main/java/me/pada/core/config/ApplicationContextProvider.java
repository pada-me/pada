package me.pada.core.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility class to inject ApplicationContext into Service
 */

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  private static ApplicationContext context;

  public static ApplicationContext getApplicationContext() {
    return context;
  }

  public static void setContext(ApplicationContext ac) {
    ApplicationContextProvider.context = ac;
  }

  @Override
  public void setApplicationContext(ApplicationContext ac) throws BeansException {
    setContext(ac);
  }
}