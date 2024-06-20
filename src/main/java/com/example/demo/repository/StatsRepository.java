package com.example.demo.repository;

import com.example.demo.domain.dto.StatsRecord;
import com.example.demo.domain.dto.UrlStatsDTO;
import com.example.demo.util.DateTimeUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class StatsRepository {

  private static final Logger logger = LoggerFactory.getLogger(StatsRepository.class);

  private final JdbcTemplate jdbcTemplate;

  private final DataSource dataSource;

  public List<UrlStatsDTO> getUrlStats() {
    String sql = "SELECT long_url, short_url, COUNT(*) AS quantity FROM UrlAccessLogs GROUP BY long_url, short_url ORDER BY quantity DESC";
    List<Object[]> rawStats = jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("long_url"),
            rs.getString("short_url"),
            rs.getLong("quantity")
    });

    return rawStats.stream()
            .map(obj -> new UrlStatsDTO(
                    (String) obj[0],
                    (String) obj[1],
                    (Long) obj[2]))
            .collect(Collectors.toList());
  }

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
