package fitnessplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    
    public UserMetrics getTodaysMetrics(int userId) {
        String query = "SELECT water_ml, calories, sleep_hours FROM user_metrics WHERE user_id = ? AND date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserMetrics metrics = new UserMetrics();
                metrics.setWaterMl(rs.getInt("water_ml"));
                metrics.setCalories(rs.getInt("calories"));
                metrics.setSleepHours(rs.getDouble("sleep_hours"));
                return metrics;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UserMetrics();
    }
    
    public boolean saveTodaysMetrics(int userId, UserMetrics metrics) {
        String checkQuery = "SELECT id FROM user_metrics WHERE user_id = ? AND date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                String updateQuery = "UPDATE user_metrics SET water_ml = ?, calories = ?, sleep_hours = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, metrics.getWaterMl());
                    updateStmt.setInt(2, metrics.getCalories());
                    updateStmt.setDouble(3, metrics.getSleepHours());
                    updateStmt.setInt(4, rs.getInt("id"));
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                String insertQuery = "INSERT INTO user_metrics (user_id, water_ml, calories, sleep_hours) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, metrics.getWaterMl());
                    insertStmt.setInt(3, metrics.getCalories());
                    insertStmt.setDouble(4, metrics.getSleepHours());
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Map<String, Double> getWeeklySleepData(int userId) {
        Map<String, Double> sleepData = new HashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        for (String day : days) {
            sleepData.put(day, 0.0);
        }
        
        String query = "SELECT DAYNAME(date) as day_name, AVG(hours_slept) as avg_hours " +
                      "FROM sleep_logs " +
                      "WHERE user_id = ? AND date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                      "GROUP BY DAYNAME(date) ORDER BY FIELD(DAYNAME(date), 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String dayName = rs.getString("day_name");
                double avgHours = rs.getDouble("avg_hours");
                sleepData.put(dayName, avgHours);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sleepData;
    }
    
    public double getLatestWeight(int userId) {
        String query = "SELECT weight FROM weight_logs WHERE user_id = ? ORDER BY log_date DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("weight");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 70.0; // Default fallback
    }
    
    public boolean saveWeightLog(int userId, double weight) {
        String query = "INSERT INTO weight_logs (user_id, weight) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, weight);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}