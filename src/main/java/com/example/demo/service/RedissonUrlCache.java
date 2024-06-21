package com.example.demo.service;

import java.util.concurrent.TimeUnit;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RedissonUrlCache implements UrlCache {

  private static final String URL_CACHE_MAP = "urlCacheMap";
  private static final String SHORT_URL_PREFIX = "shortUrl:";
  private static final String LONG_URL_PREFIX = "longUrl:";
  private final RMapCache<String, String> cache;

  public RedissonUrlCache(@Qualifier("primaryRedisClient") RedissonClient redissonClient) {
    this.cache = redissonClient.getMapCache(URL_CACHE_MAP);
  }

  @Override
  public void save(String shortUrl, String longUrl) {
    cache.put(SHORT_URL_PREFIX + shortUrl, longUrl);
    cache.put(LONG_URL_PREFIX + longUrl, shortUrl);
  }

  @Override
  public void save(String shortUrl, String longUrl, long ttl, TimeUnit timeUnit) {
    cache.put(SHORT_URL_PREFIX + shortUrl, longUrl, ttl, timeUnit);
    cache.put(LONG_URL_PREFIX + longUrl, shortUrl, ttl, timeUnit);
  }

  @Override
  public String getLongUrl(String shortUrl) {
    return cache.get(SHORT_URL_PREFIX + shortUrl);
  }

  @Override
  public String getShortUrl(String longUrl) {
    return cache.get(LONG_URL_PREFIX + longUrl);
  }

  @Override
  public boolean delete(String shortUrl) {
    String longUrl = cache.get(SHORT_URL_PREFIX + shortUrl);
    if (longUrl != null) {
      cache.remove(SHORT_URL_PREFIX + shortUrl);
      cache.remove(LONG_URL_PREFIX + longUrl);
      return true;
    }
    return false;
  }

  @Override
  public boolean exists(String shortUrl) {
    return cache.containsKey(SHORT_URL_PREFIX + shortUrl);
  }
}
