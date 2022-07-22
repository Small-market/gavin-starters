package com.gavin.framework;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gavin.framework.jackson.GavinJavaTimeModule;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.TimeZone;

/**
 * jackson 自动配置类
 *
 * @author Gavin
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JsonAutoConfigruation {

  private static final String ASIA_SHANGHAI = "Asia/Shanghai";

  @Bean
  @ConditionalOnMissingBean
  public Jackson2ObjectMapperBuilderCustomizer customizer() {
    return builder -> {
      builder.locale(Locale.CHINA);
      // 设置时区
      builder.timeZone(TimeZone.getTimeZone(ASIA_SHANGHAI));
      // 所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
      builder.simpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
      // 序列化类型
      builder.serializerByType(Long.class, ToStringSerializer.instance);
      builder.modules(new GavinJavaTimeModule());
    };
  }
}
