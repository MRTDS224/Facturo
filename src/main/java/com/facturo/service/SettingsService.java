package com.facturo.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsService {

    public String getSetting(String key, String defaultValue) {
        String sql = "SELECT value FROM settings WHERE key = ?";
        try (Connection conn = DatabaseService.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting setting '" + key + "': " + e.getMessage());
        }
        return defaultValue;
    }

    public void saveSetting(String key, String value) {
        // Insert or replace generic SQlite approach (UPSERT pattern for 3.24.0+)
        String sql = "INSERT INTO settings(key, value) VALUES(?, ?) " +
                "ON CONFLICT(key) DO UPDATE SET value=excluded.value";
        try (Connection conn = DatabaseService.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving setting '" + key + "': " + e.getMessage());
        }
    }
}
