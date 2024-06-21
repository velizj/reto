package com.example.demo.service;

import com.example.demo.dto.StatsRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class StatsCacheService {
  private static final String URL_STATS = "urlStats";
  private final RedissonClient secondaryRedisClient;
  private final ObjectMapper objectMapper;
  public StatsCacheService(@Qualifier("secondaryRedisClient") RedissonClient secondaryRedisClient) {
    this.secondaryRedisClient = secondaryRedisClient;
    this.objectMapper = new ObjectMapper();
  }
  public void recordStatistics(String shortUrl, String longUrl) {
    StatsRecord statRecord = new StatsRecord(shortUrl, longUrl, System.currentTimeMillis());
    try {
      String statJson = objectMapper.writeValueAsString(statRecord);
      String uuid = UUID.randomUUID().toString();
      secondaryRedisClient.getMap(URL_STATS).put(uuid, statJson);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
