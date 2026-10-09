package fitnessplanner;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class CheckboxChallengeStorage {
    
    // Initialize storage table for checkbox challenges
    public void initializeTables() {
        createCheckboxChallengeTable();
    }
    
    private void createCheckboxChallengeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS checkbox_challenges (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "date DATE NOT NULL," +
                    "category VARCHAR(50) NOT NULL," +
                    "challenge_item VARCHAR(255) NOT NULL," +
                    "completed BOOLEAN DEFAULT FALSE," +
                    "points_earned INT DEFAULT 0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "UNIQUE KEY unique_user_date_item (user_id, date, challenge_item)" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Checkbox challenges table created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating checkbox_challenges table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Save daily challenge completion status
    public void saveDailyChallengeStatus(int userId, LocalDate date, 
                                        Map<String, Boolean> exerciseStatus,
                                        Map<String, Boolean> nutritionStatus, 
                                        Map<String, Boolean> hydrationStatus,
                                        Map<String, Boolean> recoveryStatus) {
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Clear existing data for this date
            clearDayData(conn, userId, date);
            
            // Save exercise challenges
            saveCategoryData(conn, userId, date, "EXERCISE", exerciseStatus);
            
            // Save nutrition challenges  
            saveCategoryData(conn, userId, date, "NUTRITION", nutritionStatus);
            
            // Save hydration challenges
            saveCategoryData(conn, userId, date, "HYDRATION", hydrationStatus);
            
            // Save recovery challenges
            saveCategoryData(conn, userId, date, "RECOVERY", recoveryStatus);
            
            System.out.println("Daily challenge status saved for user " + userId + " on " + date);
            
        } catch (SQLException e) {
            System.err.println("Error saving daily challenge status: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void clearDayData(Connection conn, int userId, LocalDate date) throws SQLException {
        String sql = "DELETE FROM checkbox_challenges WHERE user_id = ? AND date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            stmt.executeUpdate();
        }
    }
    
    private void saveCategoryData(Connection conn, int userId, LocalDate date, String category, 
                                 Map<String, Boolean> statusMap) throws SQLException {
        
        String sql = "INSERT INTO checkbox_challenges (user_id, date, category, challenge_item, completed, points_earned) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, Boolean> entry : statusMap.entrySet()) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(date));
                stmt.setString(3, category);
                stmt.setString(4, entry.getKey());
                stmt.setBoolean(5, entry.getValue());
                
                // Calculate points based on completion
                int points = entry.getValue() ? getCategoryPoints(category) : 0;
                stmt.setInt(6, points);
                
                stmt.executeUpdate();
            }
        }
    }
    
    private int getCategoryPoints(String category) {
        switch (category) {
            case "EXERCISE": return 6; // 25 points / 4 items = ~6 points each
            case "NUTRITION": return 4; // 20 points / 5 items = 4 points each
            case "HYDRATION": return 4; // 15 points / 4 items = ~4 points each
            case "RECOVERY": return 5;  // 20 points / 4 items = 5 points each
            default: return 1;
        }
    }
    
    // Get daily challenge data for a specific date
    public Map<String, Map<String, Boolean>> getDailyChallengeStatus(int userId, LocalDate date) {
        Map<String, Map<String, Boolean>> result = new HashMap<>();
        result.put("EXERCISE", new HashMap<>());
        result.put("NUTRITION", new HashMap<>());
        result.put("HYDRATION", new HashMap<>());
        result.put("RECOVERY", new HashMap<>());
        
        String sql = "SELECT category, challenge_item, completed FROM checkbox_challenges " +
                    "WHERE user_id = ? AND date = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String category = rs.getString("category");
                String item = rs.getString("challenge_item");
                boolean completed = rs.getBoolean("completed");
                
                result.get(category).put(item, completed);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving daily challenge status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Update a single challenge item
    public void updateSingleChallenge(int userId, LocalDate date, String category, String challengeItem, boolean completed) {
        String checkSql = "SELECT id FROM checkbox_challenges WHERE user_id = ? AND date = ? AND category = ? AND challenge_item = ?";
        String updateSql = "UPDATE checkbox_challenges SET completed = ?, points_earned = ? WHERE user_id = ? AND date = ? AND category = ? AND challenge_item = ?";
        String insertSql = "INSERT INTO checkbox_challenges (user_id, date, category, challenge_item, completed, points_earned) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if record exists
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setDate(2, java.sql.Date.valueOf(date));
                checkStmt.setString(3, category);
                checkStmt.setString(4, challengeItem);
                ResultSet rs = checkStmt.executeQuery();
                exists = rs.next();
            }
            
            int points = completed ? getCategoryPoints(category) : 0;
            
            if (exists) {
                // Update existing record
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setBoolean(1, completed);
                    updateStmt.setInt(2, points);
                    updateStmt.setInt(3, userId);
                    updateStmt.setDate(4, java.sql.Date.valueOf(date));
                    updateStmt.setString(5, category);
                    updateStmt.setString(6, challengeItem);
                    updateStmt.executeUpdate();
                }
            } else {
                // Insert new record
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setDate(2, java.sql.Date.valueOf(date));
                    insertStmt.setString(3, category);
                    insertStmt.setString(4, challengeItem);
                    insertStmt.setBoolean(5, completed);
                    insertStmt.setInt(6, points);
                    insertStmt.executeUpdate();
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating single challenge: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Get weekly challenge summary (last 7 days)
    public List<WeeklyChallengeDay> getWeeklyChallengeData(int userId) {
        List<WeeklyChallengeDay> weeklyData = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            WeeklyChallengeDay dayData = getDayChallengeData(userId, date);
            weeklyData.add(dayData);
        }
        
        return weeklyData;
    }
    
    // Get challenge data for a specific date range
    public List<WeeklyChallengeDay> getDateRangeChallengeData(int userId, LocalDate startDate, LocalDate endDate) {
        List<WeeklyChallengeDay> rangeData = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            WeeklyChallengeDay dayData = getDayChallengeData(userId, date);
            rangeData.add(dayData);
        }
        
        return rangeData;
    }
    
    // Get total statistics for all time
    public Map<String, Object> getAllTimeStats(int userId) {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(DISTINCT date) as total_days, " +
                    "SUM(points_earned) as total_points, " +
                    "MIN(date) as first_date, " +
                    "MAX(date) as last_date " +
                    "FROM checkbox_challenges WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                stats.put("totalDays", rs.getInt("total_days"));
                stats.put("totalPoints", rs.getInt("total_points"));
                
                java.sql.Date firstDate = rs.getDate("first_date");
                java.sql.Date lastDate = rs.getDate("last_date");
                
                if (firstDate != null) stats.put("firstDate", firstDate.toLocalDate());
                if (lastDate != null) stats.put("lastDate", lastDate.toLocalDate());
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all-time stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Get category-specific stats
        String categorySql = "SELECT category, " +
                           "COUNT(DISTINCT date) as days_completed " +
                           "FROM checkbox_challenges " +
                           "WHERE user_id = ? AND completed = TRUE " +
                           "GROUP BY category";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(categorySql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String category = rs.getString("category");
                int daysCompleted = rs.getInt("days_completed");
                stats.put(category.toLowerCase() + "Days", daysCompleted);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving category stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // Calculate current streak
    public int getCurrentStreak(int userId) {
        int streak = 0;
        LocalDate checkDate = LocalDate.now();
        
        // Check backwards from today
        while (true) {
            WeeklyChallengeDay dayData = getDayChallengeData(userId, checkDate);
            
            // Consider a day complete if overall completion is >= 75%
            if (dayData.getOverallCompletion() >= 75) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
            
            // Safety limit
            if (streak > 365) break;
        }
        
        return streak;
    }
    
    // Calculate longest streak
    public int getLongestStreak(int userId) {
        int longestStreak = 0;
        int currentStreak = 0;
        
        String sql = "SELECT date, " +
                    "SUM(CASE WHEN completed THEN 1 ELSE 0 END) as completed_count, " +
                    "COUNT(*) as total_count " +
                    "FROM checkbox_challenges " +
                    "WHERE user_id = ? " +
                    "GROUP BY date " +
                    "ORDER BY date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            LocalDate previousDate = null;
            
            while (rs.next()) {
                LocalDate date = rs.getDate("date").toLocalDate();
                int completedCount = rs.getInt("completed_count");
                int totalCount = rs.getInt("total_count");
                
                double completionRate = totalCount > 0 ? (completedCount * 100.0) / totalCount : 0;
                
                // Check if day is considered complete (>= 75%)
                if (completionRate >= 75) {
                    // Check if consecutive
                    if (previousDate == null || date.equals(previousDate.plusDays(1))) {
                        currentStreak++;
                        longestStreak = Math.max(longestStreak, currentStreak);
                    } else {
                        currentStreak = 1;
                    }
                    previousDate = date;
                } else {
                    currentStreak = 0;
                    previousDate = null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating longest streak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return longestStreak;
    }
    
    private WeeklyChallengeDay getDayChallengeData(int userId, LocalDate date) {
        WeeklyChallengeDay dayData = new WeeklyChallengeDay(date);
        
        String sql = "SELECT category, COUNT(*) as total, SUM(CASE WHEN completed THEN 1 ELSE 0 END) as completed, " +
                    "SUM(points_earned) as points FROM checkbox_challenges " +
                    "WHERE user_id = ? AND date = ? GROUP BY category";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String category = rs.getString("category");
                int total = rs.getInt("total");
                int completed = rs.getInt("completed");
                int points = rs.getInt("points");
                
                double percentage = total > 0 ? (completed * 100.0) / total : 0;
                
                switch (category) {
                    case "EXERCISE":
                        dayData.setExerciseCompletion(percentage);
                        dayData.setExercisePoints(points);
                        break;
                    case "NUTRITION":
                        dayData.setNutritionCompletion(percentage);
                        dayData.setNutritionPoints(points);
                        break;
                    case "HYDRATION":
                        dayData.setHydrationCompletion(percentage);
                        dayData.setHydrationPoints(points);
                        break;
                    case "RECOVERY":
                        dayData.setRecoveryCompletion(percentage);
                        dayData.setRecoveryPoints(points);
                        break;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving day challenge data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dayData;
    }
    
    // Inner class for weekly challenge day data
    public static class WeeklyChallengeDay {
        private LocalDate date;
        private double exerciseCompletion = 0;
        private double nutritionCompletion = 0;
        private double hydrationCompletion = 0;
        private double recoveryCompletion = 0;
        private int exercisePoints = 0;
        private int nutritionPoints = 0;
        private int hydrationPoints = 0;
        private int recoveryPoints = 0;
        
        public WeeklyChallengeDay(LocalDate date) {
            this.date = date;
        }
        
        // Getters and setters
        public LocalDate getDate() { return date; }
        
        public double getExerciseCompletion() { return exerciseCompletion; }
        public void setExerciseCompletion(double exerciseCompletion) { this.exerciseCompletion = exerciseCompletion; }
        
        public double getNutritionCompletion() { return nutritionCompletion; }
        public void setNutritionCompletion(double nutritionCompletion) { this.nutritionCompletion = nutritionCompletion; }
        
        public double getHydrationCompletion() { return hydrationCompletion; }
        public void setHydrationCompletion(double hydrationCompletion) { this.hydrationCompletion = hydrationCompletion; }
        
        public double getRecoveryCompletion() { return recoveryCompletion; }
        public void setRecoveryCompletion(double recoveryCompletion) { this.recoveryCompletion = recoveryCompletion; }
        
        public int getExercisePoints() { return exercisePoints; }
        public void setExercisePoints(int exercisePoints) { this.exercisePoints = exercisePoints; }
        
        public int getNutritionPoints() { return nutritionPoints; }
        public void setNutritionPoints(int nutritionPoints) { this.nutritionPoints = nutritionPoints; }
        
        public int getHydrationPoints() { return hydrationPoints; }
        public void setHydrationPoints(int hydrationPoints) { this.hydrationPoints = hydrationPoints; }
        
        public int getRecoveryPoints() { return recoveryPoints; }
        public void setRecoveryPoints(int recoveryPoints) { this.recoveryPoints = recoveryPoints; }
        
        public double getOverallCompletion() {
            return (exerciseCompletion + nutritionCompletion + hydrationCompletion + recoveryCompletion) / 4.0;
        }
        
        public int getTotalPoints() {
            return exercisePoints + nutritionPoints + hydrationPoints + recoveryPoints;
        }
    }
}