package com.example.demo.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LongUrlResponse {
    private String longUrl;
}