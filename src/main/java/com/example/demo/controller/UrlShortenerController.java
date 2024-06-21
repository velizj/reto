package com.example.demo.controller;

import static com.example.demo.util.UrlUtil.parseUrl;
import static com.example.demo.util.UrlUtil.validUrlList;
import static com.example.demo.util.UrlUtil.validateShortUrl;

import com.example.demo.dto.request.DeleteShortUrlsRequest;
import com.example.demo.dto.request.LongUrlRequest;
import com.example.demo.dto.response.LongUrlResponse;
import com.example.demo.dto.request.ShortUrlRequest;
import com.example.demo.dto.response.ShortUrlResponse;
import com.example.demo.dto.UrlComponents;
import com.example.demo.dto.UrlStatsDTO;
import com.example.demo.service.StatsDbService;
import com.example.demo.service.UrlHandlerService;
import jakarta.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Validated
public class UrlShortenerController implements UrlShortenerDefintionController {
  private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
  private static final String REQUEST_TO_EXPAND_URL_RECEIVED =
      "Request to expand URL received: {} ";
  private static final String HEADER_LOCATION = "Location";

  private final UrlHandlerService urlHandlerService;
  private final StatsDbService statsDbService;

  @Override
  @PostMapping(value = "/shorten", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ShortUrlResponse> shortenUrl(
      @RequestBody @Valid LongUrlRequest longUrlRequest) {
    String longUrl = longUrlRequest.getLongUrl();
    return ResponseEntity.ok(urlHandlerService.shortenUrl(longUrl));
  }

  @Override
  @PostMapping(value = "/expand", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LongUrlResponse> expandUrl(@RequestBody ShortUrlRequest shortUrlRequest)
      throws MalformedURLException {
    UrlComponents shortUrl = parseUrl(shortUrlRequest.getShortUrl());
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
    validUrlList(deleteShortUrlsRequest.getShortUrls());
    urlHandlerService.deleteUrls(deleteShortUrlsRequest.getShortUrls());
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UrlStatsDTO>> getStatistics() {
    List<UrlStatsDTO> stats = statsDbService.getUrlStats();
    if (stats.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(stats);
  }
}
