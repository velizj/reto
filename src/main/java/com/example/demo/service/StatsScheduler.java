package com.example.demo.service;

import com.example.demo.dto.StatsRecord;
import com.example.demo.repository.StatsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class StatsScheduler {

    private final RedissonClient secondaryRedisClient;
    private final StatsRepository statsRepository;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public StatsScheduler(
            @Qualifier("secondaryRedisClient") RedissonClient secondaryRedisClient,
            StatsRepository statsRepository
    ) {
        this.secondaryRedisClient = secondaryRedisClient;
        this.statsRepository = statsRepository;
        this.objectMapper = new ObjectMapper();
    }
    @PostConstruct
    public void start() {
        scheduler.scheduleAtFixedRate(this::flushStatsToDB, 0, 1, TimeUnit.MINUTES);
    }

    private void flushStatsToDB() {
        RMap<String, String> statsMap = secondaryRedisClient.getMap("urlStats");
        Map<String, String> stats = statsMap.readAllMap();
        statsMap.clear();

        for (Map.Entry<String, String> entry : stats.entrySet()) {
            String statJson = entry.getValue();
            try {
                StatsRecord statRecord = objectMapper.readValue(statJson, StatsRecord.class);
                statsRepository.saveStatToDatabase(statRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}