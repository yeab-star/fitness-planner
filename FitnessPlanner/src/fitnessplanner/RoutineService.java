package fitnessplanner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class RoutineService {
    
    public DailyRoutine generateDailyRoutine(User user) {
        DailyRoutine routine = new DailyRoutine();
        routine.setDate(LocalDate.now());

        String goal = user.getGoal();
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        if ("LOSE_WEIGHT".equals(goal)) {
            if (today == DayOfWeek.MONDAY || today == DayOfWeek.WEDNESDAY || today == DayOfWeek.FRIDAY) {
                routine.setWorkoutPlan("Fat Burn HIIT");
                routine.setExercises(Arrays.asList("Running", "Jumping Jacks", "Burpees"));
            } else {
                routine.setWorkoutPlan("Full Body Strength");
                routine.setExercises(Arrays.asList("Push-ups", "Squats", "Plank"));
            }
        } else if ("GAIN_MUSCLE".equals(goal)) {
            switch (today) {
                case MONDAY: routine.setWorkoutPlan("Chest & Triceps"); break;
                case TUESDAY: routine.setWorkoutPlan("Back & Biceps"); break;
                case WEDNESDAY: routine.setWorkoutPlan("Rest or Light Cardio"); break;
                case THURSDAY: routine.setWorkoutPlan("Legs"); break;
                case FRIDAY: routine.setWorkoutPlan("Shoulders & Abs"); break;
                default: routine.setWorkoutPlan("Rest");
            }
            routine.setExercises(getExercisesForPlan(routine.getWorkoutPlan()));
        } else {
            routine.setWorkoutPlan("Balanced Routine");
            routine.setExercises(Arrays.asList("Push-ups", "Squats", "Plank", "Walking"));
        }

        routine.setMealPlan(recommendMealPlan(goal, user.getWeight()));
        return routine;
    }

    private List<String> getExercisesForPlan(String plan) {
        Map<String, List<String>> planMap = new HashMap<>();
        planMap.put("Chest & Triceps", Arrays.asList("Push-ups", "Dips", "Plank"));
        planMap.put("Back & Biceps", Arrays.asList("Pull-ups", "Rows", "Curls"));
        planMap.put("Legs", Arrays.asList("Squats", "Lunges", "Calf Raises"));
        planMap.put("Shoulders & Abs", Arrays.asList("Plank", "Shoulder Press", "Crunches"));
        planMap.put("Fat Burn HIIT", Arrays.asList("Running", "Jumping Jacks", "Burpees"));
        return planMap.getOrDefault(plan, Arrays.asList("Push-ups", "Squats"));
    }

    private String recommendMealPlan(String goal, double weight) {
        if ("LOSE_WEIGHT".equals(goal)) {
            return "High protein, low carb: Grilled chicken, vegetables, quinoa";
        } else if ("GAIN_MUSCLE".equals(goal)) {
            return "High calorie surplus: Eggs, oats, lean beef, rice, nuts";
        } else {
            return "Balanced diet: Lean protein, complex carbs, healthy fats";
        }
    }
}