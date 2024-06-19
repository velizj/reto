package com.example.demo.util;


import com.example.demo.dto.LongUrlResponse;
import com.example.demo.dto.ShortUrlResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class BuilderUtil {

    public static ShortUrlResponse buildShortUrlResponse(String shortUrl) {
      return ShortUrlResponse.builder().shortUrl(shortUrl).build();
    }

    public static ResponseEntity<LongUrlResponse> buildLongUrlResponse(String longUrl) {
        LongUrlResponse response = LongUrlResponse.builder().longUrl(longUrl).build();
        return ResponseEntity.ok(response);
    }
}