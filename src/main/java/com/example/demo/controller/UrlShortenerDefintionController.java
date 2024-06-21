package com.example.demo.controller;

import com.example.demo.domain.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface UrlShortenerDefintionController {

  @Operation(
      description = "Converts a long URL into a short URL",
      method = "shortenUrl",
      summary = "Shortens a URL",
      tags = {"shortener"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ShortUrlResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
        @ApiResponse(responseCode = "404", description = "Url not found", content = @Content)
      })
  ResponseEntity<ShortUrlResponse> shortenUrl(@RequestBody @Valid LongUrlRequest longUrlRequest);

  @Operation(
      description = "Expand a short URL into its original URL",
      method = "expandUrl",
      summary = "expand a URL",
      tags = {"expander"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = LongUrlResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
        @ApiResponse(responseCode = "404", description = "Url not found", content = @Content)
      })
  ResponseEntity<LongUrlResponse> expandUrl(@RequestBody ShortUrlRequest shortUrlRequest)
      throws MalformedURLException;

  @Operation(
      description = "Redirect to original long URL from a short URL",
      method = "redirectToLongUrl",
      summary = "redirect to a long URL",
      tags = {"redirector"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content()),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
        @ApiResponse(responseCode = "404", description = "Url not found", content = @Content)
      })
  ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortUrl);

  @Operation(
      description = "Delete multiple short URLs from the list",
      method = "deleteMultipleUrls",
      summary = "delete multiple short URLs",
      tags = {"deleter"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content()),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
        @ApiResponse(responseCode = "404", description = "Url not found", content = @Content)
      })
  ResponseEntity<Void> deleteMultipleUrls(
      @RequestBody DeleteShortUrlsRequest deleteShortUrlsRequest);

  @Operation(
      description = "Get statistics for all long and short URLs used in the system",
      method = "getStatistics",
      summary = "get statistics for all URLs",
      tags = {"stats"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = UrlStatsDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
        @ApiResponse(responseCode = "404", description = "Url not found", content = @Content)
      })
  ResponseEntity<List<UrlStatsDTO>> getStatistics();
}
