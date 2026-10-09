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

public class ImprovedDailyChallengeView {
    private Stage stage;
    private User currentUser;
    private VBox mainContainer;
    private Map<String, CheckBox> exerciseCheckBoxes;
    private Map<String, CheckBox> nutritionCheckBoxes;
    private Map<String, CheckBox> hydrationCheckBoxes;
    private Map<String, CheckBox> recoveryCheckBoxes;
    private Label progressLabel;
    private ProgressBar dailyProgressBar;
    private Label pointsLabel;
    private Label motivationLabel;
    private CheckboxChallengeStorage storage;
    
    public ImprovedDailyChallengeView(User user) {
        this.currentUser = user;
        this.exerciseCheckBoxes = new HashMap<>();
        this.nutritionCheckBoxes = new HashMap<>();
        this.hydrationCheckBoxes = new HashMap<>();
        this.recoveryCheckBoxes = new HashMap<>();
        this.storage = new CheckboxChallengeStorage();
        this.storage.initializeTables();
        initializeView();
    }
    
    private void initializeView() {
        stage = new Stage();
        stage.setTitle("Daily Challenges - " + currentUser.getName());
        
        // Main container
        mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.getStyleClass().add("container");
        
        // Create back button
        createBackButton();
        
        // Create header
        createHeader();
        
        // Create daily progress section
        createProgressSection();
        
        // Create challenge sections
        createChallengeSection();
        
        // Scroll pane
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        // Scene
        Scene scene = new Scene(scrollPane, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        
        stage.setScene(scene);
        
        // Initialize progress
        updateProgressDisplay();
    }
    
    private void createBackButton() {
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("button", "button-primary");
        backButton.setOnAction(e -> stage.close());
        
        backButtonBox.getChildren().add(backButton);
        mainContainer.getChildren().add(backButtonBox);
    }
    
    private void createHeader() {
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("🏆 Daily Fitness Challenges");
        titleLabel.getStyleClass().addAll("title-label", "glow-blue");
        
        Label dateLabel = new Label("📅 " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        dateLabel.getStyleClass().add("subtitle-label");
        
        // Daily motivation
        motivationLabel = new Label(DailyRecommendationService.getDailyMotivation(LocalDate.now()));
        motivationLabel.getStyleClass().addAll("motivation-text", "glow-green");
        motivationLabel.setWrapText(true);
        motivationLabel.setMaxWidth(600);
        motivationLabel.setAlignment(Pos.CENTER);
        
        headerBox.getChildren().addAll(titleLabel, dateLabel, motivationLabel);
        mainContainer.getChildren().add(headerBox);
    }
    
    private void createProgressSection() {
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
        mainContainer.getChildren().add(progressBox);
    }
    
    private void createChallengeSection() {
        HBox challengeContainer = new HBox(20);
        challengeContainer.setAlignment(Pos.TOP_CENTER);
        
        // Create four columns for different challenge types
        VBox exerciseColumn = createExerciseSection();
        VBox nutritionColumn = createNutritionSection();
        VBox hydrationColumn = createHydrationSection();
        VBox recoveryColumn = createRecoverySection();
        
        challengeContainer.getChildren().addAll(exerciseColumn, nutritionColumn, hydrationColumn, recoveryColumn);
        mainContainer.getChildren().add(challengeContainer);
    }
    
    private VBox createExerciseSection() {
        VBox exerciseBox = new VBox(15);
        exerciseBox.getStyleClass().add("card");
        exerciseBox.setPrefWidth(230);
        
        Label sectionTitle = new Label("🏋️ Exercise");
        sectionTitle.getStyleClass().add("card-title-label");
        
        VBox checkboxContainer = new VBox(10);
        
        // Get today's exercise recommendations
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
        
        // Get today's nutrition recommendations
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
        
        // Get today's hydration recommendations
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
        
        // Get today's recovery recommendations
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
    
    private void updateProgressDisplay() {
        // Get completion status for each category
        Map<String, Boolean> exerciseStatus = getCheckboxStatus(exerciseCheckBoxes);
        Map<String, Boolean> nutritionStatus = getCheckboxStatus(nutritionCheckBoxes);
        Map<String, Boolean> hydrationStatus = getCheckboxStatus(hydrationCheckBoxes);
        Map<String, Boolean> recoveryStatus = getCheckboxStatus(recoveryCheckBoxes);
        
        // Calculate overall completion percentage
        int totalItems = exerciseStatus.size() + nutritionStatus.size() + 
                        hydrationStatus.size() + recoveryStatus.size();
        
        long completedItems = exerciseStatus.values().stream().mapToLong(b -> b ? 1 : 0).sum() +
                             nutritionStatus.values().stream().mapToLong(b -> b ? 1 : 0).sum() +
                             hydrationStatus.values().stream().mapToLong(b -> b ? 1 : 0).sum() +
                             recoveryStatus.values().stream().mapToLong(b -> b ? 1 : 0).sum();
        
        double progressPercentage = totalItems > 0 ? (completedItems * 100.0) / totalItems : 0;
        
        // Calculate points
        int points = DailyRecommendationService.calculatePoints(
            exerciseStatus, nutritionStatus, hydrationStatus, recoveryStatus);
        
        // Update UI
        dailyProgressBar.setProgress(progressPercentage / 100.0);
        progressLabel.setText(String.format("%.0f%% Complete (%d/%d items)", 
            progressPercentage, completedItems, totalItems));
        pointsLabel.setText("🎯 Points Today: " + points);
        
        // Save progress to database
        try {
            storage.saveDailyChallengeStatus(currentUser.getId(), LocalDate.now(),
                exerciseStatus, nutritionStatus, hydrationStatus, recoveryStatus);
        } catch (Exception e) {
            System.err.println("Error saving challenge progress: " + e.getMessage());
        }
        
        // Show celebration message for milestones
        if (progressPercentage == 100) {
            showCelebrationMessage("🎉 Perfect Day! All challenges completed!");
        } else if (progressPercentage >= 75) {
            showCelebrationMessage("⭐ Excellent progress! Keep it up!");
        } else if (progressPercentage >= 50) {
            showCelebrationMessage("👍 Great job! You're halfway there!");
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
        // Update motivation label with celebration message temporarily
        String originalText = motivationLabel.getText();
        motivationLabel.setText(message);
        motivationLabel.getStyleClass().removeAll("glow-green");
        motivationLabel.getStyleClass().add("glow-purple");
        
        // Reset after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            motivationLabel.setText(originalText);
            motivationLabel.getStyleClass().removeAll("glow-purple");
            motivationLabel.getStyleClass().add("glow-green");
        }));
        timeline.play();
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