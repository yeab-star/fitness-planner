package fitnessplanner;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ChallengeManager {
    
    // Daily Challenge Types
    public enum ChallengeType {
        EXERCISE, NUTRITION, HYDRATION, SLEEP, STEPS, CALORIES_BURN
    }
    
    // Challenge difficulty levels
    public enum Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
    
    // Initialize challenges table
    public void initializeTables() {
        createChallengesTable();
        createUserChallengesTable();
        createChallengeProgressTable();
    }
    
    private void createChallengesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS daily_challenges (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "challenge_type ENUM('EXERCISE', 'NUTRITION', 'HYDRATION', 'SLEEP', 'STEPS', 'CALORIES_BURN') NOT NULL," +
                    "difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL," +
                    "title VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "target_value DOUBLE," +
                    "unit VARCHAR(50)," +
                    "points INT DEFAULT 10," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            insertDefaultChallenges();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createUserChallengesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_daily_challenges (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "challenge_id INT NOT NULL," +
                    "date DATE NOT NULL," +
                    "target_value DOUBLE," +
                    "current_value DOUBLE DEFAULT 0," +
                    "completed BOOLEAN DEFAULT FALSE," +
                    "points_earned INT DEFAULT 0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE KEY unique_user_challenge_date (user_id, challenge_id, date)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (challenge_id) REFERENCES daily_challenges(id)" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createChallengeProgressTable() {
        String sql = "CREATE TABLE IF NOT EXISTS challenge_progress (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "date DATE NOT NULL," +
                    "total_challenges INT DEFAULT 0," +
                    "completed_challenges INT DEFAULT 0," +
                    "completion_percentage DOUBLE DEFAULT 0," +
                    "total_points INT DEFAULT 0," +
                    "UNIQUE KEY unique_user_date (user_id, date)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void insertDefaultChallenges() {
        String checkSql = "SELECT COUNT(*) FROM daily_challenges";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Challenges already exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        String sql = "INSERT INTO daily_challenges (challenge_type, difficulty, title, description, target_value, unit, points) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Exercise Challenges
            addChallenge(stmt, "EXERCISE", "BEGINNER", "🏃 Walk 5000 Steps", "Take a brisk walk and reach 5000 steps today", 5000, "steps", 15);
            addChallenge(stmt, "EXERCISE", "INTERMEDIATE", "🏋️ 30 Min Workout", "Complete a 30-minute workout session", 30, "minutes", 25);
            addChallenge(stmt, "EXERCISE", "ADVANCED", "🔥 Burn 400 Calories", "Burn 400 calories through exercise", 400, "calories", 35);
            
            // Nutrition Challenges
            addChallenge(stmt, "NUTRITION", "BEGINNER", "🥗 Eat 3 Servings of Vegetables", "Include vegetables in your meals", 3, "servings", 15);
            addChallenge(stmt, "NUTRITION", "INTERMEDIATE", "🍎 5 Fruits & Veggies", "Eat 5 portions of fruits and vegetables", 5, "portions", 20);
            addChallenge(stmt, "NUTRITION", "ADVANCED", "🥑 Complete Balanced Diet", "Follow your complete nutrition plan", 100, "percentage", 30);
            
            // Hydration Challenges
            addChallenge(stmt, "HYDRATION", "BEGINNER", "💧 Drink 6 Glasses of Water", "Stay hydrated with 6 glasses", 1500, "ml", 10);
            addChallenge(stmt, "HYDRATION", "INTERMEDIATE", "💦 Drink 8 Glasses of Water", "Reach optimal hydration", 2000, "ml", 15);
            addChallenge(stmt, "HYDRATION", "ADVANCED", "🌊 Drink 10 Glasses of Water", "Maximum hydration challenge", 2500, "ml", 20);
            
            // Sleep Challenges
            addChallenge(stmt, "SLEEP", "BEGINNER", "😴 Sleep 7 Hours", "Get quality rest for recovery", 7, "hours", 20);
            addChallenge(stmt, "SLEEP", "INTERMEDIATE", "🛌 Sleep 8 Hours", "Optimal sleep for performance", 8, "hours", 25);
            addChallenge(stmt, "SLEEP", "ADVANCED", "💤 Perfect Sleep Schedule", "Consistent 8+ hours sleep", 8.5, "hours", 30);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addChallenge(PreparedStatement stmt, String type, String difficulty, String title, String description, double target, String unit, int points) throws SQLException {
        stmt.setString(1, type);
        stmt.setString(2, difficulty);
        stmt.setString(3, title);
        stmt.setString(4, description);
        stmt.setDouble(5, target);
        stmt.setString(6, unit);
        stmt.setInt(7, points);
        stmt.executeUpdate();
    }
    
    // Generate daily challenges for user based on their fitness level
    public List<Map<String, Object>> generateDailyChallenges(int userId, String userGoal) {
        List<Map<String, Object>> challenges = new ArrayList<>();
        
        // Determine difficulty based on user goal
        String difficulty = getDifficultyForGoal(userGoal);
        
        String sql = "SELECT * FROM daily_challenges WHERE difficulty = ? ORDER BY RAND() LIMIT 4";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, difficulty);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> challenge = new HashMap<>();
                challenge.put("id", rs.getInt("id"));
                challenge.put("type", rs.getString("challenge_type"));
                challenge.put("title", rs.getString("title"));
                challenge.put("description", rs.getString("description"));
                challenge.put("target", rs.getDouble("target_value"));
                challenge.put("unit", rs.getString("unit"));
                challenge.put("points", rs.getInt("points"));
                challenge.put("current", 0.0);
                challenge.put("completed", false);
                challenges.add(challenge);
            }
            
            // Assign challenges to user for today
            assignChallengesToUser(userId, challenges);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return challenges;
    }
    
    private String getDifficultyForGoal(String goal) {
        if (goal == null) return "BEGINNER";
        
        switch (goal.toUpperCase()) {
            case "LOSE_WEIGHT":
            case "LOSE WEIGHT":
                return "INTERMEDIATE";
            case "GAIN_MUSCLE":
            case "GAIN MUSCLE":
                return "ADVANCED";
            default:
                return "BEGINNER";
        }
    }
    
    private void assignChallengesToUser(int userId, List<Map<String, Object>> challenges) {
        String sql = "INSERT INTO user_daily_challenges (user_id, challenge_id, date, target_value) " +
                    "VALUES (?, ?, CURDATE(), ?) " +
                    "ON DUPLICATE KEY UPDATE target_value = VALUES(target_value)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Map<String, Object> challenge : challenges) {
                stmt.setInt(1, userId);
                stmt.setInt(2, (Integer) challenge.get("id"));
                stmt.setDouble(3, (Double) challenge.get("target"));
                stmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Get today's challenges for user
    public List<Map<String, Object>> getTodaysChallenges(int userId) {
        List<Map<String, Object>> challenges = new ArrayList<>();
        
        String sql = "SELECT uc.*, dc.challenge_type, dc.title, dc.description, dc.unit, dc.points " +
                    "FROM user_daily_challenges uc " +
                    "JOIN daily_challenges dc ON uc.challenge_id = dc.id " +
                    "WHERE uc.user_id = ? AND uc.date = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> challenge = new HashMap<>();
                challenge.put("id", rs.getInt("challenge_id"));
                challenge.put("type", rs.getString("challenge_type"));
                challenge.put("title", rs.getString("title"));
                challenge.put("description", rs.getString("description"));
                challenge.put("target", rs.getDouble("target_value"));
                challenge.put("current", rs.getDouble("current_value"));
                challenge.put("unit", rs.getString("unit"));
                challenge.put("points", rs.getInt("points"));
                challenge.put("completed", rs.getBoolean("completed"));
                challenges.add(challenge);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return challenges;
    }
    
    // Update challenge progress when user inputs data
    public void updateChallengeProgress(int userId, String challengeType, double value) {
        String sql = "UPDATE user_daily_challenges uc " +
                    "JOIN daily_challenges dc ON uc.challenge_id = dc.id " +
                    "SET uc.current_value = ?, " +
                    "uc.completed = (? >= uc.target_value), " +
                    "uc.points_earned = CASE WHEN ? >= uc.target_value THEN dc.points ELSE 0 END " +
                    "WHERE uc.user_id = ? AND dc.challenge_type = ? AND uc.date = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, value);
            stmt.setDouble(2, value);
            stmt.setDouble(3, value);
            stmt.setInt(4, userId);
            stmt.setString(5, challengeType);
            stmt.executeUpdate();
            
            // Update overall progress
            updateDailyProgress(userId);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Calculate and update daily progress percentage
    private void updateDailyProgress(int userId) {
        String sql = "INSERT INTO challenge_progress (user_id, date, total_challenges, completed_challenges, completion_percentage, total_points) " +
                    "SELECT ?, CURDATE(), " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN completed THEN 1 ELSE 0 END) as completed, " +
                    "ROUND((SUM(CASE WHEN completed THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) as percentage, " +
                    "SUM(points_earned) as points " +
                    "FROM user_daily_challenges " +
                    "WHERE user_id = ? AND date = CURDATE() " +
                    "ON DUPLICATE KEY UPDATE " +
                    "total_challenges = VALUES(total_challenges), " +
                    "completed_challenges = VALUES(completed_challenges), " +
                    "completion_percentage = VALUES(completion_percentage), " +
                    "total_points = VALUES(total_points)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Get challenge completion data for graphs
    public Map<String, Double> getChallengeProgressData(int userId, String period) {
        Map<String, Double> data = new LinkedHashMap<>();
        
        try {
            // Try to use the new DailyChallengeService first
            DailyChallengeService dailyService = new DailyChallengeService();
            
            if ("daily".equals(period.toLowerCase())) {
                // Get last 7 days of data
                java.time.LocalDate today = java.time.LocalDate.now();
                for (int i = 6; i >= 0; i--) {
                    java.time.LocalDate date = today.minusDays(i);
                    try {
                        DailyChallengeData dayData = dailyService.getDailyChallengeData(userId, date);
                        data.put(date.toString(), dayData.getCompletionPercentage());
                    } catch (Exception e) {
                        // If data doesn't exist, put 0
                        data.put(date.toString(), 0.0);
                    }
                }
                return data;
            }
            
        } catch (Exception e) {
            System.err.println("Error accessing new daily challenge service, using fallback data: " + e.getMessage());
        }
        
        // Fallback: Return sample data if database tables don't exist
        return generateSampleProgressData(period);
    }
    
    // Generate sample progress data for demonstration
    private Map<String, Double> generateSampleProgressData(String period) {
        Map<String, Double> data = new LinkedHashMap<>();
        
        switch (period.toLowerCase()) {
            case "daily":
                // Last 7 days sample data
                java.time.LocalDate today = java.time.LocalDate.now();
                for (int i = 6; i >= 0; i--) {
                    java.time.LocalDate date = today.minusDays(i);
                    // Generate sample progress (60-90%)
                    double progress = 60 + (Math.random() * 30);
                    data.put(date.toString(), progress);
                }
                break;
                
            case "weekly":
                // Last 8 weeks sample data
                for (int i = 1; i <= 8; i++) {
                    double progress = 50 + (Math.random() * 40);
                    data.put("Week " + i, progress);
                }
                break;
                
            case "monthly":
                // Last 6 months sample data
                String[] months = {"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                for (String month : months) {
                    double progress = 55 + (Math.random() * 35);
                    data.put(month, progress);
                }
                break;
        }
        
        return data;
    }
    
    // Get total points earned
    public int getTotalPoints(int userId) {
        String sql = "SELECT SUM(total_points) as total FROM challenge_progress WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}