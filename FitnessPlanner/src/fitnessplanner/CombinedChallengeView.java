package fitnessplanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CombinedChallengeView {
    private Stage stage;
    private User currentUser;
    private VBox mainContainer;
    private TabPane tabPane;
    private StackPane contentPane; // For switching between challenge and history views
    private ScrollPane mainScrollPane;
    
    // Daily challenge components
    private Map<String, CheckBox> exerciseCheckBoxes;
    private Map<String, CheckBox> nutritionCheckBoxes;
    private Map<String, CheckBox> hydrationCheckBoxes;
    private Map<String, CheckBox> recoveryCheckBoxes;
    private Label progressLabel;
    private ProgressBar dailyProgressBar;
    private Label pointsLabel;
    private Label motivationLabel;
    private CheckboxChallengeStorage storage;
    
    // Weekly challenge components
    private CheckboxChallengeStorage checkboxStorage;
    
    public CombinedChallengeView(User user) {
        this.currentUser = user;
        this.exerciseCheckBoxes = new HashMap<>();
        this.nutritionCheckBoxes = new HashMap<>();
        this.hydrationCheckBoxes = new HashMap<>();
        this.recoveryCheckBoxes = new HashMap<>();
        this.storage = new CheckboxChallengeStorage();
        this.checkboxStorage = new CheckboxChallengeStorage();
        this.storage.initializeTables();
        initializeView();
    }
    
    private void initializeView() {
        stage = new Stage();
        stage.setTitle("Fitness Challenges - " + currentUser.getName());
        
        // Create content pane for switching views
        contentPane = new StackPane();
        
        // Create main challenge view
        VBox challengeView = createChallengeView();
        
        // Scroll pane
        mainScrollPane = new ScrollPane(challengeView);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.getStyleClass().add("scroll-pane");
        mainScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        contentPane.getChildren().add(mainScrollPane);
        
        // Wrap in StackPane with gender-specific background
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        String backgroundClass = (currentUser.getGender() != null && currentUser.getGender().equalsIgnoreCase("Female")) 
            ? "female-dashboard-background" 
            : "male-dashboard-background";
        root.getStyleClass().add(backgroundClass);
        root.getChildren().add(contentPane);
        
        // Scene
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        
        stage.setScene(scene);
        
        // Load saved checkbox states from database
        loadSavedCheckboxStates();
        
        // Initialize progress
        updateProgressDisplay();
    }
    
    private VBox createChallengeView() {
        // Main container
        mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.getStyleClass().add("container");
        
        // Create back button
        createBackButton();
        
        // Create header
        createHeader();
        
        // Create tab pane for daily and weekly views
        createTabPane();
        
        return mainContainer;
    }
    
    private void createBackButton() {
        HBox backButtonBox = new HBox(15);
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("button", "button-primary");
        backButton.setOnAction(e -> stage.close());
        
        Button historyButton = new Button("📊 View History");
        historyButton.getStyleClass().addAll("button", "button-secondary");
        historyButton.setOnAction(e -> showHistoryView());
        
        // Add spacer to push logout button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logoutButton = new Button("🚪 Logout");
        logoutButton.getStyleClass().addAll("button");
        logoutButton.setMinWidth(120);
        logoutButton.setPrefWidth(120);
        logoutButton.setStyle(
            "-fx-background-color: #f85149;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnAction(e -> handleLogout());
        
        backButtonBox.getChildren().addAll(backButton, historyButton, spacer, logoutButton);
        mainContainer.getChildren().add(backButtonBox);
    }
    
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Are you sure you want to logout?");
        confirm.setContentText("Your progress will be saved.");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            stage.close();
            new LoginView().show(new Stage());
        }
    }
    
    private void createHeader() {
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("🏆 Fitness Challenges");
        titleLabel.getStyleClass().addAll("title-label", "glow-blue");
        
        Label dateLabel = new Label("📅 " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        dateLabel.getStyleClass().add("subtitle-label");
        
        headerBox.getChildren().addAll(titleLabel, dateLabel);
        mainContainer.getChildren().add(headerBox);
    }
    
    private void createTabPane() {
        tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");
        
        // Daily Challenges Tab
        Tab dailyTab = new Tab("🎯 Daily Challenges");
        dailyTab.setClosable(false);
        dailyTab.setContent(createDailyContent());
        
        // Weekly Progress Tab
        Tab weeklyTab = new Tab("📊 Weekly Progress");
        weeklyTab.setClosable(false);
        weeklyTab.setContent(createWeeklyContent());
        
        tabPane.getTabs().addAll(dailyTab, weeklyTab);
        mainContainer.getChildren().add(tabPane);
    }
    
    private VBox createDailyContent() {
        VBox dailyContainer = new VBox(20);
        dailyContainer.setPadding(new Insets(20));
        
        // Daily motivation
        motivationLabel = new Label(DailyRecommendationService.getDailyMotivation(LocalDate.now()));
        motivationLabel.getStyleClass().addAll("motivation-text", "glow-green");
        motivationLabel.setWrapText(true);
        motivationLabel.setMaxWidth(800);
        motivationLabel.setAlignment(Pos.CENTER);
        
        // Daily progress section
        VBox progressBox = new VBox(15);
        progressBox.getStyleClass().add("card");
        progressBox.setAlignment(Pos.CENTER);
        
        Label progressTitle = new Label("📊 Today's Progress");
        progressTitle.getStyleClass().add("card-title-label");
        
        dailyProgressBar = new ProgressBar(0);
        dailyProgressBar.setPrefWidth(500);
        dailyProgressBar.setPrefHeight(25);
        dailyProgressBar.getStyleClass().add("progress-bar");
        
        progressLabel = new Label("0% Complete");
        progressLabel.getStyleClass().add("stat-value-label");
        
        pointsLabel = new Label("🎯 Points Today: 0");
        pointsLabel.getStyleClass().add("subtitle-label");
        
        progressBox.getChildren().addAll(progressTitle, dailyProgressBar, progressLabel, pointsLabel);
        
        // Today's results summary (category breakdown)
        VBox todayResultsCard = createTodayResultsCard();
        
        // Challenge sections
        HBox challengeContainer = new HBox(20);
        challengeContainer.setAlignment(Pos.TOP_CENTER);
        
        VBox exerciseColumn = createExerciseSection();
        VBox nutritionColumn = createNutritionSection();
        VBox hydrationColumn = createHydrationSection();
        VBox recoveryColumn = createRecoverySection();
        
        challengeContainer.getChildren().addAll(exerciseColumn, nutritionColumn, hydrationColumn, recoveryColumn);
        
        dailyContainer.getChildren().addAll(motivationLabel, progressBox, todayResultsCard, challengeContainer);
        return dailyContainer;
    }
    
    private VBox createTodayResultsCard() {
        VBox resultsCard = new VBox(15);
        resultsCard.getStyleClass().add("card");
        resultsCard.setPadding(new Insets(20));
        
        Label resultsTitle = new Label("📋 Today's Results by Category");
        resultsTitle.getStyleClass().add("subtitle-label");
        
        // Get today's data
        List<CheckboxChallengeStorage.WeeklyChallengeDay> todayData = 
            checkboxStorage.getDateRangeChallengeData(currentUser.getId(), LocalDate.now(), LocalDate.now());
        
        HBox categoriesBox = new HBox(20);
        categoriesBox.setAlignment(Pos.CENTER);
        
        if (!todayData.isEmpty()) {
            CheckboxChallengeStorage.WeeklyChallengeDay today = todayData.get(0);
            
            VBox exerciseBox = createCategoryResultBox("🏋️ Exercise", 
                today.getExerciseCompletion(), today.getExercisePoints());
            VBox nutritionBox = createCategoryResultBox("🥗 Nutrition", 
                today.getNutritionCompletion(), today.getNutritionPoints());
            VBox hydrationBox = createCategoryResultBox("💧 Hydration", 
                today.getHydrationCompletion(), today.getHydrationPoints());
            VBox recoveryBox = createCategoryResultBox("😴 Recovery", 
                today.getRecoveryCompletion(), today.getRecoveryPoints());
            
            categoriesBox.getChildren().addAll(exerciseBox, nutritionBox, hydrationBox, recoveryBox);
        } else {
            Label noDataLabel = new Label("Start checking off challenges to see your results!");
            noDataLabel.getStyleClass().add("label");
            categoriesBox.getChildren().add(noDataLabel);
        }
        
        resultsCard.getChildren().addAll(resultsTitle, categoriesBox);
        return resultsCard;
    }
    
    private VBox createCategoryResultBox(String category, double completion, int points) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("stat-box");
        box.setPadding(new Insets(15));
        box.setPrefWidth(150);
        
        Label categoryLabel = new Label(category);
        categoryLabel.getStyleClass().add("label");
        
        ProgressBar progressBar = new ProgressBar(completion / 100.0);
        progressBar.setPrefWidth(120);
        progressBar.setPrefHeight(10);
        progressBar.getStyleClass().add("progress-bar");
        
        Label percentLabel = new Label(String.format("%.0f%%", completion));
        percentLabel.getStyleClass().add("stat-value-label");
        
        Label pointsLabel = new Label(points + " points");
        pointsLabel.getStyleClass().add("label");
        
        box.getChildren().addAll(categoryLabel, progressBar, percentLabel, pointsLabel);
        return box;
    }
    
    private VBox createWeeklyContent() {
        VBox weeklyContainer = new VBox(20);
        weeklyContainer.setPadding(new Insets(20));
        
        // Week date range
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        Label weeklyTitle = new Label(String.format("📋 Weekly Progress: %s - %s", 
            weekStart.format(DateTimeFormatter.ofPattern("MMM dd")),
            today.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        weeklyTitle.getStyleClass().add("card-title-label");
        
        // Get weekly checkbox data
        List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData = checkboxStorage.getWeeklyChallengeData(currentUser.getId());
        
        // Weekly summary statistics
        VBox summaryCard = createWeeklySummaryStats(weeklyData);
        
        // Daily progress cards
        VBox progressContainer = new VBox(15);
        
        for (CheckboxChallengeStorage.WeeklyChallengeDay dayData : weeklyData) {
            VBox dayCard = createDayProgressCard(dayData);
            progressContainer.getChildren().add(dayCard);
        }
        
        ScrollPane weeklyScrollPane = new ScrollPane(progressContainer);
        weeklyScrollPane.setFitToWidth(true);
        weeklyScrollPane.setPrefHeight(400);
        weeklyScrollPane.getStyleClass().add("scroll-pane");
        weeklyScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        weeklyContainer.getChildren().addAll(weeklyTitle, summaryCard, weeklyScrollPane);
        return weeklyContainer;
    }
    
    private VBox createWeeklySummaryStats(List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData) {
        VBox summaryCard = new VBox(15);
        summaryCard.getStyleClass().add("card");
        summaryCard.setPadding(new Insets(20));
        
        Label summaryTitle = new Label("📊 This Week's Summary");
        summaryTitle.getStyleClass().add("subtitle-label");
        
        // Calculate weekly totals
        int totalPoints = 0;
        double totalCompletion = 0;
        int activeDays = 0;
        double totalExercise = 0;
        double totalNutrition = 0;
        double totalHydration = 0;
        double totalRecovery = 0;
        
        for (CheckboxChallengeStorage.WeeklyChallengeDay day : weeklyData) {
            int dayPoints = day.getTotalPoints();
            if (dayPoints > 0) {
                activeDays++;
            }
            totalPoints += dayPoints;
            totalCompletion += day.getOverallCompletion();
            totalExercise += day.getExerciseCompletion();
            totalNutrition += day.getNutritionCompletion();
            totalHydration += day.getHydrationCompletion();
            totalRecovery += day.getRecoveryCompletion();
        }
        
        double avgCompletion = weeklyData.size() > 0 ? totalCompletion / weeklyData.size() : 0;
        double avgExercise = weeklyData.size() > 0 ? totalExercise / weeklyData.size() : 0;
        double avgNutrition = weeklyData.size() > 0 ? totalNutrition / weeklyData.size() : 0;
        double avgHydration = weeklyData.size() > 0 ? totalHydration / weeklyData.size() : 0;
        double avgRecovery = weeklyData.size() > 0 ? totalRecovery / weeklyData.size() : 0;
        
        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);
        statsGrid.setAlignment(Pos.CENTER);
        
        // Row 1: Main stats
        statsGrid.add(createWeekStatBox("🎯 Total Points", String.valueOf(totalPoints)), 0, 0);
        statsGrid.add(createWeekStatBox("📊 Avg Completion", String.format("%.1f%%", avgCompletion)), 1, 0);
        statsGrid.add(createWeekStatBox("✅ Active Days", activeDays + "/7"), 2, 0);
        
        // Row 2: Category averages
        statsGrid.add(createWeekStatBox("🏋️ Exercise", String.format("%.0f%%", avgExercise)), 0, 1);
        statsGrid.add(createWeekStatBox("🥗 Nutrition", String.format("%.0f%%", avgNutrition)), 1, 1);
        statsGrid.add(createWeekStatBox("💧 Hydration", String.format("%.0f%%", avgHydration)), 2, 1);
        
        // Performance message
        Label performanceLabel = new Label(getWeeklyPerformanceMessage(avgCompletion, activeDays));
        performanceLabel.getStyleClass().addAll("motivation-text", "glow-purple");
        performanceLabel.setWrapText(true);
        performanceLabel.setMaxWidth(600);
        performanceLabel.setAlignment(Pos.CENTER);
        
        summaryCard.getChildren().addAll(summaryTitle, statsGrid, performanceLabel);
        return summaryCard;
    }
    
    private VBox createWeekStatBox(String title, String value) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("stat-box");
        box.setPadding(new Insets(15));
        box.setPrefWidth(180);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("label");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value-label");
        
        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }
    
    private String getWeeklyPerformanceMessage(double avgCompletion, int activeDays) {
        if (activeDays == 7 && avgCompletion >= 90) {
            return "🌟 PERFECT WEEK! You're absolutely crushing it! Keep this momentum going!";
        } else if (avgCompletion >= 80) {
            return "⭐ Excellent week! You're showing great consistency and dedication!";
        } else if (avgCompletion >= 60) {
            return "👍 Good progress this week! Keep building those healthy habits!";
        } else if (avgCompletion >= 40) {
            return "💪 You're making progress! Try to stay more consistent this coming week!";
        } else if (activeDays > 0) {
            return "🎯 Every step counts! Let's aim for more consistency next week!";
        } else {
            return "🚀 Ready to start fresh? This week is your opportunity to shine!";
        }
    }
    
    private VBox createExerciseSection() {
        VBox exerciseBox = new VBox(15);
        exerciseBox.getStyleClass().add("card");
        exerciseBox.setPrefWidth(230);
        
        Label sectionTitle = new Label("🏋️ Exercise");
        sectionTitle.getStyleClass().add("card-title-label");
        
        VBox checkboxContainer = new VBox(10);
        
        List<String> exercises = DailyRecommendationService.getDailyExercises(LocalDate.now());
        
        for (String exercise : exercises) {
            CheckBox checkBox = new CheckBox(exercise);
            checkBox.getStyleClass().add("check-box");
            checkBox.setWrapText(true);
            checkBox.setMaxWidth(200);
            checkBox.setOnAction(e -> updateProgressDisplay());
            
            exerciseCheckBoxes.put(exercise, checkBox);
            checkboxContainer.getChildren().add(checkBox);
        }
        
        exerciseBox.getChildren().addAll(sectionTitle, checkboxContainer);
        return exerciseBox;
    }
    
    private VBox createNutritionSection() {
        VBox nutritionBox = new VBox(15);
        nutritionBox.getStyleClass().add("card");
        nutritionBox.setPrefWidth(230);
        
        Label sectionTitle = new Label("🥗 Nutrition");
        sectionTitle.getStyleClass().add("card-title-label");
        
        VBox checkboxContainer = new VBox(10);
        
        List<String> nutrition = DailyRecommendationService.getDailyNutrition(LocalDate.now());
        
        for (String food : nutrition) {
            CheckBox checkBox = new CheckBox(food);
            checkBox.getStyleClass().add("check-box");
            checkBox.setWrapText(true);
            checkBox.setMaxWidth(200);
            checkBox.setOnAction(e -> updateProgressDisplay());
            
            nutritionCheckBoxes.put(food, checkBox);
            checkboxContainer.getChildren().add(checkBox);
        }
        
        nutritionBox.getChildren().addAll(sectionTitle, checkboxContainer);
        return nutritionBox;
    }
    
    private VBox createHydrationSection() {
        VBox hydrationBox = new VBox(15);
        hydrationBox.getStyleClass().add("card");
        hydrationBox.setPrefWidth(230);
        
        Label sectionTitle = new Label("💧 Hydration");
        sectionTitle.getStyleClass().add("card-title-label");
        
        VBox checkboxContainer = new VBox(10);
        
        List<String> hydration = DailyRecommendationService.getDailyHydration(LocalDate.now());
        
        for (String drink : hydration) {
            CheckBox checkBox = new CheckBox(drink);
            checkBox.getStyleClass().add("check-box");
            checkBox.setWrapText(true);
            checkBox.setMaxWidth(200);
            checkBox.setOnAction(e -> updateProgressDisplay());
            
            hydrationCheckBoxes.put(drink, checkBox);
            checkboxContainer.getChildren().add(checkBox);
        }
        
        hydrationBox.getChildren().addAll(sectionTitle, checkboxContainer);
        return hydrationBox;
    }
    
    private VBox createRecoverySection() {
        VBox recoveryBox = new VBox(15);
        recoveryBox.getStyleClass().add("card");
        recoveryBox.setPrefWidth(230);
        
        Label sectionTitle = new Label("😴 Recovery");
        sectionTitle.getStyleClass().add("card-title-label");
        
        VBox checkboxContainer = new VBox(10);
        
        List<String> recovery = DailyRecommendationService.getDailyRecovery(LocalDate.now());
        
        for (String item : recovery) {
            CheckBox checkBox = new CheckBox(item);
            checkBox.getStyleClass().add("check-box");
            checkBox.setWrapText(true);
            checkBox.setMaxWidth(200);
            checkBox.setOnAction(e -> updateProgressDisplay());
            
            recoveryCheckBoxes.put(item, checkBox);
            checkboxContainer.getChildren().add(checkBox);
        }
        
        recoveryBox.getChildren().addAll(sectionTitle, checkboxContainer);
        return recoveryBox;
    }
    
    private VBox createDayProgressCard(CheckboxChallengeStorage.WeeklyChallengeDay dayData) {
        VBox dayCard = new VBox(10);
        dayCard.getStyleClass().add("card");
        dayCard.setPadding(new Insets(15));
        
        Label dayLabel = new Label(dayData.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMM dd")));
        dayLabel.getStyleClass().add("card-title-label");
        
        double overallCompletion = dayData.getOverallCompletion();
        ProgressBar overallBar = new ProgressBar(overallCompletion / 100.0);
        overallBar.setPrefWidth(300);
        overallBar.getStyleClass().add("progress-bar");
        
        Label overallLabel = new Label(String.format("Overall: %.0f%% (%d points)", 
            overallCompletion, dayData.getTotalPoints()));
        overallLabel.getStyleClass().add("subtitle-label");
        
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
    
    private void loadSavedCheckboxStates() {
        try {
            // Get today's saved challenge status from database
            Map<String, Map<String, Boolean>> savedStatus = storage.getDailyChallengeStatus(currentUser.getId(), LocalDate.now());
            
            // Load exercise checkboxes
            Map<String, Boolean> exerciseStatus = savedStatus.get("EXERCISE");
            if (exerciseStatus != null) {
                for (Map.Entry<String, Boolean> entry : exerciseStatus.entrySet()) {
                    CheckBox checkBox = exerciseCheckBoxes.get(entry.getKey());
                    if (checkBox != null) {
                        checkBox.setSelected(entry.getValue());
                    }
                }
            }
            
            // Load nutrition checkboxes
            Map<String, Boolean> nutritionStatus = savedStatus.get("NUTRITION");
            if (nutritionStatus != null) {
                for (Map.Entry<String, Boolean> entry : nutritionStatus.entrySet()) {
                    CheckBox checkBox = nutritionCheckBoxes.get(entry.getKey());
                    if (checkBox != null) {
                        checkBox.setSelected(entry.getValue());
                    }
                }
            }
            
            // Load hydration checkboxes
            Map<String, Boolean> hydrationStatus = savedStatus.get("HYDRATION");
            if (hydrationStatus != null) {
                for (Map.Entry<String, Boolean> entry : hydrationStatus.entrySet()) {
                    CheckBox checkBox = hydrationCheckBoxes.get(entry.getKey());
                    if (checkBox != null) {
                        checkBox.setSelected(entry.getValue());
                    }
                }
            }
            
            // Load recovery checkboxes
            Map<String, Boolean> recoveryStatus = savedStatus.get("RECOVERY");
            if (recoveryStatus != null) {
                for (Map.Entry<String, Boolean> entry : recoveryStatus.entrySet()) {
                    CheckBox checkBox = recoveryCheckBoxes.get(entry.getKey());
                    if (checkBox != null) {
                        checkBox.setSelected(entry.getValue());
                    }
                }
            }
            
            System.out.println("Loaded saved checkbox states for user " + currentUser.getId());
        } catch (Exception e) {
            System.err.println("Error loading saved checkbox states: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateProgressDisplay() {
        Map<String, Boolean> exerciseStatus = getCheckboxStatus(exerciseCheckBoxes);
        Map<String, Boolean> nutritionStatus = getCheckboxStatus(nutritionCheckBoxes);
        Map<String, Boolean> hydrationStatus = getCheckboxStatus(hydrationCheckBoxes);
        Map<String, Boolean> recoveryStatus = getCheckboxStatus(recoveryCheckBoxes);
        
        int totalItems = exerciseStatus.size() + nutritionStatus.size() + 
                        hydrationStatus.size() + recoveryStatus.size();
        
        long completedItems = exerciseStatus.values().stream().mapToLong(b -> b ? 1 : 0).sum() +
                             nutritionStatus.values().stream().mapToLong(b -> b ? 1 : 0).sum() +
                             hydrationStatus.values().stream().mapToLong(b -> b ? 1 : 0).sum() +
                             recoveryStatus.values().stream().mapToLong(b -> b ? 1 : 0).sum();
        
        double progressPercentage = totalItems > 0 ? (completedItems * 100.0) / totalItems : 0;
        
        int points = DailyRecommendationService.calculatePoints(
            exerciseStatus, nutritionStatus, hydrationStatus, recoveryStatus);
        
        dailyProgressBar.setProgress(progressPercentage / 100.0);
        progressLabel.setText(String.format("%.0f%% Complete (%d/%d items)", 
            progressPercentage, completedItems, totalItems));
        pointsLabel.setText("🎯 Points Today: " + points);
        
        try {
            storage.saveDailyChallengeStatus(currentUser.getId(), LocalDate.now(),
                exerciseStatus, nutritionStatus, hydrationStatus, recoveryStatus);
            
            // Refresh the daily tab to show updated results
            refreshDailyTab();
        } catch (Exception e) {
            System.err.println("Error saving challenge progress: " + e.getMessage());
        }
        
        if (progressPercentage == 100) {
            showCelebrationMessage("🎉 Perfect Day! All challenges completed!");
        } else if (progressPercentage >= 75) {
            showCelebrationMessage("⭐ Excellent progress! Keep it up!");
        } else if (progressPercentage >= 50) {
            showCelebrationMessage("👍 Great job! You're halfway there!");
        }
    }
    
    private void refreshDailyTab() {
        // Find the daily tab and refresh its results card
        if (tabPane != null && tabPane.getTabs().size() > 0) {
            Tab dailyTab = tabPane.getTabs().get(0);
            VBox dailyContent = (VBox) dailyTab.getContent();
            
            // Find and update the results card (it's the 3rd child after motivation and progress)
            if (dailyContent.getChildren().size() >= 3) {
                VBox newResultsCard = createTodayResultsCard();
                dailyContent.getChildren().set(2, newResultsCard);
            }
        }
    }
    
    private Map<String, Boolean> getCheckboxStatus(Map<String, CheckBox> checkboxMap) {
        Map<String, Boolean> status = new HashMap<>();
        for (Map.Entry<String, CheckBox> entry : checkboxMap.entrySet()) {
            status.put(entry.getKey(), entry.getValue().isSelected());
        }
        return status;
    }
    
    private void showCelebrationMessage(String message) {
        String originalText = motivationLabel.getText();
        motivationLabel.setText(message);
        motivationLabel.getStyleClass().removeAll("glow-green");
        motivationLabel.getStyleClass().add("glow-purple");
        
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            motivationLabel.setText(originalText);
            motivationLabel.getStyleClass().removeAll("glow-purple");
            motivationLabel.getStyleClass().add("glow-green");
        }));
        timeline.play();
    }
    
    private void showHistoryView() {
        // Create history content
        VBox historyContent = createHistoryContent();
        
        // Replace content in scroll pane
        mainScrollPane.setContent(historyContent);
    }
    
    private void showChallengeView() {
        // Recreate challenge view
        VBox challengeView = createChallengeView();
        
        // Replace content in scroll pane
        mainScrollPane.setContent(challengeView);
        
        // Load saved checkbox states
        loadSavedCheckboxStates();
        
        // Refresh progress
        updateProgressDisplay();
    }
    
    private VBox createHistoryContent() {
        VBox historyContainer = new VBox(20);
        historyContainer.setPadding(new Insets(30));
        historyContainer.getStyleClass().add("container");
        
        // Back button to return to challenges
        HBox backButtonBox = new HBox(15);
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Challenges");
        backButton.getStyleClass().addAll("button", "button-primary");
        backButton.setOnAction(e -> showChallengeView());
        
        // Add spacer to push logout button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logoutButton = new Button("🚪 Logout");
        logoutButton.getStyleClass().addAll("button");
        logoutButton.setMinWidth(120);
        logoutButton.setPrefWidth(120);
        logoutButton.setStyle(
            "-fx-background-color: #f85149;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnAction(e -> handleLogout());
        
        backButtonBox.getChildren().addAll(backButton, spacer, logoutButton);
        
        // Header
        Label titleLabel = new Label("📊 Challenge History");
        titleLabel.getStyleClass().addAll("title-label", "glow-blue");
        
        // Tab pane for different history views
        TabPane historyTabPane = new TabPane();
        historyTabPane.getStyleClass().add("tab-pane");
        
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
        
        historyTabPane.getTabs().addAll(currentWeekTab, last4WeeksTab, allTimeTab);
        
        historyContainer.getChildren().addAll(backButtonBox, titleLabel, historyTabPane);
        return historyContainer;
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
        VBox summaryCard = createHistoryWeeklySummaryCard(weeklyData);
        
        // Daily breakdown
        VBox dailyBreakdown = new VBox(15);
        Label breakdownTitle = new Label("📋 Daily Breakdown");
        breakdownTitle.getStyleClass().add("subtitle-label");
        
        for (CheckboxChallengeStorage.WeeklyChallengeDay dayData : weeklyData) {
            HBox dayRow = createHistoryDayRow(dayData);
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
        VBox comparisonContainer = new VBox(15);
        LocalDate today = LocalDate.now();
        
        for (int weekOffset = 0; weekOffset < 4; weekOffset++) {
            LocalDate weekEnd = today.minusDays(weekOffset * 7);
            LocalDate weekStart = weekEnd.minusDays(6);
            
            List<CheckboxChallengeStorage.WeeklyChallengeDay> weekData = 
                checkboxStorage.getDateRangeChallengeData(currentUser.getId(), weekStart, weekEnd);
            
            VBox weekCard = createWeekComparisonCard(weekStart, weekEnd, weekData);
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
        Map<String, Object> dbStats = checkboxStorage.getAllTimeStats(currentUser.getId());
        
        int totalDays = (Integer) dbStats.getOrDefault("totalDays", 0);
        int totalPoints = (Integer) dbStats.getOrDefault("totalPoints", 0);
        int exerciseDays = (Integer) dbStats.getOrDefault("exerciseDays", 0);
        int nutritionDays = (Integer) dbStats.getOrDefault("nutritionDays", 0);
        int hydrationDays = (Integer) dbStats.getOrDefault("hydrationDays", 0);
        int currentStreak = checkboxStorage.getCurrentStreak(currentUser.getId());
        int longestStreak = checkboxStorage.getLongestStreak(currentUser.getId());
        
        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);
        
        // Row 1
        statsGrid.add(createStatCard("📅 Total Days", String.valueOf(totalDays)), 0, 0);
        statsGrid.add(createStatCard("🎯 Total Points", String.valueOf(totalPoints)), 1, 0);
        statsGrid.add(createStatCard("🔥 Current Streak", currentStreak + " days"), 2, 0);
        
        // Row 2
        statsGrid.add(createStatCard("⭐ Longest Streak", longestStreak + " days"), 0, 1);
        statsGrid.add(createStatCard("🏋️ Exercise Days", String.valueOf(exerciseDays)), 1, 1);
        statsGrid.add(createStatCard("🥗 Nutrition Days", String.valueOf(nutritionDays)), 2, 1);
        
        container.getChildren().addAll(titleLabel, statsGrid);
        return container;
    }
    
    private VBox createHistoryWeeklySummaryCard(List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData) {
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
    
    private HBox createHistoryDayRow(CheckboxChallengeStorage.WeeklyChallengeDay dayData) {
        HBox row = new HBox(20);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(15));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Date - increased width to prevent cutoff
        Label dateLabel = new Label(dayData.getDate().format(DateTimeFormatter.ofPattern("EEE, MMM dd")));
        dateLabel.getStyleClass().add("label");
        dateLabel.setMinWidth(140);
        dateLabel.setPrefWidth(140);
        dateLabel.setMaxWidth(140);
        dateLabel.setWrapText(false); // Prevent wrapping, ensure single line
        
        // Overall progress
        ProgressBar overallBar = new ProgressBar(dayData.getOverallCompletion() / 100.0);
        overallBar.setPrefWidth(200);
        overallBar.setPrefHeight(20);
        overallBar.getStyleClass().add("progress-bar");
        
        Label percentLabel = new Label(String.format("%.0f%%", dayData.getOverallCompletion()));
        percentLabel.getStyleClass().add("label");
        percentLabel.setMinWidth(60);
        percentLabel.setPrefWidth(60);
        
        // Points
        Label pointsLabel = new Label(dayData.getTotalPoints() + " pts");
        pointsLabel.getStyleClass().add("stat-value-label");
        pointsLabel.setMinWidth(80);
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
    
    private VBox createWeekComparisonCard(LocalDate startDate, LocalDate endDate, 
                                         List<CheckboxChallengeStorage.WeeklyChallengeDay> weekData) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));
        
        Label weekLabel = new Label(String.format("%s - %s", 
            startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
            endDate.format(DateTimeFormatter.ofPattern("MMM dd"))));
        weekLabel.getStyleClass().add("subtitle-label");
        
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
        
        double avgCompletion = activeDays > 0 ? totalCompletion / activeDays : 0;
        
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        statsBox.getChildren().addAll(
            createSmallStatBox("Points", String.valueOf(totalPoints)),
            createSmallStatBox("Avg", String.format("%.0f%%", avgCompletion)),
            createSmallStatBox("Days", activeDays + "/7")
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
    
    public void show() {
        stage.show();
    }
    
    public void close() {
        if (stage != null) {
            stage.close();
        }
    }
}