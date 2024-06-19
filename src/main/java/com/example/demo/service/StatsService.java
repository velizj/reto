package com.example.demo.service;

import com.example.demo.dto.StatsRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
//@AllArgsConstructor
public class StatsService {

  private final RedissonClient secondaryRedisClient;
  private final ObjectMapper objectMapper;

  public StatsService(@Qualifier("secondaryRedisClient") RedissonClient secondaryRedisClient) {
    this.secondaryRedisClient = secondaryRedisClient;
    this.objectMapper = new ObjectMapper();
  }

  public void recordStatistics(String shortUrl, String longUrl) {
    StatsRecord statRecord = new StatsRecord(shortUrl, longUrl, System.currentTimeMillis());
    try {
      String statJson = objectMapper.writeValueAsString(statRecord);
      String uuid = UUID.randomUUID().toString();
      secondaryRedisClient.getMap("urlStats").put(uuid, statJson);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
