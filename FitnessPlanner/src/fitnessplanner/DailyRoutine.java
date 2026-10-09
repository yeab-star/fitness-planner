package fitnessplanner;

import java.time.LocalDate;
import java.util.List;

public class DailyRoutine {
    private LocalDate date;
    private String workoutPlan;
    private List<String> exercises;
    private String mealPlan;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getWorkoutPlan() { return workoutPlan; }
    public void setWorkoutPlan(String workoutPlan) { this.workoutPlan = workoutPlan; }
    
    public List<String> getExercises() { return exercises; }
    public void setExercises(List<String> exercises) { this.exercises = exercises; }
    
    public String getMealPlan() { return mealPlan; }
    public void setMealPlan(String mealPlan) { this.mealPlan = mealPlan; }
}