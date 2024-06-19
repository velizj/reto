package com.example.demo.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlMetricsService {

    private final MeterRegistry meterRegistry;

    @Autowired
    public UrlMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementUrlCounter(String url) {
        meterRegistry.counter("url.requests", "url", url).increment();
    }
}