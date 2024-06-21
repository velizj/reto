package com.example.demo.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

  private static final String REDIS = "redis://";

  @Value("${spring.redis.primary.host}")
  private String primaryRedisHost;

  @Value("${spring.redis.primary.port}")
  private int primaryRedisPort;

  @Value("${spring.redis.secondary.host}")
  private String secondaryRedisHost;

  @Value("${spring.redis.secondary.port}")
  private int secondaryRedisPort;

  @Bean(name = "primaryRedisClient")
  public RedissonClient primaryRedisClient() {
    Config config = new Config();
    config
        .useSingleServer()
        .setAddress(REDIS + primaryRedisHost + ":" + primaryRedisPort)
        .setConnectionPoolSize(10)
        .setConnectionMinimumIdleSize(1);

    return Redisson.create(config);
  }

  @Bean(name = "secondaryRedisClient")
  public RedissonClient secondaryRedisClient() {
    Config config = new Config();
    config
        .useSingleServer()
        .setAddress(REDIS + secondaryRedisHost + ":" + secondaryRedisPort)
        .setConnectionPoolSize(10)
        .setConnectionMinimumIdleSize(1);

    return Redisson.create(config);
  }
}
