package com.example.demo.service;

import com.example.demo.domain.dto.UrlStatsDTO;
import com.example.demo.repository.StatsRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StatsDbService {
  private final StatsRepository statsRepository;

  public List<UrlStatsDTO> getUrlStats() {
    List<UrlStatsDTO> rawStats = statsRepository.getUrlStats();
    return rawStats.stream()
        .map(obj -> new UrlStatsDTO(obj.getShortUrl(), obj.getLongUrl(), obj.getQuantity()))
        .toList();
  }
}