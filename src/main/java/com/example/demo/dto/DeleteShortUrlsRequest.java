package com.example.demo.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteShortUrlsRequest {
    private List<String> shortUrls;

}