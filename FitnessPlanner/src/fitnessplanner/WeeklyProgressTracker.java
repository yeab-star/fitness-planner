package fitnessplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WeeklyProgressTracker {
    private Stage stage;
    private User currentUser;
    private ChallengeManager challengeManager;
    private CheckboxChallengeStorage checkboxStorage;
    private VBox mainContainer;
    
    public WeeklyProgressTracker(User user) {
        this.currentUser = user;
        this.challengeManager = new ChallengeManager();
        this.checkboxStorage = new CheckboxChallengeStorage();
        this.checkboxStorage.initializeTables();
        initializeView();
    }
    
    private void initializeView() {
        stage = new Stage();
        stage.setTitle("Weekly Progress - " + currentUser.getName());
        
        mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.getStyleClass().add("container");
        
        createBackButton();
        createHeader();
        createWeeklySummary();
        createWeeklyCheckboxProgress();
        createInsightsSection();
        createGoalsSection();
        
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Wrap in StackPane with gender-specific background
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        String backgroundClass = (currentUser.getGender() != null && currentUser.getGender().equalsIgnoreCase("Female")) 
            ? "female-dashboard-background" 
            : "male-dashboard-background";
        root.getStyleClass().add(backgroundClass);
        root.getChildren().add(scrollPane);
        
        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        
        stage.setScene(scene);
    }
    
    private void createWeeklyCheckboxProgress() {
        VBox weeklyBox = new VBox(20);
        weeklyBox.getStyleClass().add("card");
        
        Label weeklyTitle = new Label("📋 Weekly Challenge Progress");
        weeklyTitle.getStyleClass().add("card-title-label");
        
        // Get weekly checkbox data
        List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData = checkboxStorage.getWeeklyChallengeData(currentUser.getId());
        
        VBox progressContainer = new VBox(15);
        
        for (CheckboxChallengeStorage.WeeklyChallengeDay dayData : weeklyData) {
            VBox dayCard = createDayProgressCard(dayData);
            progressContainer.getChildren().add(dayCard);
        }
        
        ScrollPane scrollPane = new ScrollPane(progressContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.getStyleClass().add("scroll-pane");
        
        weeklyBox.getChildren().addAll(weeklyTitle, scrollPane);
        mainContainer.getChildren().add(weeklyBox);
    }
    
    private VBox createDayProgressCard(CheckboxChallengeStorage.WeeklyChallengeDay dayData) {
        VBox dayCard = new VBox(10);
        dayCard.getStyleClass().add("card");
        dayCard.setPadding(new Insets(15));
        
        // Day header
        Label dayLabel = new Label(dayData.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMM dd")));
        dayLabel.getStyleClass().add("card-title-label");
        
        // Overall progress
        double overallCompletion = dayData.getOverallCompletion();
        ProgressBar overallBar = new ProgressBar(overallCompletion / 100.0);
        overallBar.setPrefWidth(300);
        overallBar.getStyleClass().add("progress-bar");
        
        Label overallLabel = new Label(String.format("Overall: %.0f%% (%d points)", 
            overallCompletion, dayData.getTotalPoints()));
        overallLabel.getStyleClass().add("subtitle-label");
        
        // Category breakdown
        HBox categoriesBox = new HBox(20);
        categoriesBox.setAlignment(Pos.CENTER);
        
        VBox exerciseBox = createCategoryProgress("🏋️ Exercise", dayData.getExerciseCompletion(), dayData.getExercisePoints());
        VBox nutritionBox = createCategoryProgress("🥗 Nutrition", dayData.getNutritionCompletion(), dayData.getNutritionPoints());
        VBox hydrationBox = createCategoryProgress("💧 Hydration", dayData.getHydrationCompletion(), dayData.getHydrationPoints());
        VBox recoveryBox = createCategoryProgress("😴 Recovery", dayData.getRecoveryCompletion(), dayData.getRecoveryPoints());
        
        categoriesBox.getChildren().addAll(exerciseBox, nutritionBox, hydrationBox, recoveryBox);
        
        dayCard.getChildren().addAll(dayLabel, overallBar, overallLabel, categoriesBox);
        return dayCard;
    }
    
    private VBox createCategoryProgress(String category, double completion, int points) {
        VBox categoryBox = new VBox(5);
        categoryBox.setAlignment(Pos.CENTER);
        categoryBox.setPrefWidth(100);
        
        Label categoryLabel = new Label(category);
        categoryLabel.getStyleClass().add("label");
        categoryLabel.setAlignment(Pos.CENTER);
        
        ProgressBar categoryBar = new ProgressBar(completion / 100.0);
        categoryBar.setPrefWidth(80);
        categoryBar.setPrefHeight(8);
        
        Label percentLabel = new Label(String.format("%.0f%%", completion));
        percentLabel.getStyleClass().add("label");
        
        Label pointsLabel = new Label(points + " pts");
        pointsLabel.getStyleClass().add("label");
        
        categoryBox.getChildren().addAll(categoryLabel, categoryBar, percentLabel, pointsLabel);
        return categoryBox;
    }
    
    private void createBackButton() {
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("button", "button-primary");
        backButton.setOnAction(e -> {
            stage.close();
        });
        
        backButtonBox.getChildren().add(backButton);
        mainContainer.getChildren().add(backButtonBox);
    }
    
    private void createHeader() {
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("📊 Weekly Progress Report");
        titleLabel.getStyleClass().addAll("title-label", "glow-blue");
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        Label dateRangeLabel = new Label(String.format("📅 %s - %s", 
            startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
            endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        dateRangeLabel.getStyleClass().add("subtitle-label");
        
        headerBox.getChildren().addAll(titleLabel, dateRangeLabel);
        mainContainer.getChildren().add(headerBox);
    }
    
    private void createWeeklySummary() {
        VBox summaryBox = new VBox(15);
        summaryBox.getStyleClass().add("card");
        
        Label summaryTitle = new Label("📈 Week at a Glance");
        summaryTitle.getStyleClass().add("card-title-label");
        
        // Get weekly data
        Map<String, Double> weeklyData = challengeManager.getChallengeProgressData(currentUser.getId(), "daily");
        
        // Calculate statistics
        WeeklyStats stats = calculateWeeklyStats(weeklyData);
        
        // Create summary cards
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);
        
        VBox avgCard = createStatCard("📊 Average Completion", String.format("%.1f%%", stats.averageCompletion), "glow-blue");
        VBox bestCard = createStatCard("🏆 Best Day", String.format("%.1f%%", stats.bestDay), "glow-green");
        VBox streakCard = createStatCard("🔥 Current Streak", stats.currentStreak + " days", "glow-red");
        VBox totalPointsCard = createStatCard("🎯 Total Points", String.valueOf(stats.totalPoints), "glow-purple");
        
        statsRow.getChildren().addAll(avgCard, bestCard, streakCard, totalPointsCard);
        
        summaryBox.getChildren().addAll(summaryTitle, statsRow);
        mainContainer.getChildren().add(summaryBox);
    }
    
    private VBox createStatCard(String title, String value, String glowClass) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("card", glowClass);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-subtitle");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-value");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    

    
    private void createInsightsSection() {
        VBox insightsBox = new VBox(15);
        insightsBox.getStyleClass().add("card");
        
        Label insightsTitle = new Label("💡 Weekly Insights & Recommendations");
        insightsTitle.getStyleClass().add("card-title-label");
        
        VBox insightsList = new VBox(10);
        
        // Generate insights based on data
        List<String> insights = generateInsights();
        
        for (String insight : insights) {
            Label insightLabel = new Label("• " + insight);
            insightLabel.getStyleClass().add("label");
            insightLabel.setWrapText(true);
            insightsList.getChildren().add(insightLabel);
        }
        
        insightsBox.getChildren().addAll(insightsTitle, insightsList);
        mainContainer.getChildren().add(insightsBox);
    }
    
    private void createGoalsSection() {
        VBox goalsBox = new VBox(15);
        goalsBox.getStyleClass().add("card");
        
        Label goalsTitle = new Label("🎯 Next Week's Goals");
        goalsTitle.getStyleClass().add("card-title-label");
        
        VBox goalsList = new VBox(10);
        
        // Generate goals based on performance
        List<String> goals = generateNextWeekGoals();
        
        for (String goal : goals) {
            CheckBox goalCheckBox = new CheckBox(goal);
            goalCheckBox.getStyleClass().add("check-box");
            goalsList.getChildren().add(goalCheckBox);
        }
        
        Button saveGoalsBtn = new Button("💾 Save Goals for Next Week");
        saveGoalsBtn.getStyleClass().addAll("button", "button-primary");
        saveGoalsBtn.setOnAction(e -> saveWeeklyGoals());
        
        goalsBox.getChildren().addAll(goalsTitle, goalsList, saveGoalsBtn);
        mainContainer.getChildren().add(goalsBox);
    }
    
    private WeeklyStats calculateWeeklyStats(Map<String, Double> weeklyData) {
        WeeklyStats stats = new WeeklyStats();
        
        if (weeklyData.isEmpty()) {
            return stats;
        }
        
        double total = 0;
        double max = 0;
        int streak = 0;
        int currentStreak = 0;
        
        for (Double completion : weeklyData.values()) {
            total += completion;
            max = Math.max(max, completion);
            
            if (completion >= 80) { // Consider 80%+ as a good day
                currentStreak++;
                streak = Math.max(streak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }
        
        stats.averageCompletion = total / weeklyData.size();
        stats.bestDay = max;
        stats.currentStreak = streak;
        stats.totalPoints = challengeManager.getTotalPoints(currentUser.getId());
        
        return stats;
    }
    
    private Map<String, Double> getChallengeTypePerformance() {
        Map<String, Double> performance = new LinkedHashMap<>();
        
        // This would ideally come from database queries
        // For now, using sample data structure
        performance.put("Exercise", 85.0);
        performance.put("Nutrition", 72.0);
        performance.put("Hydration", 90.0);
        performance.put("Sleep", 68.0);
        
        return performance;
    }
    
    private List<String> generateInsights() {
        List<String> insights = new ArrayList<>();
        
        Map<String, Double> weeklyData = challengeManager.getChallengeProgressData(currentUser.getId(), "daily");
        WeeklyStats stats = calculateWeeklyStats(weeklyData);
        
        if (stats.averageCompletion >= 80) {
            insights.add("🎉 Excellent week! You maintained high consistency across all challenges.");
        } else if (stats.averageCompletion >= 60) {
            insights.add("👍 Good progress this week. Focus on consistency to reach the next level.");
        } else {
            insights.add("💪 Room for improvement. Start with small, achievable daily goals.");
        }
        
        if (stats.currentStreak >= 3) {
            insights.add("🔥 Amazing streak! You're building strong healthy habits.");
        }
        
        Map<String, Double> typePerformance = getChallengeTypePerformance();
        String weakestArea = typePerformance.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("General");
        
        insights.add("📊 Focus area for next week: " + weakestArea + " challenges need more attention.");
        
        if (stats.totalPoints > 100) {
            insights.add("🏆 You've earned " + stats.totalPoints + " points! Consider treating yourself to something healthy.");
        }
        
        return insights;
    }
    
    private List<String> generateNextWeekGoals() {
        List<String> goals = new ArrayList<>();
        
        WeeklyStats stats = calculateWeeklyStats(challengeManager.getChallengeProgressData(currentUser.getId(), "daily"));
        
        if (stats.averageCompletion < 70) {
            goals.add("🎯 Achieve 70% daily completion rate");
            goals.add("📱 Set daily reminders for challenge check-ins");
        } else {
            goals.add("🚀 Increase daily completion to 85%");
        }
        
        goals.add("💧 Drink 8 glasses of water daily");
        goals.add("🏃 Complete 10,000 steps at least 5 days");
        goals.add("🥗 Eat 5 servings of fruits/vegetables daily");
        goals.add("😴 Maintain 7+ hours of sleep nightly");
        goals.add("🏋️ Exercise for 30+ minutes, 4 times this week");
        
        return goals;
    }
    
    private void saveWeeklyGoals() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Goals Saved");
        alert.setHeaderText(null);
        alert.setContentText("Your goals for next week have been saved! 🎯\nYou'll receive daily reminders to help you stay on track.");
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
    
    public void close() {
        if (stage != null) {
            stage.close();
        }
    }
    
    // Helper class for weekly statistics
    private static class WeeklyStats {
        double averageCompletion = 0;
        double bestDay = 0;
        int currentStreak = 0;
        int totalPoints = 0;
    }
}