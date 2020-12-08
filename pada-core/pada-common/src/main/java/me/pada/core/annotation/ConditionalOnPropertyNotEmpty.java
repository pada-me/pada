package me.pada.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import me.pada.core.annotation.ConditionalOnPropertyNotEmpty.OnPropertyNotEmptyCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Conditional(OnPropertyNotEmptyCondition.class)
public @interface ConditionalOnPropertyNotEmpty {
  String value();

  class OnPropertyNotEmptyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      Map<String, Object> attrs = metadata.getAnnotationAttributes(ConditionalOnPropertyNotEmpty.class.getName());
      if (attrs != null) {
        String propertyName = (String) attrs.get("value");
        String value = context.getEnvironment().getProperty(propertyName);
        return value != null && !value.trim().isEmpty();
      }
      return false;
    }
  }
}