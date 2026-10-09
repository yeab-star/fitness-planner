package fitnessplanner;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DailyChallengeService {
    
    // Initialize all required tables for daily challenges
    public void initializeTables() {
        createDailyChallengeDataTable();
        createWeeklyProgressTable();
        createChallengeStreaksTable();
    }
    
    private void createDailyChallengeDataTable() {
        String sql = "CREATE TABLE IF NOT EXISTS daily_challenge_data (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "date DATE NOT NULL," +
                    "steps INT DEFAULT 0," +
                    "workout_minutes INT DEFAULT 0," +
                    "calories_burned INT DEFAULT 0," +
                    "vegetables_servings INT DEFAULT 0," +
                    "fruits_servings INT DEFAULT 0," +
                    "calorie_intake INT DEFAULT 0," +
                    "protein_grams INT DEFAULT 0," +
                    "water_ml INT DEFAULT 0," +
                    "water_glasses INT DEFAULT 0," +
                    "sleep_hours DECIMAL(3,1) DEFAULT 0," +
                    "sleep_quality BOOLEAN DEFAULT FALSE," +
                    "exercise_completed BOOLEAN DEFAULT FALSE," +
                    "nutrition_completed BOOLEAN DEFAULT FALSE," +
                    "hydration_completed BOOLEAN DEFAULT FALSE," +
                    "sleep_completed BOOLEAN DEFAULT FALSE," +
                    "total_points INT DEFAULT 0," +
                    "completion_percentage DECIMAL(5,2) DEFAULT 0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "UNIQUE KEY unique_user_date (user_id, date)" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Daily challenge data table created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating daily_challenge_data table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createWeeklyProgressTable() {
        String sql = "CREATE TABLE IF NOT EXISTS weekly_progress (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "week_start_date DATE NOT NULL," +
                    "week_end_date DATE NOT NULL," +
                    "total_days INT DEFAULT 7," +
                    "completed_days INT DEFAULT 0," +
                    "avg_completion_percentage DECIMAL(5,2) DEFAULT 0," +
                    "total_steps INT DEFAULT 0," +
                    "total_workout_minutes INT DEFAULT 0," +
                    "total_calories_burned INT DEFAULT 0," +
                    "avg_water_intake INT DEFAULT 0," +
                    "avg_sleep_hours DECIMAL(3,1) DEFAULT 0," +
                    "total_points INT DEFAULT 0," +
                    "best_day_percentage DECIMAL(5,2) DEFAULT 0," +
                    "current_streak INT DEFAULT 0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE KEY unique_user_week (user_id, week_start_date)" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Weekly progress table created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating weekly_progress table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createChallengeStreaksTable() {
        String sql = "CREATE TABLE IF NOT EXISTS challenge_streaks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "challenge_type ENUM('EXERCISE', 'NUTRITION', 'HYDRATION', 'SLEEP', 'OVERALL') NOT NULL," +
                    "current_streak INT DEFAULT 0," +
                    "longest_streak INT DEFAULT 0," +
                    "last_completed_date DATE," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "UNIQUE KEY unique_user_challenge_type (user_id, challenge_type)" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Challenge streaks table created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating challenge_streaks table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Save or update daily challenge data
    public boolean saveDailyChallengeData(int userId, DailyChallengeData data) {
        String sql = "INSERT INTO daily_challenge_data (" +
                    "user_id, date, steps, workout_minutes, calories_burned, " +
                    "vegetables_servings, fruits_servings, calorie_intake, protein_grams, " +
                    "water_ml, water_glasses, sleep_hours, sleep_quality, " +
                    "exercise_completed, nutrition_completed, hydration_completed, sleep_completed, " +
                    "total_points, completion_percentage) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "steps = VALUES(steps), workout_minutes = VALUES(workout_minutes), " +
                    "calories_burned = VALUES(calories_burned), vegetables_servings = VALUES(vegetables_servings), " +
                    "fruits_servings = VALUES(fruits_servings), calorie_intake = VALUES(calorie_intake), " +
                    "protein_grams = VALUES(protein_grams), water_ml = VALUES(water_ml), " +
                    "water_glasses = VALUES(water_glasses), sleep_hours = VALUES(sleep_hours), " +
                    "sleep_quality = VALUES(sleep_quality), exercise_completed = VALUES(exercise_completed), " +
                    "nutrition_completed = VALUES(nutrition_completed), hydration_completed = VALUES(hydration_completed), " +
                    "sleep_completed = VALUES(sleep_completed), total_points = VALUES(total_points), " +
                    "completion_percentage = VALUES(completion_percentage)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(data.getDate()));
            stmt.setInt(3, data.getSteps());
            stmt.setInt(4, data.getWorkoutMinutes());
            stmt.setInt(5, data.getCaloriesBurned());
            stmt.setInt(6, data.getVegetablesServings());
            stmt.setInt(7, data.getFruitsServings());
            stmt.setInt(8, data.getCalorieIntake());
            stmt.setInt(9, data.getProteinGrams());
            stmt.setInt(10, data.getWaterMl());
            stmt.setInt(11, data.getWaterGlasses());
            stmt.setDouble(12, data.getSleepHours());
            stmt.setBoolean(13, data.isSleepQuality());
            stmt.setBoolean(14, data.isExerciseCompleted());
            stmt.setBoolean(15, data.isNutritionCompleted());
            stmt.setBoolean(16, data.isHydrationCompleted());
            stmt.setBoolean(17, data.isSleepCompleted());
            stmt.setInt(18, data.getTotalPoints());
            stmt.setDouble(19, data.getCompletionPercentage());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Saved daily challenge data for user " + userId + ", rows affected: " + rowsAffected);
            
            // Update streaks
            updateStreaks(userId, data);
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error saving daily challenge data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get daily challenge data for a specific date
    public DailyChallengeData getDailyChallengeData(int userId, LocalDate date) {
        String sql = "SELECT * FROM daily_challenge_data WHERE user_id = ? AND date = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Found existing data for user " + userId + " on " + date);
                return mapResultSetToDailyChallengeData(rs);
            } else {
                System.out.println("No existing data found for user " + userId + " on " + date + ", creating new record");
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving daily challenge data: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Return empty data if no record found
        return new DailyChallengeData(date);
    }
    
    // Get weekly challenge data (last 7 days)
    public List<DailyChallengeData> getWeeklyChallengeData(int userId) {
        List<DailyChallengeData> weeklyData = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        String sql = "SELECT * FROM daily_challenge_data " +
                    "WHERE user_id = ? AND date BETWEEN ? AND ? " +
                    "ORDER BY date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                weeklyData.add(mapResultSetToDailyChallengeData(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return weeklyData;
    }
    
    // Calculate and save weekly progress
    public void calculateWeeklyProgress(int userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        List<DailyChallengeData> weeklyData = getWeeklyChallengeData(userId);
        
        if (weeklyData.isEmpty()) {
            return;
        }
        
        // Calculate weekly statistics
        WeeklyProgressData weeklyProgress = new WeeklyProgressData();
        weeklyProgress.setUserId(userId);
        weeklyProgress.setWeekStartDate(startDate);
        weeklyProgress.setWeekEndDate(endDate);
        
        int completedDays = 0;
        double totalCompletion = 0;
        int totalSteps = 0;
        int totalWorkoutMinutes = 0;
        int totalCaloriesBurned = 0;
        int totalWaterIntake = 0;
        double totalSleepHours = 0;
        int totalPoints = 0;
        double bestDayPercentage = 0;
        
        for (DailyChallengeData data : weeklyData) {
            if (data.getCompletionPercentage() >= 80) { // Consider 80%+ as completed day
                completedDays++;
            }
            
            totalCompletion += data.getCompletionPercentage();
            totalSteps += data.getSteps();
            totalWorkoutMinutes += data.getWorkoutMinutes();
            totalCaloriesBurned += data.getCaloriesBurned();
            totalWaterIntake += data.getWaterMl();
            totalSleepHours += data.getSleepHours();
            totalPoints += data.getTotalPoints();
            
            bestDayPercentage = Math.max(bestDayPercentage, data.getCompletionPercentage());
        }
        
        weeklyProgress.setCompletedDays(completedDays);
        weeklyProgress.setAvgCompletionPercentage(totalCompletion / weeklyData.size());
        weeklyProgress.setTotalSteps(totalSteps);
        weeklyProgress.setTotalWorkoutMinutes(totalWorkoutMinutes);
        weeklyProgress.setTotalCaloriesBurned(totalCaloriesBurned);
        weeklyProgress.setAvgWaterIntake(totalWaterIntake / weeklyData.size());
        weeklyProgress.setAvgSleepHours(totalSleepHours / weeklyData.size());
        weeklyProgress.setTotalPoints(totalPoints);
        weeklyProgress.setBestDayPercentage(bestDayPercentage);
        
        // Calculate current streak
        int currentStreak = calculateCurrentStreak(userId);
        weeklyProgress.setCurrentStreak(currentStreak);
        
        // Save weekly progress
        saveWeeklyProgress(weeklyProgress);
    }
    
    private void saveWeeklyProgress(WeeklyProgressData weeklyProgress) {
        String sql = "INSERT INTO weekly_progress (" +
                    "user_id, week_start_date, week_end_date, completed_days, " +
                    "avg_completion_percentage, total_steps, total_workout_minutes, " +
                    "total_calories_burned, avg_water_intake, avg_sleep_hours, " +
                    "total_points, best_day_percentage, current_streak) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "completed_days = VALUES(completed_days), " +
                    "avg_completion_percentage = VALUES(avg_completion_percentage), " +
                    "total_steps = VALUES(total_steps), " +
                    "total_workout_minutes = VALUES(total_workout_minutes), " +
                    "total_calories_burned = VALUES(total_calories_burned), " +
                    "avg_water_intake = VALUES(avg_water_intake), " +
                    "avg_sleep_hours = VALUES(avg_sleep_hours), " +
                    "total_points = VALUES(total_points), " +
                    "best_day_percentage = VALUES(best_day_percentage), " +
                    "current_streak = VALUES(current_streak)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, weeklyProgress.getUserId());
            stmt.setDate(2, java.sql.Date.valueOf(weeklyProgress.getWeekStartDate()));
            stmt.setDate(3, java.sql.Date.valueOf(weeklyProgress.getWeekEndDate()));
            stmt.setInt(4, weeklyProgress.getCompletedDays());
            stmt.setDouble(5, weeklyProgress.getAvgCompletionPercentage());
            stmt.setInt(6, weeklyProgress.getTotalSteps());
            stmt.setInt(7, weeklyProgress.getTotalWorkoutMinutes());
            stmt.setInt(8, weeklyProgress.getTotalCaloriesBurned());
            stmt.setDouble(9, weeklyProgress.getAvgWaterIntake());
            stmt.setDouble(10, weeklyProgress.getAvgSleepHours());
            stmt.setInt(11, weeklyProgress.getTotalPoints());
            stmt.setDouble(12, weeklyProgress.getBestDayPercentage());
            stmt.setInt(13, weeklyProgress.getCurrentStreak());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Update challenge streaks
    private void updateStreaks(int userId, DailyChallengeData data) {
        updateChallengeStreak(userId, "EXERCISE", data.isExerciseCompleted(), data.getDate());
        updateChallengeStreak(userId, "NUTRITION", data.isNutritionCompleted(), data.getDate());
        updateChallengeStreak(userId, "HYDRATION", data.isHydrationCompleted(), data.getDate());
        updateChallengeStreak(userId, "SLEEP", data.isSleepCompleted(), data.getDate());
        
        // Overall streak (all challenges completed)
        boolean overallCompleted = data.isExerciseCompleted() && data.isNutritionCompleted() && 
                                  data.isHydrationCompleted() && data.isSleepCompleted();
        updateChallengeStreak(userId, "OVERALL", overallCompleted, data.getDate());
    }
    
    private void updateChallengeStreak(int userId, String challengeType, boolean completed, LocalDate date) {
        String selectSql = "SELECT current_streak, longest_streak, last_completed_date " +
                          "FROM challenge_streaks WHERE user_id = ? AND challenge_type = ?";
        
        String insertSql = "INSERT INTO challenge_streaks (user_id, challenge_type, current_streak, longest_streak, last_completed_date) " +
                          "VALUES (?, ?, ?, ?, ?) " +
                          "ON DUPLICATE KEY UPDATE " +
                          "current_streak = VALUES(current_streak), " +
                          "longest_streak = VALUES(longest_streak), " +
                          "last_completed_date = VALUES(last_completed_date)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            int currentStreak = 0;
            int longestStreak = 0;
            LocalDate lastCompletedDate = null;
            
            // Get existing streak data
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userId);
                selectStmt.setString(2, challengeType);
                
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    currentStreak = rs.getInt("current_streak");
                    longestStreak = rs.getInt("longest_streak");
                    java.sql.Date lastDate = rs.getDate("last_completed_date");
                    if (lastDate != null) {
                        lastCompletedDate = lastDate.toLocalDate();
                    }
                }
            }
            
            // Update streak based on completion
            if (completed) {
                if (lastCompletedDate == null || lastCompletedDate.equals(date.minusDays(1))) {
                    currentStreak++;
                } else {
                    currentStreak = 1; // Reset streak if not consecutive
                }
                longestStreak = Math.max(longestStreak, currentStreak);
                lastCompletedDate = date;
            } else {
                currentStreak = 0; // Reset current streak if not completed
            }
            
            // Save updated streak
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, challengeType);
                insertStmt.setInt(3, currentStreak);
                insertStmt.setInt(4, longestStreak);
                insertStmt.setDate(5, lastCompletedDate != null ? java.sql.Date.valueOf(lastCompletedDate) : null);
                
                insertStmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Calculate current overall streak
    private int calculateCurrentStreak(int userId) {
        String sql = "SELECT current_streak FROM challenge_streaks " +
                    "WHERE user_id = ? AND challenge_type = 'OVERALL'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("current_streak");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Get challenge streaks for user
    public Map<String, Integer> getChallengeStreaks(int userId) {
        Map<String, Integer> streaks = new HashMap<>();
        
        String sql = "SELECT challenge_type, current_streak FROM challenge_streaks WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                streaks.put(rs.getString("challenge_type"), rs.getInt("current_streak"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return streaks;
    }
    
    // Helper method to map ResultSet to DailyChallengeData
    private DailyChallengeData mapResultSetToDailyChallengeData(ResultSet rs) throws SQLException {
        DailyChallengeData data = new DailyChallengeData(rs.getDate("date").toLocalDate());
        
        data.setSteps(rs.getInt("steps"));
        data.setWorkoutMinutes(rs.getInt("workout_minutes"));
        data.setCaloriesBurned(rs.getInt("calories_burned"));
        data.setVegetablesServings(rs.getInt("vegetables_servings"));
        data.setFruitsServings(rs.getInt("fruits_servings"));
        data.setCalorieIntake(rs.getInt("calorie_intake"));
        data.setProteinGrams(rs.getInt("protein_grams"));
        data.setWaterMl(rs.getInt("water_ml"));
        data.setWaterGlasses(rs.getInt("water_glasses"));
        data.setSleepHours(rs.getDouble("sleep_hours"));
        data.setSleepQuality(rs.getBoolean("sleep_quality"));
        data.setExerciseCompleted(rs.getBoolean("exercise_completed"));
        data.setNutritionCompleted(rs.getBoolean("nutrition_completed"));
        data.setHydrationCompleted(rs.getBoolean("hydration_completed"));
        data.setSleepCompleted(rs.getBoolean("sleep_completed"));
        data.setTotalPoints(rs.getInt("total_points"));
        data.setCompletionPercentage(rs.getDouble("completion_percentage"));
        
        return data;
    }
}