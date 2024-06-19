package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlComponents {
    private String protocol;
    private String host;
    private int port;
    private String path;
    private String query;
    private String fragment;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://").append(host);
        if (port != -1 && port != 80 && port != 443) {
            sb.append(":").append(port);
        }
        sb.append(path);
        if (query != null && !query.isEmpty()) {
            sb.append("?").append(query);
        }
        if (fragment != null && !fragment.isEmpty()) {
            sb.append("#").append(fragment);
        }
        return sb.toString();
    }
}