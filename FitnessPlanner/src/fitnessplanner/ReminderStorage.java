package fitnessplanner;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class ReminderStorage {
    
    public void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS reminders (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "reminder_type VARCHAR(50) NOT NULL," +
                    "reminder_time VARCHAR(10) NOT NULL," +
                    "reminder_message VARCHAR(255) NOT NULL," +
                    "enabled BOOLEAN DEFAULT 1," +
                    "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Reminders table initialized successfully");
        } catch (SQLException e) {
            System.err.println("Error initializing reminders table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Save a new reminder
    public boolean saveReminder(int userId, String type, String time, String message) {
        String sql = "INSERT INTO reminders (user_id, reminder_type, reminder_time, reminder_message, enabled) VALUES (?, ?, ?, ?, 1)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, type);
            stmt.setString(3, time);
            stmt.setString(4, message);
            
            stmt.executeUpdate();
            System.out.println("Reminder saved successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error saving reminder: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all reminders for a user
    public List<Map<String, Object>> getUserReminders(int userId) {
        List<Map<String, Object>> reminders = new ArrayList<>();
        String sql = "SELECT id, reminder_type, reminder_time, reminder_message, enabled FROM reminders WHERE user_id = ? ORDER BY reminder_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> reminder = new HashMap<>();
                reminder.put("id", rs.getInt("id"));
                reminder.put("type", rs.getString("reminder_type"));
                reminder.put("time", rs.getString("reminder_time"));
                reminder.put("message", rs.getString("reminder_message"));
                reminder.put("enabled", rs.getBoolean("enabled"));
                reminders.add(reminder);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving reminders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reminders;
    }
    
    // Update reminder enabled status
    public boolean updateReminderStatus(int reminderId, boolean enabled) {
        String sql = "UPDATE reminders SET enabled = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, enabled);
            stmt.setInt(2, reminderId);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error updating reminder status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete a reminder
    public boolean deleteReminder(int reminderId) {
        String sql = "DELETE FROM reminders WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reminderId);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error deleting reminder: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get enabled reminders for a user
    public List<Map<String, Object>> getEnabledReminders(int userId) {
        List<Map<String, Object>> reminders = new ArrayList<>();
        String sql = "SELECT id, reminder_type, reminder_time, reminder_message FROM reminders WHERE user_id = ? AND enabled = 1 ORDER BY reminder_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> reminder = new HashMap<>();
                reminder.put("id", rs.getInt("id"));
                reminder.put("type", rs.getString("reminder_type"));
                reminder.put("time", rs.getString("reminder_time"));
                reminder.put("message", rs.getString("reminder_message"));
                reminders.add(reminder);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving enabled reminders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reminders;
    }
}
