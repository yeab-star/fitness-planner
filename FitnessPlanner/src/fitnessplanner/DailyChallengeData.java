package fitnessplanner;

import java.time.LocalDate;

public class DailyChallengeData {
    private LocalDate date;
    private int steps;
    private int workoutMinutes;
    private int caloriesBurned;
    private int vegetablesServings;
    private int fruitsServings;
    private int calorieIntake;
    private int proteinGrams;
    private int waterMl;
    private int waterGlasses;
    private double sleepHours;
    private boolean sleepQuality;
    private boolean exerciseCompleted;
    private boolean nutritionCompleted;
    private boolean hydrationCompleted;
    private boolean sleepCompleted;
    private int totalPoints;
    private double completionPercentage;
    
    // Challenge targets (can be customized per user)
    private static final int TARGET_STEPS = 10000;
    private static final int TARGET_WORKOUT_MINUTES = 30;
    private static final int TARGET_CALORIES_BURNED = 300;
    private static final int TARGET_VEGETABLES = 5;
    private static final int TARGET_FRUITS = 3;
    private static final int TARGET_WATER_ML = 2000;
    private static final int TARGET_WATER_GLASSES = 8;
    private static final double TARGET_SLEEP_HOURS = 8.0;
    
    // Points for each challenge
    private static final int POINTS_EXERCISE = 25;
    private static final int POINTS_NUTRITION = 20;
    private static final int POINTS_HYDRATION = 15;
    private static final int POINTS_SLEEP = 20;
    
    public DailyChallengeData(LocalDate date) {
        this.date = date;
        this.steps = 0;
        this.workoutMinutes = 0;
        this.caloriesBurned = 0;
        this.vegetablesServings = 0;
        this.fruitsServings = 0;
        this.calorieIntake = 0;
        this.proteinGrams = 0;
        this.waterMl = 0;
        this.waterGlasses = 0;
        this.sleepHours = 0;
        this.sleepQuality = false;
        this.exerciseCompleted = false;
        this.nutritionCompleted = false;
        this.hydrationCompleted = false;
        this.sleepCompleted = false;
        this.totalPoints = 0;
        this.completionPercentage = 0;
    }
    
    // Calculate completion status and points based on current values
    public void calculateCompletion() {
        // Exercise completion
        exerciseCompleted = (steps >= TARGET_STEPS || workoutMinutes >= TARGET_WORKOUT_MINUTES || 
                           caloriesBurned >= TARGET_CALORIES_BURNED);
        
        // Nutrition completion
        nutritionCompleted = (vegetablesServings >= TARGET_VEGETABLES && fruitsServings >= TARGET_FRUITS);
        
        // Hydration completion
        hydrationCompleted = (waterMl >= TARGET_WATER_ML || waterGlasses >= TARGET_WATER_GLASSES);
        
        // Sleep completion
        sleepCompleted = (sleepHours >= TARGET_SLEEP_HOURS && sleepQuality);
        
        // Calculate points
        totalPoints = 0;
        if (exerciseCompleted) totalPoints += POINTS_EXERCISE;
        if (nutritionCompleted) totalPoints += POINTS_NUTRITION;
        if (hydrationCompleted) totalPoints += POINTS_HYDRATION;
        if (sleepCompleted) totalPoints += POINTS_SLEEP;
        
        // Calculate completion percentage
        int completedChallenges = 0;
        if (exerciseCompleted) completedChallenges++;
        if (nutritionCompleted) completedChallenges++;
        if (hydrationCompleted) completedChallenges++;
        if (sleepCompleted) completedChallenges++;
        
        completionPercentage = (completedChallenges / 4.0) * 100.0;
    }
    
    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public int getSteps() { return steps; }
    public void setSteps(int steps) { 
        this.steps = steps; 
        calculateCompletion();
    }
    
    public int getWorkoutMinutes() { return workoutMinutes; }
    public void setWorkoutMinutes(int workoutMinutes) { 
        this.workoutMinutes = workoutMinutes; 
        calculateCompletion();
    }
    
    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { 
        this.caloriesBurned = caloriesBurned; 
        calculateCompletion();
    }
    
    public int getVegetablesServings() { return vegetablesServings; }
    public void setVegetablesServings(int vegetablesServings) { 
        this.vegetablesServings = vegetablesServings; 
        calculateCompletion();
    }
    
