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

public class SimpleDailyChallengeView {
    private Stage stage;
    private User currentUser;
    private VBox mainContainer;
    private Map<String, CheckBox> challengeCheckBoxes;
    private Map<String, TextField> inputFields;
    private Label progressLabel;
    private ProgressBar dailyProgressBar;
    private Label pointsLabel;
    
    // Simple in-memory storage for demo
    private Map<String, Integer> dailyData;
    
    public SimpleDailyChallengeView(User user) {
        this.currentUser = user;
        this.challengeCheckBoxes = new HashMap<>();
        this.inputFields = new HashMap<>();
        this.dailyData = new HashMap<>();
        initializeView();
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
        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        
        stage.setScene(scene);
        
        // Initialize progress
        updateProgressDisplay();
    }
    
    private void createHeader() {
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("🏆 Daily Fitness Challenges");
        titleLabel.getStyleClass().addAll("title-label", "glow-blue");
        
        Label dateLabel = new Label("📅 " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        dateLabel.getStyleClass().add("subtitle-label");
        
        headerBox.getChildren().addAll(titleLabel, dateLabel);
        mainContainer.getChildren().add(headerBox);
    }
    
    private void createProgressSection() {
        VBox progressBox = new VBox(15);
        progressBox.getStyleClass().add("card");
        progressBox.setAlignment(Pos.CENTER);
        
        Label progressTitle = new Label("📊 Today's Progress");
        progressTitle.getStyleClass().add("card-title-label");
        
        dailyProgressBar = new ProgressBar(0);
        dailyProgressBar.setPrefWidth(400);
        dailyProgressBar.setPrefHeight(20);
        dailyProgressBar.getStyleClass().add("progress-bar");
        
        progressLabel = new Label("0% Complete");
        progressLabel.getStyleClass().add("stat-value-label");
        
        pointsLabel = new Label("🎯 Points Today: 0");
        pointsLabel.getStyleClass().add("subtitle-label");
        
        progressBox.getChildren().addAll(progressTitle, dailyProgressBar, progressLabel, pointsLabel);
        mainContainer.getChildren().add(progressBox);
    }
    
    private void createChallengeSection() {
        VBox challengeContainer = new VBox(20);
        
        // Exercise Section
        createExerciseSection(challengeContainer);
        
        // Nutrition Section
        createNutritionSection(challengeContainer);
        
        // Hydration Section
        createHydrationSection(challengeContainer);
        
        // Sleep Section
        createSleepSection(challengeContainer);
        
        mainContainer.getChildren().add(challengeContainer);
    }
    
    private void createExerciseSection(VBox parent) {
        VBox exerciseBox = new VBox(15);
        exerciseBox.getStyleClass().add("card");
        
        Label sectionTitle = new Label("🏋️ Exercise & Activity");
        sectionTitle.getStyleClass().add("card-title-label");
        
        // Steps tracking
        HBox stepsBox = createInputRow("👟 Steps Taken:", "steps", "Target: 10,000 steps", 10000);
        
        // Workout minutes
        HBox workoutBox = createInputRow("⏱️ Workout Minutes:", "workout_minutes", "Target: 30 minutes", 30);
        
        // Calories burned
        HBox caloriesBox = createInputRow("🔥 Calories Burned:", "calories_burned", "Target: 300 calories", 300);
        
        exerciseBox.getChildren().addAll(sectionTitle, stepsBox, workoutBox, caloriesBox);
        parent.getChildren().add(exerciseBox);
    }
    
    private void createNutritionSection(VBox parent) {
        VBox nutritionBox = new VBox(15);
        nutritionBox.getStyleClass().add("card");
        
        Label sectionTitle = new Label("🥗 Nutrition & Diet");
        sectionTitle.getStyleClass().add("card-title-label");
        
        // Vegetables servings
        HBox veggiesBox = createInputRow("🥬 Vegetable Servings:", "vegetables", "Target: 5 servings", 5);
        
        // Fruits servings
        HBox fruitsBox = createInputRow("🍎 Fruit Servings:", "fruits", "Target: 3 servings", 3);
        
        // Water intake
        HBox waterBox = createInputRow("💧 Water (glasses):", "water", "Target: 8 glasses", 8);
        
        nutritionBox.getChildren().addAll(sectionTitle, veggiesBox, fruitsBox, waterBox);
        parent.getChildren().add(nutritionBox);
    }
    
    private void createHydrationSection(VBox parent) {
        VBox hydrationBox = new VBox(15);
        hydrationBox.getStyleClass().add("card");
        
        Label sectionTitle = new Label("💧 Hydration Tracker");
        sectionTitle.getStyleClass().add("card-title-label");
        
        // Water glasses counter
        HBox glassesBox = new HBox(10);
        glassesBox.setAlignment(Pos.CENTER_LEFT);
        
        Label glassLabel = new Label("💧 Quick Water Tracker:");
        glassLabel.getStyleClass().add("label");
        
        HBox glassButtons = new HBox(5);
        for (int i = 1; i <= 10; i++) {
            Button glassBtn = new Button("💧");
            glassBtn.getStyleClass().add("button");
            glassBtn.setPrefSize(40, 40);
            final int glassCount = i;
            glassBtn.setOnAction(e -> updateWaterGlasses(glassCount));
            glassButtons.getChildren().add(glassBtn);
        }
        
        glassesBox.getChildren().addAll(glassLabel, glassButtons);
        
        hydrationBox.getChildren().addAll(sectionTitle, glassesBox);
        parent.getChildren().add(hydrationBox);
    }
    
    private void createSleepSection(VBox parent) {
        VBox sleepBox = new VBox(15);
        sleepBox.getStyleClass().add("card");
        
        Label sectionTitle = new Label("😴 Sleep & Recovery");
        sectionTitle.getStyleClass().add("card-title-label");
        
        // Sleep hours
        HBox sleepHoursBox = createInputRow("🛌 Sleep Hours:", "sleep_hours", "Target: 8 hours", 8);
        
        // Sleep quality checkbox
        CheckBox sleepQualityCheck = new CheckBox("✨ Good Sleep Quality");
        sleepQualityCheck.getStyleClass().add("check-box");
        sleepQualityCheck.setOnAction(e -> updateProgressDisplay());
        challengeCheckBoxes.put("sleep_quality", sleepQualityCheck);
        
        sleepBox.getChildren().addAll(sectionTitle, sleepHoursBox, sleepQualityCheck);
        parent.getChildren().add(sleepBox);
    }
    
    private HBox createInputRow(String labelText, String fieldKey, String promptText, int target) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(labelText);
        label.getStyleClass().add("label");
        label.setPrefWidth(180);
        
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.getStyleClass().add("text-field");
        textField.setPrefWidth(150);
        
        CheckBox checkBox = new CheckBox("✅ Completed");
        checkBox.getStyleClass().add("check-box");
        
        Button updateBtn = new Button("Update");
        updateBtn.getStyleClass().addAll("button", "button-primary");
        updateBtn.setOnAction(e -> updateChallengeValue(fieldKey, textField.getText(), target, checkBox));
        
        // Store references
        inputFields.put(fieldKey, textField);
        challengeCheckBoxes.put(fieldKey, checkBox);
        
        row.getChildren().addAll(label, textField, updateBtn, checkBox);
        return row;
    }
    
