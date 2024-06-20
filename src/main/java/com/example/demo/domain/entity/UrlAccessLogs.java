package com.example.demo.domain.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "UrlAccessLogs")
public class UrlAccessLogs {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String longUrl;
  private String shortUrl;
  private Timestamp accessTime;
}
