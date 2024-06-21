package com.example.demo.service;

import static com.example.demo.util.UrlUtil.cleanPath;
import static com.example.demo.util.UrlUtil.generateNewShortUrl;
import static com.example.demo.util.UrlUtil.parseUrl;

import com.example.demo.dto.UrlComponents;
import com.example.demo.dto.response.LongUrlResponse;
import com.example.demo.dto.response.ShortUrlResponse;
import com.example.demo.exception.UrlNotFoundException;
import com.example.demo.exception.UrlShortenerException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UrlHandlerService {
  private static final String URL_BASE_MELI = "http://me.li:8080/";
  public static final String URL_NOT_FOUND = "URL not found: {}";
  public static final String ERROR_SHORTENING_URL = "Error shortening the URL: {}";
  private static final Logger logger = LoggerFactory.getLogger(UrlHandlerService.class);

  private final UrlMetricsService urlMetricsService;
  private final UrlCacheService urlCacheService;
  private final StatsCacheService statsCacheService;

  public ShortUrlResponse shortenUrl(String longUrl) {
    try {
      incrementUrlCounter(longUrl);
      String validatedLongUrl = validateUrl(longUrl);
      String shortUrl = resolveShortUrl(validatedLongUrl);
      registerStatistics(longUrl, shortUrl);
      return buildShortUrlResponse(shortUrl);
    } catch (Exception e) {
      throw new UrlShortenerException(ERROR_SHORTENING_URL + e.getMessage());
    }
  }

  public LongUrlResponse expandUrl(UrlComponents urlComponents) {
    String shortUrl = cleanPath(urlComponents.getPath());
    incrementUrlCounter(shortUrl);
    String longUrl = resolveLongUrl(shortUrl);
    return LongUrlResponse.builder().longUrl(longUrl).build();
  }

  public String expandUrl(String shortUrl) {
    return resolveLongUrl(shortUrl);
  }

  private String resolveLongUrl(String shortUrl) {
    return urlCacheService
        .getLongUrl(shortUrl)
        .orElseGet(
            () -> {
              logger.error(URL_NOT_FOUND, shortUrl);
              throw new UrlNotFoundException(URL_NOT_FOUND + URL_BASE_MELI + shortUrl);
            });
  }

  public void deleteUrls(List<String> shortUrls) {
    shortUrls.forEach(urlCacheService::delete);
  }

/*  public UrlComponents parseUrl(String url) throws MalformedURLException {
    return parseUrl(url);
  }*/

  private void registerStatistics(String longUrl, String shortUrl) {
    statsCacheService.recordStatistics(shortUrl, longUrl);
  }

  private String resolveShortUrl(String validatedLongUrl) {
    return URL_BASE_MELI
        + getCachedShortUrl(validatedLongUrl)
            .orElseGet(
                () -> {
                  String newShortUrl = generateNewShortUrl();
                  urlCacheService.save(newShortUrl, validatedLongUrl);
                  return newShortUrl;
                });
  }

  private void incrementUrlCounter(String longUrl) {
    urlMetricsService.incrementUrlCounter(longUrl);
  }

  public String validateUrl(String url) throws MalformedURLException {
    return parseUrl(url).toString();
  }

  private Optional<String> getCachedShortUrl(String longUrl) {
    return Optional.ofNullable(urlCacheService.getShortUrl(longUrl));
  }

  private ShortUrlResponse buildShortUrlResponse(String shortUrl) {
    return ShortUrlResponse.builder().shortUrl(shortUrl).build();
  }
}
