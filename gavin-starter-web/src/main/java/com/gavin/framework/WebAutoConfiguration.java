package com.gavin.framework;

import com.gavin.framework.config.UndertowPoolCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web 服务自动配置
 *
 * @author Gavin
 */
@Configuration
@ConditionalOnWebApplication
public class WebAutoConfiguration {

  /**
   * 初始化 undertowPool 大小
   */
  @Bean
  public UndertowPoolCustomizer undertowPoolCustomizer() {
    return new UndertowPoolCustomizer();
  }
}