    private void updateWaterGlasses(int glassCount) {
        TextField waterField = inputFields.get("water");
        if (waterField != null) {
            waterField.setText(String.valueOf(glassCount));
            updateChallengeValue("water", String.valueOf(glassCount), 8, challengeCheckBoxes.get("water"));
        }
    }
    
    private void updateChallengeValue(String fieldKey, String value, int target, CheckBox checkBox) {
        try {
            int numValue = Integer.parseInt(value);
            
            // Store the value
            dailyData.put(fieldKey, numValue);
            
            // Auto-check if target is met
            if (numValue >= target) {
                checkBox.setSelected(true);
            }
            
            // Update progress display
            updateProgressDisplay();
            
            // Show success message
            showSuccessMessage("Challenge updated! 🎉");
            
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid number");
        }
    }
    
    private void updateProgressDisplay() {
        int completedChallenges = 0;
        int totalChallenges = 7; // Exercise(3) + Nutrition(3) + Sleep(1)
        int totalPoints = 0;
        
        // Check exercise challenges
        if (isCompleted("steps", 10000)) { completedChallenges++; totalPoints += 25; }
        if (isCompleted("workout_minutes", 30)) { completedChallenges++; totalPoints += 25; }
        if (isCompleted("calories_burned", 300)) { completedChallenges++; totalPoints += 25; }
        
        // Check nutrition challenges
        if (isCompleted("vegetables", 5)) { completedChallenges++; totalPoints += 20; }
        if (isCompleted("fruits", 3)) { completedChallenges++; totalPoints += 20; }
        if (isCompleted("water", 8)) { completedChallenges++; totalPoints += 15; }
        
        // Check sleep challenge
        if (isCompleted("sleep_hours", 8) && challengeCheckBoxes.get("sleep_quality").isSelected()) {
            completedChallenges++; 
            totalPoints += 20;
        }
        
        double progressPercentage = (double) completedChallenges / totalChallenges;
        
        dailyProgressBar.setProgress(progressPercentage);
        progressLabel.setText(String.format("%.0f%% Complete (%d/%d challenges)", 
            progressPercentage * 100, completedChallenges, totalChallenges));
        pointsLabel.setText("🎯 Points Today: " + totalPoints);
    }
    
    private boolean isCompleted(String fieldKey, int target) {
        Integer value = dailyData.get(fieldKey);
        return value != null && value >= target;
    }
    
    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
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
}