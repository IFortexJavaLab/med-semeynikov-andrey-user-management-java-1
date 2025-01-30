package com.ifortex.internship.usermanagementapi.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {

  @Bean
  public ErrorDecoder errorDecoder() {
    return new CustomFeignErrorDecoder();
  }

  @Bean
  public FeignClientInterceptor interceptor() {
    return new FeignClientInterceptor();
  }
}
