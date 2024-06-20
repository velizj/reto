package com.example.demo.service;


import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import com.example.demo.domain.dto.LongUrlResponse;
import com.example.demo.domain.dto.ShortUrlResponse;
import com.example.demo.domain.dto.UrlComponents;
import com.example.demo.exception.UrlShortenerException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.example.demo.util.BuilderUtil.buildShortUrlResponse;
import static com.example.demo.util.UrlUtil.*;

@Service
@AllArgsConstructor
public class UrlHandlerService {
  private static final String URL_BASE_MELI = "http://me.li:8080/";
  private static final Logger logger = LoggerFactory.getLogger(UrlHandlerService.class);
  public static final String URL_NOT_FOUND = "URL not found: {}";
  public static final String ERROR_SHORTENING_URL = "Error shortening the URL: {}";
  public static final String ERROR_EXPANDING_URL = "Error expanding the URL: {}";

  private final UrlMetricsService urlMetricsService;
  private final UrlCacheService urlCacheService;

  public ShortUrlResponse shortenUrl(String longUrl) {
    try {
      incrementUrlCounter(longUrl);
      String validatedLongUrl = validateAndParseUrl(longUrl);
      String shortUrl =
          URL_BASE_MELI
              + getCachedShortUrl(validatedLongUrl)
                  .orElseGet(() -> generateAndCacheShortUrl(validatedLongUrl));
      return buildShortUrlResponse(shortUrl);
    } catch (Exception e) {
      throw new UrlShortenerException(ERROR_SHORTENING_URL + e.getMessage());
    }
  }

  private void incrementUrlCounter(String longUrl) {
    urlMetricsService.incrementUrlCounter(longUrl);
  }

  private String validateAndParseUrl(String longUrl) throws MalformedURLException {
    return parseUrl(longUrl).toString();
  }

  private Optional<String> getCachedShortUrl(String longUrl) {
    return Optional.ofNullable(urlCacheService.getShortUrl(longUrl));
  }

  private String generateAndCacheShortUrl(String longUrl) {
    String shortUrl = generateShortUrl();
    urlCacheService.save(shortUrl, longUrl);
    return shortUrl;
  }

  public LongUrlResponse expandUrl(UrlComponents shortUrl) {
    try {
      // todo: validar shortUrl?
      String longUrl = urlCacheService.getLongUrl(cleanPath(shortUrl.getPath()));
      if (longUrl == null) {
        logger.error(URL_NOT_FOUND, shortUrl);
        throw new UrlShortenerException(URL_NOT_FOUND);
      }
      return LongUrlResponse.builder().longUrl(longUrl).build();
    } catch (Exception e) {
      throw new UrlShortenerException(ERROR_EXPANDING_URL + e.getMessage());
    }
  }

  public String expandUrl(String shortUrl) {
    try {
      // todo: validar shortUrl?
      String longUrl = urlCacheService.getLongUrl(shortUrl);
      if (longUrl == null) {
        throw new UrlShortenerException(URL_NOT_FOUND + URL_BASE_MELI + shortUrl);
      }
      return longUrl;
    } catch (Exception e) {
      throw new UrlShortenerException(ERROR_EXPANDING_URL + e.getMessage());
    }
  }

  public void deleteUrls(List<String> shortUrls) {
    shortUrls.forEach(urlCacheService::delete);
  }
}
