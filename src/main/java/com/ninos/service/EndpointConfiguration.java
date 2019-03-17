package com.ninos.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "endpoint-requests")
  public Endpoint endpoint() {
    return new Endpoint();
  }

  /*
   * the endpoint configuration items
   */
  public static class Endpoint {
    private int timeoutMs;

    public int getTimeoutMs() {
      return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
      this.timeoutMs = timeoutMs;
    }
  }
}
