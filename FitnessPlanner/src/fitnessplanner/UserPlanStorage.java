package fitnessplanner;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class UserPlanStorage {
    
    public void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_plans (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "plan_name VARCHAR(255) NOT NULL," +
                    "plan_time VARCHAR(50) NOT NULL," +
                    "completed BOOLEAN DEFAULT 0," +
                    "date DATE NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User plans table initialized successfully");
        } catch (SQLException e) {
            System.err.println("Error initializing user plans table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Save a new plan
    public boolean savePlan(int userId, String planName, String planTime, LocalDate date) {
        String sql = "INSERT INTO user_plans (user_id, plan_name, plan_time, completed, date) VALUES (?, ?, ?, 0, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, planName);
            stmt.setString(3, planTime);
            stmt.setDate(4, java.sql.Date.valueOf(date));
            
            stmt.executeUpdate();
            System.out.println("Plan saved successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error saving plan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get today's plans for a user
    public List<Map<String, Object>> getTodaysPlans(int userId, LocalDate date) {
        List<Map<String, Object>> plans = new ArrayList<>();
        String sql = "SELECT id, plan_name, plan_time, completed FROM user_plans WHERE user_id = ? AND date = ? ORDER BY plan_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> plan = new HashMap<>();
                plan.put("id", rs.getInt("id"));
                plan.put("name", rs.getString("plan_name"));
                plan.put("time", rs.getString("plan_time"));
                plan.put("completed", rs.getBoolean("completed"));
                plans.add(plan);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving plans: " + e.getMessage());
            e.printStackTrace();
        }
        
        return plans;
    }
    
    // Update plan completion status
    public boolean updatePlanCompletion(int planId, boolean completed) {
        String sql = "UPDATE user_plans SET completed = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, completed);
            stmt.setInt(2, planId);
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error updating plan completion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete a plan
    public boolean deletePlan(int planId) {
        String sql = "DELETE FROM user_plans WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, planId);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error deleting plan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get unfinished plans count
    public int getUnfinishedPlansCount(int userId, LocalDate date) {
        String sql = "SELECT COUNT(*) as count FROM user_plans WHERE user_id = ? AND date = ? AND completed = 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting unfinished plans: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Get unfinished plan names
    public List<String> getUnfinishedPlanNames(int userId, LocalDate date) {
        List<String> planNames = new ArrayList<>();
        String sql = "SELECT plan_name, plan_time FROM user_plans WHERE user_id = ? AND date = ? AND completed = 0 ORDER BY plan_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                planNames.add(rs.getString("plan_time") + " - " + rs.getString("plan_name"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving unfinished plans: " + e.getMessage());
            e.printStackTrace();
        }
        
        return planNames;
    }
}
