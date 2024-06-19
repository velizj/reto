package com.example.demo.service;

import java.util.concurrent.TimeUnit;

public interface UrlCache {
    void save(String shortUrl, String longUrl);
    void save(String shortUrl, String longUrl, long ttl, TimeUnit timeUnit);
    String getLongUrl(String shortUrl);
    String getShortUrl(String longUrl);
    boolean delete(String shortUrl);
    boolean exists(String shortUrl);
}