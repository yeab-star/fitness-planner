package fitnessplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

public class WorkoutService {
    
    public boolean markExerciseCompleted(int userId, String exerciseName) {
        String query = "INSERT INTO workout_completions (user_id, exercise_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, exerciseName);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // NEW: Unmark exercise as completed
    public boolean unmarkExerciseCompleted(int userId, String exerciseName) {
        String query = "DELETE FROM workout_completions WHERE user_id = ? AND exercise_name = ? AND completion_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, exerciseName);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isExerciseCompletedToday(int userId, String exerciseName) {
        String query = "SELECT 1 FROM workout_completions WHERE user_id = ? AND exercise_name = ? AND completion_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, exerciseName);
            return stmt.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void updateStreak(int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String getLastDateQuery = "SELECT last_completion_date FROM users WHERE id = ?";
            LocalDate lastDate = null;
            try (PreparedStatement stmt = conn.prepareStatement(getLastDateQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getDate("last_completion_date") != null) {
                    lastDate = rs.getDate("last_completion_date").toLocalDate();
                }
            }
            
            LocalDate today = LocalDate.now();
            int newStreak = 1;
            
            // Check if user has completed ANY exercise today
            boolean hasAnyCompletionToday = hasAnyExerciseCompletedToday(userId);
            
            if (lastDate != null) {
                if (lastDate.equals(today.minusDays(1)) && hasAnyCompletionToday) {
                    // Consecutive day with completion - increment streak
                    String getStreakQuery = "SELECT streak FROM users WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(getStreakQuery)) {
                        stmt.setInt(1, userId);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            newStreak = rs.getInt("streak") + 1;
                        }
                    }
                } else if (!lastDate.equals(today) && hasAnyCompletionToday) {
                    // New day with completion - check if should reset or start
                    if (lastDate.isBefore(today.minusDays(1))) {
                        // Gap of more than 1 day - reset streak
                        newStreak = 1;
                    } else {
                        // Consecutive day
                        String getStreakQuery = "SELECT streak FROM users WHERE id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(getStreakQuery)) {
                            stmt.setInt(1, userId);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                newStreak = rs.getInt("streak") + 1;
                            }
                        }
                    }
                } else if (!hasAnyCompletionToday) {
                    // No completion today - streak stays same (don't reset yet)
                    String getStreakQuery = "SELECT streak FROM users WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(getStreakQuery)) {
                        stmt.setInt(1, userId);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            newStreak = rs.getInt("streak");
                        }
                    }
                }
            } else {
                // First time user - if they completed today, start streak
                if (hasAnyCompletionToday) {
                    newStreak = 1;
                }
            }
            
            String updateQuery = "UPDATE users SET streak = ?, last_completion_date = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setInt(1, newStreak);
                if (hasAnyCompletionToday) {
                    stmt.setDate(2, java.sql.Date.valueOf(today));
                } else {
                    // Keep last completion date as is
                    stmt.setDate(2, lastDate != null ? java.sql.Date.valueOf(lastDate) : null);
                }
                stmt.setInt(3, userId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean hasAnyExerciseCompletedToday(int userId) {
        String query = "SELECT 1 FROM workout_completions WHERE user_id = ? AND completion_date = CURDATE() LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getCurrentStreak(int userId) {
        String query = "SELECT streak FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("streak");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getWeeklyCompletionRate(int userId) {
        String query = "SELECT COUNT(*) as completed FROM workout_completions WHERE user_id = ? AND completion_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int completed = rs.getInt("completed");
                // Assuming 5 workouts per week with 3 exercises each = 105 total possible
                // But let's be more realistic: 5 workouts * 3 exercises * 7 days = 105
                // However, we'll cap it at 100%
                return Math.min(100.0, (completed / 21.0) * 100); // 3 exercises * 7 days = 21
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}