package com.example.demo.util;

import com.example.demo.dto.UrlComponents;
import com.example.demo.exception.InvalidUrlException;
import lombok.experimental.UtilityClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class UrlUtil {
  private static final int BEGIN_INDEX = 0;
  private static final int END_INDEX = 6;
  private static final String SHORT_URL_PATTERN = "^[a-zA-Z0-9]{6}$";
  private static final String INVALID_SHORT_URL_FORMAT = "Invalid short URL format";

  public static UrlComponents parseUrl(String urlStr) throws MalformedURLException {
    URL url = new URL(urlStr);
    int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
    return UrlComponents.builder()
        .protocol(url.getProtocol())
        .host(url.getHost())
        .port(port)
        .path(url.getPath())
        .query(url.getQuery())
        .fragment(url.getRef())
        .build();
  }

  public static void validateShortUrl(String shortUrl) {
    if (!shortUrl.matches(SHORT_URL_PATTERN)) {
      throw new InvalidUrlException(INVALID_SHORT_URL_FORMAT);
    }
  }

  public static void validUrlList(List<String> urlList) {
    if (urlList == null || urlList.isEmpty()) {
      throw new InvalidUrlException("No short URLs provided");
    }
  }

  public static String generateNewShortUrl() {
    return UUID.randomUUID().toString().substring(BEGIN_INDEX, END_INDEX);
  }

  public static String cleanPath(String path) {
    return path.replaceAll("^/|/$", "");
  }
}
