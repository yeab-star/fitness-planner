package fitnessplanner;

import java.time.LocalDate;
import java.util.*;

public class DailyRecommendationService {
    
    // Exercise recommendations that rotate daily
    private static final String[][] DAILY_EXERCISES = {
        // Day 1 (Monday)
        {"🏃 Morning Jog (20 min)", "💪 Push-ups (3 sets of 10)", "🧘 Stretching (10 min)", "🚶 Evening Walk (15 min)"},
        // Day 2 (Tuesday)  
        {"🏋️ Weight Training (30 min)", "🤸 Planks (3 sets of 30 sec)", "🚴 Cycling (25 min)", "🏊 Swimming (20 min)"},
        // Day 3 (Wednesday)
        {"🤾 Cardio Workout (25 min)", "🦵 Squats (3 sets of 15)", "🧘 Yoga (20 min)", "🏃 Interval Running (15 min)"},
        // Day 4 (Thursday)
        {"🏋️ Strength Training (35 min)", "💪 Pull-ups (3 sets of 8)", "🤸 Core Workout (15 min)", "🚶 Brisk Walk (20 min)"},
        // Day 5 (Friday)
        {"🏃 HIIT Workout (20 min)", "🦵 Lunges (3 sets of 12)", "🧘 Meditation Walk (15 min)", "🏊 Water Aerobics (25 min)"},
        // Day 6 (Saturday)
        {"🚴 Outdoor Cycling (40 min)", "🤸 Flexibility Training (20 min)", "🏃 Trail Running (30 min)", "💪 Bodyweight Circuit (15 min)"},
        // Day 7 (Sunday)
        {"🧘 Gentle Yoga (30 min)", "🚶 Nature Walk (45 min)", "🏊 Relaxed Swimming (20 min)", "🤸 Light Stretching (15 min)"}
    };
    
    // Nutrition recommendations that rotate daily
    private static final String[][] DAILY_NUTRITION = {
        // Day 1
        {"🥗 Green Leafy Salad", "🍎 Apple with Almonds", "🐟 Grilled Salmon", "🥑 Avocado Toast", "🥛 Greek Yogurt"},
        // Day 2
        {"🥕 Carrot Sticks", "🍌 Banana Smoothie", "🍗 Grilled Chicken", "🥒 Cucumber Water", "🥜 Mixed Nuts"},
        // Day 3
        {"🥬 Spinach Smoothie", "🍓 Berry Bowl", "🐟 Tuna Salad", "🥦 Steamed Broccoli", "🧀 Cottage Cheese"},
        // Day 4
        {"🥒 Veggie Wrap", "🍊 Orange Slices", "🥩 Lean Beef", "🍅 Tomato Salad", "🥛 Protein Shake"},
        // Day 5
        {"🥑 Avocado Bowl", "🥝 Kiwi Fruit", "🍗 Turkey Breast", "🥕 Roasted Vegetables", "🥯 Whole Grain Toast"},
        // Day 6
        {"🥗 Quinoa Salad", "🍇 Grape Snack", "🐟 Baked Cod", "🥬 Kale Chips", "🥛 Almond Milk"},
        // Day 7
        {"🍅 Caprese Salad", "🍎 Apple Cinnamon", "🥩 Grilled Steak", "🥦 Veggie Stir-fry", "🧀 String Cheese"}
    };
    
    // Hydration recommendations
    private static final String[][] DAILY_HYDRATION = {
        {"💧 Morning Water (2 glasses)", "🍋 Lemon Water", "🥤 Herbal Tea", "💧 Pre-workout Water"},
        {"💧 Wake-up Water (1 glass)", "🥤 Green Tea", "🍋 Infused Water", "💧 Post-meal Water"},
        {"💧 Breakfast Water", "🥤 Chamomile Tea", "🍊 Orange Water", "💧 Evening Hydration"},
        {"💧 Early Morning (2 glasses)", "🥤 Mint Tea", "🍓 Berry Water", "💧 Workout Hydration"},
        {"💧 Start Day Water", "🥤 Ginger Tea", "🥒 Cucumber Water", "💧 Bedtime Water"},
        {"💧 Morning Boost (2 glasses)", "🥤 Oolong Tea", "🍋 Citrus Water", "💧 Activity Water"},
        {"💧 Gentle Morning Water", "🥤 Relaxing Tea", "🍃 Mint Water", "💧 Peaceful Hydration"}
    };
    
