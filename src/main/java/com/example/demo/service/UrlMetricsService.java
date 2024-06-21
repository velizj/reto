package com.example.demo.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlMetricsService {

  private static final String URL_REQUESTS = "url.requests";
  private static final String URL = "url";
  private final MeterRegistry meterRegistry;

  @Autowired
  public UrlMetricsService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  public void incrementUrlCounter(String url) {
    meterRegistry.counter(URL_REQUESTS, URL, url).increment();
  }
}
