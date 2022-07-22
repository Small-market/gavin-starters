package com.gavin.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gavin.framework.aspect.WebLogAspect;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class LogAutoConfiguration {

  @Bean
  public WebLogAspect webLogAspect(ObjectMapper json) {
    return new WebLogAspect(json);
  }

}
