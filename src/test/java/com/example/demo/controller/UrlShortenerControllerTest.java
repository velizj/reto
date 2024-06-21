package com.example.demo.controller;

import static com.example.demo.util.UrlUtil.parseUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.UrlComponents;
import com.example.demo.dto.UrlStatsDTO;
import com.example.demo.dto.request.DeleteShortUrlsRequest;
import com.example.demo.dto.request.LongUrlRequest;
import com.example.demo.dto.request.ShortUrlRequest;
import com.example.demo.dto.response.LongUrlResponse;
import com.example.demo.dto.response.ShortUrlResponse;
import com.example.demo.service.StatsDbService;
import com.example.demo.service.UrlHandlerService;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UrlShortenerControllerTest {
  public static final String LONG_URL = "http://example.com";
  public static final String SHORT_URL = "http://short.url/abc";
  @Mock private UrlHandlerService urlHandlerService;
  @Mock private StatsDbService statsDbService;

  @InjectMocks private UrlShortenerController urlShortenerController;

  @Test
  void shouldReturnShortenUrl() {
    LongUrlRequest request = LongUrlRequest.builder().longUrl(LONG_URL).build();
    ShortUrlResponse response = ShortUrlResponse.builder().shortUrl(SHORT_URL).build();
    when(urlHandlerService.shortenUrl(anyString())).thenReturn(response);
    ResponseEntity<ShortUrlResponse> result = urlShortenerController.shortenUrl(request);
    assertEquals(ResponseEntity.ok(response), result);
    verify(urlHandlerService, times(1)).shortenUrl(LONG_URL);
  }

  @Test
  void shouldReturnExpandedUrl() throws MalformedURLException {
    ShortUrlRequest request = ShortUrlRequest.builder().shortUrl(SHORT_URL).build();
    UrlComponents urlComponents = parseUrl(request.getShortUrl());
    LongUrlResponse response = LongUrlResponse.builder().longUrl(LONG_URL).build();
    when(urlHandlerService.expandUrl(any(UrlComponents.class))).thenReturn(response);
    ResponseEntity<LongUrlResponse> result = urlShortenerController.expandUrl(request);
    assertEquals(ResponseEntity.ok(response), result);
    verify(urlHandlerService, times(1)).expandUrl(urlComponents);
  }

  @Test
  void shouldReturnRedirectToLongUrl() {
    String idShortUrl = "abc123";
    when(urlHandlerService.expandUrl(idShortUrl)).thenReturn(LONG_URL);
    ResponseEntity<Void> result = urlShortenerController.redirectToLongUrl(idShortUrl);
    assertEquals(HttpStatus.FOUND, result.getStatusCode());
    assertEquals(LONG_URL, result.getHeaders().getLocation().toString());
    verify(urlHandlerService, times(1)).expandUrl(idShortUrl);
  }

  @Test
  void shouldReturnDeleteMultipleUrls() {
    DeleteShortUrlsRequest request =
        DeleteShortUrlsRequest.builder().shortUrls(List.of("abc", "def")).build();
    doNothing().when(urlHandlerService).deleteUrls(request.getShortUrls());
    ResponseEntity<Void> result = urlShortenerController.deleteMultipleUrls(request);
    assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    verify(urlHandlerService, times(1)).deleteUrls(request.getShortUrls());
  }

  @Test
  void shouldReturnGetStatisticsEmptyList() {
    when(statsDbService.getUrlStats()).thenReturn(Collections.emptyList());
    ResponseEntity<List<UrlStatsDTO>> result = urlShortenerController.getStatistics();
    assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    verify(statsDbService, times(1)).getUrlStats();
  }

  @Test
  void shouldReturnGetStatisticsNonEmptyList() {
    UrlStatsDTO urlStatsDTO =
        UrlStatsDTO.builder().longUrl(LONG_URL).shortUrl(SHORT_URL).quantity(5).build();
    List<UrlStatsDTO> stats = List.of(urlStatsDTO);
    when(statsDbService.getUrlStats()).thenReturn(stats);
    ResponseEntity<List<UrlStatsDTO>> result = urlShortenerController.getStatistics();
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(stats, result.getBody());
    verify(statsDbService, times(1)).getUrlStats();
  }
}
