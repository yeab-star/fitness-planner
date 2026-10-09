package fitnessplanner;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

public class ProgressTracker {
    
    // Save daily progress (called automatically when user inputs data)
    public boolean saveDailyProgress(int userId, double weight, int calories, int waterMl, double sleepHours) {
        String query = "INSERT INTO daily_progress (user_id, date, weight, calories, water_ml, sleep_hours) " +
                      "VALUES (?, CURDATE(), ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE weight = ?, calories = ?, water_ml = ?, sleep_hours = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, weight);
            stmt.setInt(3, calories);
            stmt.setInt(4, waterMl);
            stmt.setDouble(5, sleepHours);
            stmt.setDouble(6, weight);
            stmt.setInt(7, calories);
            stmt.setInt(8, waterMl);
            stmt.setDouble(9, sleepHours);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get daily progress for last 7 days
    public Map<String, Double> getDailyProgress(int userId, String metric) {
        Map<String, Double> data = new LinkedHashMap<>();
        String query = "SELECT date, " + getMetricColumn(metric) + " as value " +
                      "FROM daily_progress " +
                      "WHERE user_id = ? AND date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                      "ORDER BY date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String date = rs.getDate("date").toString();
                double value = rs.getDouble("value");
                data.put(date, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return data;
    }
    
    // Get weekly progress (aggregated from daily data)
    public Map<String, Double> getWeeklyProgress(int userId, String metric) {
        Map<String, Double> data = new LinkedHashMap<>();
        String query = "SELECT YEARWEEK(date) as week, AVG(" + getMetricColumn(metric) + ") as avg_value " +
                      "FROM daily_progress " +
                      "WHERE user_id = ? AND date >= DATE_SUB(CURDATE(), INTERVAL 8 WEEK) " +
                      "GROUP BY YEARWEEK(date) " +
                      "ORDER BY week ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            int weekNum = 1;
            while (rs.next()) {
                double avgValue = rs.getDouble("avg_value");
                data.put("Week " + weekNum++, avgValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return data;
    }
    
    // Get monthly progress (aggregated from weekly data)
    public Map<String, Double> getMonthlyProgress(int userId, String metric) {
        Map<String, Double> data = new LinkedHashMap<>();
        String query = "SELECT DATE_FORMAT(date, '%Y-%m') as month, " +
                      "DATE_FORMAT(date, '%b') as month_name, " +
                      "AVG(" + getMetricColumn(metric) + ") as avg_value " +
                      "FROM daily_progress " +
                      "WHERE user_id = ? AND date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                      "GROUP BY DATE_FORMAT(date, '%Y-%m') " +
                      "ORDER BY month ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String monthName = rs.getString("month_name");
                double avgValue = rs.getDouble("avg_value");
                data.put(monthName, avgValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return data;
    }
    
    private String getMetricColumn(String metric) {
        switch (metric.toLowerCase()) {
            case "weight": return "weight";
            case "calories": return "calories";
            case "water": return "water_ml";
            case "sleep": return "sleep_hours";
            default: return "weight";
        }
    }
    
    // Create table if not exists
    public void initializeTable() {
        String createTable = "CREATE TABLE IF NOT EXISTS daily_progress (" +
                           "id INT AUTO_INCREMENT PRIMARY KEY," +
                           "user_id INT NOT NULL," +
                           "date DATE NOT NULL," +
                           "weight DOUBLE," +
                           "calories INT," +
                           "water_ml INT," +
                           "sleep_hours DOUBLE," +
                           "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                           "UNIQUE KEY unique_user_date (user_id, date)," +
                           "FOREIGN KEY (user_id) REFERENCES users(id)" +
                           ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
