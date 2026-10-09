package fitnessplanner;

import java.time.LocalDate;

public class WeeklyProgressData {
    private int userId;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private int totalDays;
    private int completedDays;
    private double avgCompletionPercentage;
    private int totalSteps;
    private int totalWorkoutMinutes;
    private int totalCaloriesBurned;
    private double avgWaterIntake;
    private double avgSleepHours;
    private int totalPoints;
    private double bestDayPercentage;
    private int currentStreak;
    
    public WeeklyProgressData() {
        this.totalDays = 7;
        this.completedDays = 0;
        this.avgCompletionPercentage = 0;
        this.totalSteps = 0;
        this.totalWorkoutMinutes = 0;
        this.totalCaloriesBurned = 0;
        this.avgWaterIntake = 0;
        this.avgSleepHours = 0;
        this.totalPoints = 0;
        this.bestDayPercentage = 0;
        this.currentStreak = 0;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public LocalDate getWeekStartDate() { return weekStartDate; }
    public void setWeekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; }
    
    public LocalDate getWeekEndDate() { return weekEndDate; }
    public void setWeekEndDate(LocalDate weekEndDate) { this.weekEndDate = weekEndDate; }
    
    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
    
    public int getCompletedDays() { return completedDays; }
    public void setCompletedDays(int completedDays) { this.completedDays = completedDays; }
    
    public double getAvgCompletionPercentage() { return avgCompletionPercentage; }
    public void setAvgCompletionPercentage(double avgCompletionPercentage) { this.avgCompletionPercentage = avgCompletionPercentage; }
    
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    
    public int getTotalWorkoutMinutes() { return totalWorkoutMinutes; }
    public void setTotalWorkoutMinutes(int totalWorkoutMinutes) { this.totalWorkoutMinutes = totalWorkoutMinutes; }
    
    public int getTotalCaloriesBurned() { return totalCaloriesBurned; }
    public void setTotalCaloriesBurned(int totalCaloriesBurned) { this.totalCaloriesBurned = totalCaloriesBurned; }
    
    public double getAvgWaterIntake() { return avgWaterIntake; }
    public void setAvgWaterIntake(double avgWaterIntake) { this.avgWaterIntake = avgWaterIntake; }
    
    public double getAvgSleepHours() { return avgSleepHours; }
    public void setAvgSleepHours(double avgSleepHours) { this.avgSleepHours = avgSleepHours; }
    
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    
    public double getBestDayPercentage() { return bestDayPercentage; }
    public void setBestDayPercentage(double bestDayPercentage) { this.bestDayPercentage = bestDayPercentage; }
    
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    
    // Calculated properties
    public double getCompletionRate() {
        return totalDays > 0 ? (completedDays / (double) totalDays) * 100 : 0;
    }
    
    public int getAvgStepsPerDay() {
        return totalDays > 0 ? totalSteps / totalDays : 0;
    }
    
    public int getAvgWorkoutMinutesPerDay() {
        return totalDays > 0 ? totalWorkoutMinutes / totalDays : 0;
    }
    
    public int getAvgCaloriesBurnedPerDay() {
        return totalDays > 0 ? totalCaloriesBurned / totalDays : 0;
    }
    
    public String getWeekSummary() {
        return String.format("Week %s - %s: %.1f%% completion, %d points", 
                           weekStartDate.toString(), weekEndDate.toString(), 
                           avgCompletionPercentage, totalPoints);
    }
    
    public String getPerformanceLevel() {
        if (avgCompletionPercentage >= 90) return "Excellent";
        if (avgCompletionPercentage >= 75) return "Great";
        if (avgCompletionPercentage >= 60) return "Good";
        if (avgCompletionPercentage >= 40) return "Fair";
        return "Needs Improvement";
    }
    
    @Override
    public String toString() {
        return String.format("WeeklyProgressData{week=%s to %s, completion=%.1f%%, points=%d}", 
                           weekStartDate, weekEndDate, avgCompletionPercentage, totalPoints);
    }
}