    // Sleep & Recovery recommendations
    private static final String[][] DAILY_RECOVERY = {
        {"😴 8 Hours Sleep", "🛌 10 PM Bedtime", "📱 No Screens 1hr Before Bed", "🧘 Evening Meditation"},
        {"😴 Quality Sleep", "🛌 Consistent Schedule", "📖 Read Before Bed", "🧘 Deep Breathing"},
        {"😴 Restful Night", "🛌 Cool Room Temp", "🛁 Warm Bath", "🧘 Progressive Relaxation"},
        {"😴 Deep Sleep", "🛌 Dark Environment", "📱 Phone Away", "🧘 Gratitude Practice"},
        {"😴 Peaceful Sleep", "🛌 Comfortable Mattress", "🎵 Calming Music", "🧘 Body Scan"},
        {"😴 Rejuvenating Rest", "🛌 Weekend Sleep-in", "📚 Light Reading", "🧘 Mindfulness"},
        {"😴 Recovery Sleep", "🛌 Early Bedtime", "🛁 Relaxing Routine", "🧘 Peaceful Thoughts"}
    };
    
    public static List<String> getDailyExercises(LocalDate date) {
        int dayIndex = (date.getDayOfYear() - 1) % DAILY_EXERCISES.length;
        return Arrays.asList(DAILY_EXERCISES[dayIndex]);
    }
    
    public static List<String> getDailyNutrition(LocalDate date) {
        int dayIndex = (date.getDayOfYear() - 1) % DAILY_NUTRITION.length;
        return Arrays.asList(DAILY_NUTRITION[dayIndex]);
    }
    
    public static List<String> getDailyHydration(LocalDate date) {
        int dayIndex = (date.getDayOfYear() - 1) % DAILY_HYDRATION.length;
        return Arrays.asList(DAILY_HYDRATION[dayIndex]);
    }
    
    public static List<String> getDailyRecovery(LocalDate date) {
        int dayIndex = (date.getDayOfYear() - 1) % DAILY_RECOVERY.length;
        return Arrays.asList(DAILY_RECOVERY[dayIndex]);
    }
    
    // Get motivational message for the day
    public static String getDailyMotivation(LocalDate date) {
        String[] motivations = {
            "💪 Start strong, finish stronger!",
            "🌟 Every step counts towards your goal!",
            "🔥 Push your limits today!",
            "✨ Consistency is the key to success!",
            "🚀 You're capable of amazing things!",
            "💎 Diamonds are formed under pressure!",
            "🌈 Rest, recover, and come back stronger!"
        };
        
        int dayIndex = (date.getDayOfYear() - 1) % motivations.length;
        return motivations[dayIndex];
    }
    
    // Calculate completion percentage based on checked items
    public static double calculateCompletionPercentage(Map<String, Boolean> completedItems) {
        if (completedItems.isEmpty()) return 0.0;
        
        long completedCount = completedItems.values().stream()
            .mapToLong(completed -> completed ? 1 : 0)
            .sum();
        
        return (completedCount * 100.0) / completedItems.size();
    }
    
    // Calculate points based on category completion
    public static int calculatePoints(Map<String, Boolean> exerciseItems, 
                                    Map<String, Boolean> nutritionItems,
                                    Map<String, Boolean> hydrationItems, 
                                    Map<String, Boolean> recoveryItems) {
        int points = 0;
        
        // Exercise points (25 points max)
        double exerciseCompletion = calculateCompletionPercentage(exerciseItems);
        if (exerciseCompletion >= 75) points += 25;
        else if (exerciseCompletion >= 50) points += 15;
        else if (exerciseCompletion >= 25) points += 10;
        
        // Nutrition points (20 points max)
        double nutritionCompletion = calculateCompletionPercentage(nutritionItems);
        if (nutritionCompletion >= 80) points += 20;
        else if (nutritionCompletion >= 60) points += 15;
        else if (nutritionCompletion >= 40) points += 10;
        
        // Hydration points (15 points max)
        double hydrationCompletion = calculateCompletionPercentage(hydrationItems);
        if (hydrationCompletion >= 75) points += 15;
        else if (hydrationCompletion >= 50) points += 10;
        else if (hydrationCompletion >= 25) points += 5;
        
        // Recovery points (20 points max)
        double recoveryCompletion = calculateCompletionPercentage(recoveryItems);
        if (recoveryCompletion >= 75) points += 20;
        else if (recoveryCompletion >= 50) points += 15;
        else if (recoveryCompletion >= 25) points += 10;
        
        return points;
    }
}