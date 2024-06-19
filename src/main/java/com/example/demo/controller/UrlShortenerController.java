package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.StatsService;
import com.example.demo.service.UrlHandlerService;
import jakarta.validation.Valid;
import java.net.MalformedURLException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.util.UrlUtil.parseUrl;
import static com.example.demo.util.UrlUtil.validateShortUrl;

@RestController
@AllArgsConstructor
@Validated
public class UrlShortenerController implements UrlShortenerDefintionController {
  private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
  private static final String REQUEST_TO_SHORTEN_URL_RECEIVED =
      "Request to shorten URL received: {} ";
  private static final String REQUEST_TO_EXPAND_URL_RECEIVED =
      "Request to shorten URL received: {} ";
  private static final String HEADER_LOCATION = "Location";

  private final UrlHandlerService urlHandlerService;
  private final StatsService statsService;

  @Override
  @PostMapping(value = "/shorten", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ShortUrlResponse> shortenUrl(
      @RequestBody @Valid LongUrlRequest longUrlRequest) {
    String longUrl = longUrlRequest.getLongUrl();
    logger.info(REQUEST_TO_SHORTEN_URL_RECEIVED, longUrl);

    ShortUrlResponse response = urlHandlerService.shortenUrl(longUrl);

    statsService.recordStatistics(response.getShortUrl(), longUrl);

    return ResponseEntity.ok(response);
  }
  @Override
  @PostMapping(value = "/expand", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LongUrlResponse> expandUrl(@RequestBody ShortUrlRequest shortUrlRequest)
      throws MalformedURLException {
    UrlComponents shortUrl = parseUrl(shortUrlRequest.getShortUrl());
    logger.info(REQUEST_TO_EXPAND_URL_RECEIVED, shortUrlRequest.getShortUrl());
    return ResponseEntity.ok(urlHandlerService.expandUrl(shortUrl));
  }

  @Override
  @GetMapping("/{shortUrl}")
  public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortUrl) {
    validateShortUrl(shortUrl);
    String longUrl = urlHandlerService.expandUrl(shortUrl);
    return ResponseEntity.status(HttpStatus.FOUND).header(HEADER_LOCATION, longUrl).build();
  }

  @Override
  @DeleteMapping("/shorturls")
  public ResponseEntity<Void> deleteMultipleUrls(
      @RequestBody DeleteShortUrlsRequest deleteShortUrlsRequest) {
    urlHandlerService.deleteUrls(deleteShortUrlsRequest.getShortUrls());
    return ResponseEntity.noContent().build();
  }
}
