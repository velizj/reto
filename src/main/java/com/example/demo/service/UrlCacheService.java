package com.example.demo.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UrlCacheService {

  private final UrlCache urlCache;
  public void save(String shortUrl, String longUrl) {
    urlCache.save(shortUrl, longUrl);
  }

  public void save(String shortUrl, String longUrl, long ttl, TimeUnit timeUnit) {
    urlCache.save(shortUrl, longUrl, ttl, timeUnit);
  }

  public Optional<String> getLongUrl(String shortUrl) {
    return Optional.ofNullable(urlCache.getLongUrl(shortUrl));
  }

  public String getShortUrl(String longUrl) {
    return urlCache.getShortUrl(longUrl);
  }

  public boolean delete(String shortUrl) {
    return urlCache.delete(shortUrl);
  }

  public boolean exists(String shortUrl) {
    return urlCache.exists(shortUrl);
  }
}
