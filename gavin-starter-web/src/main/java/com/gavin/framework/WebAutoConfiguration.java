package com.gavin.framework;

import com.gavin.framework.config.UndertowPoolCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import cn.hutool.core.date.DatePattern;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Web 服务自动配置
 *
 * @author Gavin
 */
@Configuration
@ConditionalOnWebApplication
public class WebAutoConfiguration implements WebMvcConfigurer {

  /**
   * 初始化 undertowPool 大小
   */
  @Bean
  public UndertowPoolCustomizer undertowPoolCustomizer() {
    return new UndertowPoolCustomizer();
  }

  /**
   * 增加GET请求参数中时间类型转换 {@link com.gavin.framework.jackson.GavinJavaTimeModule}
   * <ul>
   * <li>HH:mm:ss -> LocalTime</li>
   * <li>yyyy-MM-dd -> LocalDate</li>
   * <li>yyyy-MM-dd HH:mm:ss -> LocalDateTime</li>
   * </ul>
   *
   * @param registry
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
    registrar.setTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
    registrar.setDateFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
    registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
    registrar.registerFormatters(registry);
  }

  /**
   * 避免form 提交 context-type 不规范中文乱码
   *
   * @return Filter
   */
  @Bean
  public OrderedCharacterEncodingFilter characterEncodingFilter() {
    OrderedCharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
    filter.setEncoding(StandardCharsets.UTF_8.name());
    filter.setForceEncoding(true);
    filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return filter;
  }

}
