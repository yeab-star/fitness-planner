package fitnessplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChallengeHistoryView {
    private Stage stage;
    private User currentUser;
    private CheckboxChallengeStorage checkboxStorage;
    private DailyChallengeService dailyChallengeService;
    
    public ChallengeHistoryView(User user) {
        this.currentUser = user;
        this.checkboxStorage = new CheckboxChallengeStorage();
        this.dailyChallengeService = new DailyChallengeService();
        initializeView();
    }
    
    private void initializeView() {
        stage = new Stage();
        stage.setTitle("Challenge History - " + currentUser.getName());
        
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.getStyleClass().add("container");
        
        // Back button
        Button backButton = new Button("← Back");
        backButton.getStyleClass().addAll("button", "button-primary");
        backButton.setOnAction(e -> stage.close());
        
        // Header
        Label titleLabel = new Label("📊 Challenge History");
        titleLabel.getStyleClass().addAll("title-label", "glow-blue");
        
        // Tab pane for different views
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");
        
        // Current Week Tab
        Tab currentWeekTab = new Tab("📅 Current Week");
        currentWeekTab.setClosable(false);
        currentWeekTab.setContent(createCurrentWeekView());
        
        // Last 4 Weeks Tab
        Tab last4WeeksTab = new Tab("📈 Last 4 Weeks");
        last4WeeksTab.setClosable(false);
        last4WeeksTab.setContent(createLast4WeeksView());
        
        // All Time Stats Tab
        Tab allTimeTab = new Tab("🏆 All Time Stats");
        allTimeTab.setClosable(false);
        allTimeTab.setContent(createAllTimeStatsView());
        
        tabPane.getTabs().addAll(currentWeekTab, last4WeeksTab, allTimeTab);
        
        mainContainer.getChildren().addAll(backButton, titleLabel, tabPane);
        
        // Scroll pane
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Root with background
        StackPane root = new StackPane();
        String backgroundClass = (currentUser.getGender() != null && currentUser.getGender().equalsIgnoreCase("Female")) 
            ? "female-dashboard-background" 
            : "male-dashboard-background";
        root.getStyleClass().add(backgroundClass);
        root.getChildren().add(scrollPane);
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        
        stage.setScene(scene);
    }
    
    private VBox createCurrentWeekView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        
        Label weekLabel = new Label(String.format("Week: %s - %s", 
            weekStart.format(DateTimeFormatter.ofPattern("MMM dd")),
            today.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        weekLabel.getStyleClass().add("card-title-label");
        
        // Get weekly data
        List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData = 
            checkboxStorage.getWeeklyChallengeData(currentUser.getId());
        
        // Weekly summary card
        VBox summaryCard = createWeeklySummaryCard(weeklyData);
        
        // Daily breakdown
        VBox dailyBreakdown = new VBox(15);
        Label breakdownTitle = new Label("📋 Daily Breakdown");
        breakdownTitle.getStyleClass().add("subtitle-label");
        
        for (CheckboxChallengeStorage.WeeklyChallengeDay dayData : weeklyData) {
            HBox dayRow = createDayRow(dayData);
            dailyBreakdown.getChildren().add(dayRow);
        }
        
        container.getChildren().addAll(weekLabel, summaryCard, breakdownTitle, dailyBreakdown);
        return container;
    }
    
    private VBox createLast4WeeksView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        
        Label titleLabel = new Label("📈 Last 4 Weeks Comparison");
        titleLabel.getStyleClass().add("card-title-label");
        
        // Get data for last 4 weeks
        List<WeekSummary> weekSummaries = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int weekOffset = 0; weekOffset < 4; weekOffset++) {
            LocalDate weekEnd = today.minusDays(weekOffset * 7);
            LocalDate weekStart = weekEnd.minusDays(6);
            
            List<CheckboxChallengeStorage.WeeklyChallengeDay> weekData = 
                getWeekData(weekStart, weekEnd);
            
            WeekSummary summary = calculateWeekSummary(weekStart, weekEnd, weekData);
            weekSummaries.add(summary);
        }
        
        // Create comparison cards
        VBox comparisonContainer = new VBox(15);
        for (WeekSummary summary : weekSummaries) {
            VBox weekCard = createWeekComparisonCard(summary);
            comparisonContainer.getChildren().add(weekCard);
        }
        
        container.getChildren().addAll(titleLabel, comparisonContainer);
        return container;
    }
    
    private VBox createAllTimeStatsView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        
        Label titleLabel = new Label("🏆 All Time Statistics");
        titleLabel.getStyleClass().add("card-title-label");
        
        // Get all time stats
        AllTimeStats stats = calculateAllTimeStats();
        
        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);
        
        // Row 1
        statsGrid.add(createStatCard("📅 Total Days", String.valueOf(stats.totalDays)), 0, 0);
        statsGrid.add(createStatCard("✅ Completed Days", String.valueOf(stats.completedDays)), 1, 0);
        statsGrid.add(createStatCard("🎯 Total Points", String.valueOf(stats.totalPoints)), 2, 0);
        
        // Row 2
        statsGrid.add(createStatCard("🔥 Current Streak", stats.currentStreak + " days"), 0, 1);
        statsGrid.add(createStatCard("⭐ Longest Streak", stats.longestStreak + " days"), 1, 1);
        statsGrid.add(createStatCard("📊 Avg Completion", String.format("%.1f%%", stats.avgCompletion)), 2, 1);
        
        // Row 3
        statsGrid.add(createStatCard("🏋️ Exercise Days", String.valueOf(stats.exerciseDays)), 0, 2);
        statsGrid.add(createStatCard("🥗 Nutrition Days", String.valueOf(stats.nutritionDays)), 1, 2);
        statsGrid.add(createStatCard("💧 Hydration Days", String.valueOf(stats.hydrationDays)), 2, 2);
        
        // Best week
        VBox bestWeekCard = new VBox(10);
        bestWeekCard.getStyleClass().add("card");
        bestWeekCard.setPadding(new Insets(20));
        bestWeekCard.setAlignment(Pos.CENTER);
        
        Label bestWeekTitle = new Label("🌟 Best Week Ever");
        bestWeekTitle.getStyleClass().add("subtitle-label");
        
        Label bestWeekInfo = new Label(String.format("%s - %s", 
            stats.bestWeekStart != null ? stats.bestWeekStart.format(DateTimeFormatter.ofPattern("MMM dd")) : "N/A",
            stats.bestWeekEnd != null ? stats.bestWeekEnd.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A"));
        bestWeekInfo.getStyleClass().add("label");
        
        Label bestWeekPoints = new Label(stats.bestWeekPoints + " points");
        bestWeekPoints.getStyleClass().add("stat-value-label");
        
        bestWeekCard.getChildren().addAll(bestWeekTitle, bestWeekInfo, bestWeekPoints);
        
        container.getChildren().addAll(titleLabel, statsGrid, bestWeekCard);
        return container;
    }
    
    private VBox createWeeklySummaryCard(List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        
        Label titleLabel = new Label("📊 Week Summary");
        titleLabel.getStyleClass().add("subtitle-label");
        
        // Calculate totals
        int totalPoints = 0;
        double totalCompletion = 0;
        int daysWithData = 0;
        
        for (CheckboxChallengeStorage.WeeklyChallengeDay day : weeklyData) {
            totalPoints += day.getTotalPoints();
            double dayCompletion = day.getOverallCompletion();
            if (dayCompletion > 0) {
                totalCompletion += dayCompletion;
                daysWithData++;
            }
        }
        
        double avgCompletion = daysWithData > 0 ? totalCompletion / daysWithData : 0;
        
        // Stats display
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);
        
        VBox pointsBox = createStatBox("🎯 Total Points", String.valueOf(totalPoints));
        VBox avgBox = createStatBox("📊 Avg Completion", String.format("%.1f%%", avgCompletion));
        VBox daysBox = createStatBox("✅ Active Days", daysWithData + "/7");
        
        statsBox.getChildren().addAll(pointsBox, avgBox, daysBox);
        
        card.getChildren().addAll(titleLabel, statsBox);
        return card;
    }
    
    private HBox createDayRow(CheckboxChallengeStorage.WeeklyChallengeDay dayData) {
        HBox row = new HBox(20);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(15));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Date
        Label dateLabel = new Label(dayData.getDate().format(DateTimeFormatter.ofPattern("EEE, MMM dd")));
        dateLabel.getStyleClass().add("label");
        dateLabel.setPrefWidth(120);
        
        // Overall progress
        ProgressBar overallBar = new ProgressBar(dayData.getOverallCompletion() / 100.0);
        overallBar.setPrefWidth(200);
        overallBar.setPrefHeight(20);
        overallBar.getStyleClass().add("progress-bar");
        
        Label percentLabel = new Label(String.format("%.0f%%", dayData.getOverallCompletion()));
        percentLabel.getStyleClass().add("label");
        percentLabel.setPrefWidth(60);
        
        // Points
        Label pointsLabel = new Label(dayData.getTotalPoints() + " pts");
        pointsLabel.getStyleClass().add("stat-value-label");
        pointsLabel.setPrefWidth(80);
        
        // Category mini bars
        HBox categoriesBox = new HBox(10);
        categoriesBox.getChildren().addAll(
            createMiniProgressBar("🏋️", dayData.getExerciseCompletion()),
            createMiniProgressBar("🥗", dayData.getNutritionCompletion()),
            createMiniProgressBar("💧", dayData.getHydrationCompletion()),
            createMiniProgressBar("😴", dayData.getRecoveryCompletion())
        );
        
        row.getChildren().addAll(dateLabel, overallBar, percentLabel, pointsLabel, categoriesBox);
        return row;
    }
    
    private VBox createMiniProgressBar(String emoji, double completion) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 14px;");
        
        ProgressBar bar = new ProgressBar(completion / 100.0);
        bar.setPrefWidth(40);
        bar.setPrefHeight(6);
        
        box.getChildren().addAll(emojiLabel, bar);
        return box;
    }
    
    private VBox createWeekComparisonCard(WeekSummary summary) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));
        
        Label weekLabel = new Label(String.format("%s - %s", 
            summary.startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
            summary.endDate.format(DateTimeFormatter.ofPattern("MMM dd"))));
        weekLabel.getStyleClass().add("subtitle-label");
        
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        statsBox.getChildren().addAll(
            createSmallStatBox("Points", String.valueOf(summary.totalPoints)),
            createSmallStatBox("Avg", String.format("%.0f%%", summary.avgCompletion)),
            createSmallStatBox("Days", summary.activeDays + "/7")
        );
        
        card.getChildren().addAll(weekLabel, statsBox);
        return card;
    }
    
    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("label");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value-label");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private VBox createStatBox(String title, String value) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("label");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value-label");
        
        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }
    
    private VBox createSmallStatBox(String title, String value) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("label");
        titleLabel.setStyle("-fx-font-size: 12px;");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("label");
        valueLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }
    
    private List<CheckboxChallengeStorage.WeeklyChallengeDay> getWeekData(LocalDate startDate, LocalDate endDate) {
        return checkboxStorage.getDateRangeChallengeData(currentUser.getId(), startDate, endDate);
    }
    
    private WeekSummary calculateWeekSummary(LocalDate startDate, LocalDate endDate, 
                                            List<CheckboxChallengeStorage.WeeklyChallengeDay> weekData) {
        WeekSummary summary = new WeekSummary();
        summary.startDate = startDate;
        summary.endDate = endDate;
        
        int totalPoints = 0;
        double totalCompletion = 0;
        int activeDays = 0;
        
        for (CheckboxChallengeStorage.WeeklyChallengeDay day : weekData) {
            totalPoints += day.getTotalPoints();
            double dayCompletion = day.getOverallCompletion();
            if (dayCompletion > 0) {
                totalCompletion += dayCompletion;
                activeDays++;
            }
        }
        
        summary.totalPoints = totalPoints;
        summary.avgCompletion = activeDays > 0 ? totalCompletion / activeDays : 0;
        summary.activeDays = activeDays;
        
        return summary;
    }
    
    private AllTimeStats calculateAllTimeStats() {
        AllTimeStats stats = new AllTimeStats();
        
        // Get all-time statistics from database
        Map<String, Object> dbStats = checkboxStorage.getAllTimeStats(currentUser.getId());
        
        stats.totalDays = (Integer) dbStats.getOrDefault("totalDays", 0);
        stats.totalPoints = (Integer) dbStats.getOrDefault("totalPoints", 0);
        stats.exerciseDays = (Integer) dbStats.getOrDefault("exerciseDays", 0);
        stats.nutritionDays = (Integer) dbStats.getOrDefault("nutritionDays", 0);
        stats.hydrationDays = (Integer) dbStats.getOrDefault("hydrationDays", 0);
        
        // Calculate streaks
        stats.currentStreak = checkboxStorage.getCurrentStreak(currentUser.getId());
        stats.longestStreak = checkboxStorage.getLongestStreak(currentUser.getId());
        
        // Calculate completed days (days with >= 75% completion)
        stats.completedDays = calculateCompletedDays();
        
        // Calculate average completion
        stats.avgCompletion = stats.totalDays > 0 ? (stats.completedDays * 100.0) / stats.totalDays : 0;
        
        // Find best week
        findBestWeek(stats);
        
        return stats;
    }
    
    private int calculateCompletedDays() {
        int completedDays = 0;
        
        // Get all days with data
        Map<String, Object> dbStats = checkboxStorage.getAllTimeStats(currentUser.getId());
        LocalDate firstDate = (LocalDate) dbStats.get("firstDate");
        LocalDate lastDate = (LocalDate) dbStats.get("lastDate");
        
        if (firstDate != null && lastDate != null) {
            for (LocalDate date = firstDate; !date.isAfter(lastDate); date = date.plusDays(1)) {
                List<CheckboxChallengeStorage.WeeklyChallengeDay> dayData = 
                    checkboxStorage.getDateRangeChallengeData(currentUser.getId(), date, date);
                
                if (!dayData.isEmpty() && dayData.get(0).getOverallCompletion() >= 75) {
                    completedDays++;
                }
            }
        }
        
        return completedDays;
    }
    
    private void findBestWeek(AllTimeStats stats) {
        Map<String, Object> dbStats = checkboxStorage.getAllTimeStats(currentUser.getId());
        LocalDate firstDate = (LocalDate) dbStats.get("firstDate");
        LocalDate lastDate = (LocalDate) dbStats.get("lastDate");
        
        if (firstDate == null || lastDate == null) {
            return;
        }
        
        int bestPoints = 0;
        LocalDate bestStart = null;
        LocalDate bestEnd = null;
        
        // Check each week
        for (LocalDate weekStart = firstDate; !weekStart.isAfter(lastDate); weekStart = weekStart.plusWeeks(1)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(lastDate)) {
                weekEnd = lastDate;
            }
            
            List<CheckboxChallengeStorage.WeeklyChallengeDay> weekData = 
                checkboxStorage.getDateRangeChallengeData(currentUser.getId(), weekStart, weekEnd);
            
            int weekPoints = 0;
            for (CheckboxChallengeStorage.WeeklyChallengeDay day : weekData) {
                weekPoints += day.getTotalPoints();
            }
            
            if (weekPoints > bestPoints) {
                bestPoints = weekPoints;
                bestStart = weekStart;
                bestEnd = weekEnd;
            }
        }
        
        stats.bestWeekStart = bestStart;
        stats.bestWeekEnd = bestEnd;
        stats.bestWeekPoints = bestPoints;
    }
    
    public void show() {
        stage.show();
    }
    
    // Helper classes
    private static class WeekSummary {
        LocalDate startDate;
        LocalDate endDate;
        int totalPoints;
        double avgCompletion;
        int activeDays;
    }
    
    private static class AllTimeStats {
        int totalDays;
        int completedDays;
        int totalPoints;
        int currentStreak;
        int longestStreak;
        double avgCompletion;
        int exerciseDays;
        int nutritionDays;
        int hydrationDays;
        LocalDate bestWeekStart;
        LocalDate bestWeekEnd;
        int bestWeekPoints;
    }
}
