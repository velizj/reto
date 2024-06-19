package com.example.demo.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.sql.DataSource;

import com.example.demo.dto.StatsRecord;
import com.example.demo.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class StatsRepository {

  private static final Logger logger = LoggerFactory.getLogger(StatsRepository.class);

  private final DataSource dataSource;

  public void saveStatToDatabase(StatsRecord statRecord) {
    LocalDateTime accessTime = DateTimeUtils.convertTimestampToLocalDateTime(statRecord.getTimestamp());
    String formattedAccessTime = DateTimeUtils.formatLocalDateTime(accessTime);
    String sql = "INSERT INTO UrlAccessLogs (short_url, long_url, access_time) VALUES (?, ?, ?)";

    logger.info("Saving stat to database: {}", statRecord);

    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, statRecord.getShortUrl());
      stmt.setString(2, statRecord.getLongUrl());
      stmt.setString(3, formattedAccessTime);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error saving stat to database: {}", e.getMessage());
    }
  }
}
