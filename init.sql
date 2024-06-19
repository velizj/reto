CREATE DATABASE IF NOT EXISTS UrlShortenerStats;

USE UrlShortenerStats;

CREATE TABLE IF NOT EXISTS UrlAccessLogs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    long_url VARCHAR(255) NOT NULL,
    short_url VARCHAR(255) NOT NULL,
    access_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_long_url ON UrlAccessLogs(long_url);
CREATE INDEX idx_short_url ON UrlAccessLogs(short_url);

INSERT INTO UrlAccessLogs (long_url, short_url) VALUES
('https://www.example.com/very/long/url/1', 'short1'),
('https://www.example.com/very/long/url/2', 'short2'),
('https://www.example.com/very/long/url/3', 'short3');