    public int getFruitsServings() { return fruitsServings; }
    public void setFruitsServings(int fruitsServings) { 
        this.fruitsServings = fruitsServings; 
        calculateCompletion();
    }
    
    public int getCalorieIntake() { return calorieIntake; }
    public void setCalorieIntake(int calorieIntake) { this.calorieIntake = calorieIntake; }
    
    public int getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(int proteinGrams) { this.proteinGrams = proteinGrams; }
    
    public int getWaterMl() { return waterMl; }
    public void setWaterMl(int waterMl) { 
        this.waterMl = waterMl; 
        this.waterGlasses = waterMl / 250; // Convert to glasses
        calculateCompletion();
    }
    
    public int getWaterGlasses() { return waterGlasses; }
    public void setWaterGlasses(int waterGlasses) { 
        this.waterGlasses = waterGlasses; 
        this.waterMl = waterGlasses * 250; // Convert to ml
        calculateCompletion();
    }
    
    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { 
        this.sleepHours = sleepHours; 
        calculateCompletion();
    }
    
    public boolean isSleepQuality() { return sleepQuality; }
    public void setSleepQuality(boolean sleepQuality) { 
        this.sleepQuality = sleepQuality; 
        calculateCompletion();
    }
    
    public boolean isExerciseCompleted() { return exerciseCompleted; }
    public void setExerciseCompleted(boolean exerciseCompleted) { this.exerciseCompleted = exerciseCompleted; }
    
    public boolean isNutritionCompleted() { return nutritionCompleted; }
    public void setNutritionCompleted(boolean nutritionCompleted) { this.nutritionCompleted = nutritionCompleted; }
    
    public boolean isHydrationCompleted() { return hydrationCompleted; }
    public void setHydrationCompleted(boolean hydrationCompleted) { this.hydrationCompleted = hydrationCompleted; }
    
    public boolean isSleepCompleted() { return sleepCompleted; }
    public void setSleepCompleted(boolean sleepCompleted) { this.sleepCompleted = sleepCompleted; }
    
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    
    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }
    
    // Target getters for UI display
    public static int getTargetSteps() { return TARGET_STEPS; }
    public static int getTargetWorkoutMinutes() { return TARGET_WORKOUT_MINUTES; }
    public static int getTargetCaloriesBurned() { return TARGET_CALORIES_BURNED; }
    public static int getTargetVegetables() { return TARGET_VEGETABLES; }
    public static int getTargetFruits() { return TARGET_FRUITS; }
    public static int getTargetWaterMl() { return TARGET_WATER_ML; }
    public static int getTargetWaterGlasses() { return TARGET_WATER_GLASSES; }
    public static double getTargetSleepHours() { return TARGET_SLEEP_HOURS; }
    
    // Progress percentage for individual challenges
    public double getExerciseProgress() {
        double stepsProgress = Math.min(100, (steps / (double) TARGET_STEPS) * 100);
        double workoutProgress = Math.min(100, (workoutMinutes / (double) TARGET_WORKOUT_MINUTES) * 100);
        double caloriesProgress = Math.min(100, (caloriesBurned / (double) TARGET_CALORIES_BURNED) * 100);
        return Math.max(stepsProgress, Math.max(workoutProgress, caloriesProgress));
    }
    
    public double getNutritionProgress() {
        double veggieProgress = Math.min(100, (vegetablesServings / (double) TARGET_VEGETABLES) * 100);
        double fruitProgress = Math.min(100, (fruitsServings / (double) TARGET_FRUITS) * 100);
        return (veggieProgress + fruitProgress) / 2.0;
    }
    
    public double getHydrationProgress() {
        return Math.min(100, (waterMl / (double) TARGET_WATER_ML) * 100);
    }
    
    public double getSleepProgress() {
        double hoursProgress = Math.min(100, (sleepHours / TARGET_SLEEP_HOURS) * 100);
        return sleepQuality ? hoursProgress : hoursProgress * 0.7; // Reduce if poor quality
    }
    
    @Override
    public String toString() {
        return String.format("DailyChallengeData{date=%s, completion=%.1f%%, points=%d}", 
                           date, completionPercentage, totalPoints);
    }
}