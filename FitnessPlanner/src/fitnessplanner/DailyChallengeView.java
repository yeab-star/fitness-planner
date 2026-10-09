package fitnessplanner;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DailyChallengeView {
    private Stage stage;
    private User currentUser;
    private DailyChallengeService challengeService;
    private DailyChallengeData todaysData;
    private VBox mainContainer;
    private ScrollPane scrollPane;
    private Map<String, CheckBox> challengeCheckBoxes;
    private Map<String, TextField> inputFields;
    private Label progressLabel;
    private ProgressBar dailyProgressBar;
    private Label pointsLabel;
    
    public DailyChallengeView(User user) {
        this.currentUser = user;
        this.challengeCheckBoxes = new HashMap<>();
        this.inputFields = new HashMap<>();
        
        try {
            this.challengeService = new DailyChallengeService();
            this.challengeService.initializeTables();
            this.todaysData = challengeService.getDailyChallengeData(user.getId(), LocalDate.now());
            System.out.println("Daily Challenge View initialized successfully for user: " + user.getName());
        } catch (Exception e) {
            System.err.println("Error initializing Daily Challenge View: " + e.getMessage());
            e.printStackTrace();
            // Create empty data as fallback
            this.todaysData = new DailyChallengeData(LocalDate.now());
        }
        
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
        
        // Create weekly progress section
        createWeeklyProgressSection();
        
        // Scroll pane
        scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        // Scene
        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        
        stage.setScene(scene);
        
        // Load today's challenges
        loadTodaysChallenges();
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
        HBox stepsBox = createInputRow("👟 Steps Taken:", "steps", 
            "Target: " + DailyChallengeData.getTargetSteps() + " steps", "STEPS");
        
        // Workout minutes
        HBox workoutBox = createInputRow("⏱️ Workout Minutes:", "workout_minutes", 
            "Target: " + DailyChallengeData.getTargetWorkoutMinutes() + " minutes", "EXERCISE");
        
        // Calories burned
        HBox caloriesBox = createInputRow("🔥 Calories Burned:", "calories_burned", 
            "Target: " + DailyChallengeData.getTargetCaloriesBurned() + " calories", "CALORIES_BURN");
        
        exerciseBox.getChildren().addAll(sectionTitle, stepsBox, workoutBox, caloriesBox);
        parent.getChildren().add(exerciseBox);
    }
    
    private void createNutritionSection(VBox parent) {
        VBox nutritionBox = new VBox(15);
        nutritionBox.getStyleClass().add("card");
        
        Label sectionTitle = new Label("🥗 Nutrition & Diet");
        sectionTitle.getStyleClass().add("card-title-label");
        
        // Vegetables servings
        HBox veggiesBox = createInputRow("🥬 Vegetable Servings:", "vegetables", 
            "Target: " + DailyChallengeData.getTargetVegetables() + " servings", "NUTRITION");
        
        // Fruits servings
        HBox fruitsBox = createInputRow("🍎 Fruit Servings:", "fruits", 
            "Target: " + DailyChallengeData.getTargetFruits() + " servings", "NUTRITION");
        
        // Calorie intake
        HBox calorieIntakeBox = createInputRow("🍽️ Calories Consumed:", "calorie_intake", 
            "Track your daily intake", "NUTRITION");
        
        // Protein intake
        HBox proteinBox = createInputRow("🥩 Protein (grams):", "protein", 
            "Track protein intake", "NUTRITION");
        
        nutritionBox.getChildren().addAll(sectionTitle, veggiesBox, fruitsBox, calorieIntakeBox, proteinBox);
        parent.getChildren().add(nutritionBox);
    }
    
    private void createHydrationSection(VBox parent) {
        VBox hydrationBox = new VBox(15);
        hydrationBox.getStyleClass().add("card");
        
        Label sectionTitle = new Label("💧 Hydration");
        sectionTitle.getStyleClass().add("card-title-label");
        
        // Water intake
        HBox waterBox = createInputRow("🚰 Water Intake (ml):", "water_intake", 
            "Target: " + DailyChallengeData.getTargetWaterMl() + " ml", "HYDRATION");
        
        // Water glasses counter
        HBox glassesBox = new HBox(10);
        glassesBox.setAlignment(Pos.CENTER_LEFT);
        
        Label glassLabel = new Label("💧 Water Glasses:");
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
        
        hydrationBox.getChildren().addAll(sectionTitle, waterBox, glassesBox);
        parent.getChildren().add(hydrationBox);
    }
    
    private void createSleepSection(VBox parent) {
        VBox sleepBox = new VBox(15);
        sleepBox.getStyleClass().add("card");
        
        Label sectionTitle = new Label("😴 Sleep & Recovery");
        sectionTitle.getStyleClass().add("card-title-label");
        
        // Sleep hours
        HBox sleepHoursBox = createInputRow("🛌 Sleep Hours:", "sleep_hours", 
            "Target: " + DailyChallengeData.getTargetSleepHours() + " hours", "SLEEP");
        
        // Sleep quality checkbox
        CheckBox sleepQualityCheck = new CheckBox("✨ Good Sleep Quality");
        sleepQualityCheck.getStyleClass().add("check-box");
        challengeCheckBoxes.put("sleep_quality", sleepQualityCheck);
        
        sleepBox.getChildren().addAll(sectionTitle, sleepHoursBox, sleepQualityCheck);
        parent.getChildren().add(sleepBox);
    }
    
    private HBox createInputRow(String labelText, String fieldKey, String promptText, String challengeType) {
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
        updateBtn.setOnAction(e -> updateChallengeValue(fieldKey, textField.getText(), challengeType, checkBox));
        
        // Store references
        inputFields.put(fieldKey, textField);
        challengeCheckBoxes.put(fieldKey, checkBox);
        
        row.getChildren().addAll(label, textField, updateBtn, checkBox);
        return row;
    }
    
    private void updateWaterGlasses(int glassCount) {
        int mlPerGlass = 250; // Standard glass size
        int totalMl = glassCount * mlPerGlass;
        
        TextField waterField = inputFields.get("water_intake");
        if (waterField != null) {
            waterField.setText(String.valueOf(totalMl));
            updateChallengeValue("water_intake", String.valueOf(totalMl), "HYDRATION", challengeCheckBoxes.get("water_intake"));
        }
    }
    
    private void updateChallengeValue(String fieldKey, String value, String challengeType, CheckBox checkBox) {
        try {
            double numValue = Double.parseDouble(value);
            
            // Update the data model based on field type
            updateDataModel(fieldKey, numValue, checkBox.isSelected());
            
            // Save to database
            if (challengeService != null) {
                boolean saved = challengeService.saveDailyChallengeData(currentUser.getId(), todaysData);
                if (saved) {
                    // Calculate weekly progress if it's been 7 days
                    challengeService.calculateWeeklyProgress(currentUser.getId());
                    
                    // Show success message
                    showSuccessMessage("Challenge updated! 🎉 Points: " + todaysData.getTotalPoints());
                } else {
                    showErrorMessage("Failed to save challenge data");
                }
            } else {
                showErrorMessage("Challenge service not available");
            }
            
            // Refresh progress display
            updateProgressDisplay();
            
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid number");
        } catch (Exception e) {
            showErrorMessage("Error updating challenge: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateDataModel(String fieldKey, double value, boolean completed) {
        switch (fieldKey) {
            case "steps":
                todaysData.setSteps((int) value);
                break;
            case "workout_minutes":
                todaysData.setWorkoutMinutes((int) value);
                break;
            case "calories_burned":
                todaysData.setCaloriesBurned((int) value);
                break;
            case "vegetables":
                todaysData.setVegetablesServings((int) value);
                break;
            case "fruits":
                todaysData.setFruitsServings((int) value);
                break;
            case "calorie_intake":
                todaysData.setCalorieIntake((int) value);
                break;
            case "protein":
                todaysData.setProteinGrams((int) value);
                break;
            case "water_intake":
                todaysData.setWaterMl((int) value);
                break;
            case "sleep_hours":
                todaysData.setSleepHours(value);
                break;
        }
        
        // Update completion status for sleep quality
        if (fieldKey.equals("sleep_quality")) {
            todaysData.setSleepQuality(completed);
        }
    }
    
    private void createWeeklyProgressSection() {
        VBox weeklyBox = new VBox(15);
        weeklyBox.getStyleClass().add("card");
        
        Label weeklyTitle = new Label("📈 Weekly Progress Summary");
        weeklyTitle.getStyleClass().add("card-title-label");
        
        // Get weekly data from service
        List<DailyChallengeData> weeklyData = challengeService.getWeeklyChallengeData(currentUser.getId());
        
        VBox progressBars = new VBox(10);
        
        // Show last 7 days
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dayName = date.format(DateTimeFormatter.ofPattern("EEE MMM dd"));
            
            // Find data for this date
            DailyChallengeData dayData = weeklyData.stream()
                .filter(data -> data.getDate().equals(date))
                .findFirst()
                .orElse(new DailyChallengeData(date));
            
            HBox dayRow = new HBox(10);
            dayRow.setAlignment(Pos.CENTER_LEFT);
            
            Label dayLabel = new Label(dayName);
            dayLabel.getStyleClass().add("label");
            dayLabel.setPrefWidth(120);
            
            ProgressBar dayProgress = new ProgressBar(dayData.getCompletionPercentage() / 100.0);
            dayProgress.setPrefWidth(200);
            dayProgress.getStyleClass().add("progress-bar");
            
            Label percentLabel = new Label(String.format("%.0f%% (%d pts)", 
                dayData.getCompletionPercentage(), dayData.getTotalPoints()));
            percentLabel.getStyleClass().add("label");
            
            dayRow.getChildren().addAll(dayLabel, dayProgress, percentLabel);
            progressBars.getChildren().add(dayRow);
        }
        
        // Add weekly summary
        HBox summaryRow = new HBox(10);
        summaryRow.setAlignment(Pos.CENTER_LEFT);
        summaryRow.setStyle("-fx-border-color: #58a6ff; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");
        
        double weeklyAvg = weeklyData.stream()
            .mapToDouble(DailyChallengeData::getCompletionPercentage)
            .average()
            .orElse(0);
        
        int weeklyPoints = weeklyData.stream()
            .mapToInt(DailyChallengeData::getTotalPoints)
            .sum();
        
        Label summaryLabel = new Label("Week Average:");
        summaryLabel.getStyleClass().add("subtitle-label");
        summaryLabel.setPrefWidth(120);
        
        ProgressBar weeklyProgress = new ProgressBar(weeklyAvg / 100.0);
        weeklyProgress.setPrefWidth(200);
        weeklyProgress.getStyleClass().add("progress-bar");
        
        Label weeklyLabel = new Label(String.format("%.0f%% (%d total pts)", weeklyAvg, weeklyPoints));
        weeklyLabel.getStyleClass().add("stat-value-label");
        
        summaryRow.getChildren().addAll(summaryLabel, weeklyProgress, weeklyLabel);
        
        weeklyBox.getChildren().addAll(weeklyTitle, progressBars, summaryRow);
        mainContainer.getChildren().add(weeklyBox);
    }
    
    private void loadTodaysChallenges() {
        // Load today's data from service
        todaysData = challengeService.getDailyChallengeData(currentUser.getId(), LocalDate.now());
        
        // Update UI with current data
        updateUIWithCurrentData();
        updateProgressDisplay();
    }
    
    private void updateUIWithCurrentData() {
        // Update input fields with current values
        updateFieldValue("steps", todaysData.getSteps());
        updateFieldValue("workout_minutes", todaysData.getWorkoutMinutes());
        updateFieldValue("calories_burned", todaysData.getCaloriesBurned());
        updateFieldValue("vegetables", todaysData.getVegetablesServings());
        updateFieldValue("fruits", todaysData.getFruitsServings());
        updateFieldValue("calorie_intake", todaysData.getCalorieIntake());
        updateFieldValue("protein", todaysData.getProteinGrams());
        updateFieldValue("water_intake", todaysData.getWaterMl());
        updateFieldValue("sleep_hours", todaysData.getSleepHours());
        
        // Update checkboxes
        CheckBox sleepQualityCheck = challengeCheckBoxes.get("sleep_quality");
        if (sleepQualityCheck != null) {
            sleepQualityCheck.setSelected(todaysData.isSleepQuality());
        }
    }
    
    private void updateFieldValue(String fieldKey, double value) {
        TextField field = inputFields.get(fieldKey);
        if (field != null && value > 0) {
            if (fieldKey.equals("sleep_hours")) {
                field.setText(String.valueOf(value));
            } else {
                field.setText(String.valueOf((int) value));
            }
        }
    }
    

    
    private void updateProgressDisplay() {
        Platform.runLater(() -> {
            // Recalculate completion based on current data
            todaysData.calculateCompletion();
            
            double progressPercentage = todaysData.getCompletionPercentage() / 100.0;
            int completedChallenges = 0;
            
            if (todaysData.isExerciseCompleted()) completedChallenges++;
            if (todaysData.isNutritionCompleted()) completedChallenges++;
            if (todaysData.isHydrationCompleted()) completedChallenges++;
            if (todaysData.isSleepCompleted()) completedChallenges++;
            
            dailyProgressBar.setProgress(progressPercentage);
            progressLabel.setText(String.format("%.0f%% Complete (%d/4 challenges)", 
                todaysData.getCompletionPercentage(), completedChallenges));
            pointsLabel.setText("🎯 Points Today: " + todaysData.getTotalPoints());
            
            // Update individual progress indicators
            updateIndividualProgress();
        });
    }
    
    private void updateIndividualProgress() {
        // Update checkboxes based on completion status
        CheckBox exerciseCheck = challengeCheckBoxes.get("workout_minutes");
        if (exerciseCheck != null) {
            exerciseCheck.setSelected(todaysData.isExerciseCompleted());
        }
        
        CheckBox nutritionCheck = challengeCheckBoxes.get("vegetables");
        if (nutritionCheck != null) {
            nutritionCheck.setSelected(todaysData.isNutritionCompleted());
        }
        
        CheckBox hydrationCheck = challengeCheckBoxes.get("water_intake");
        if (hydrationCheck != null) {
            hydrationCheck.setSelected(todaysData.isHydrationCompleted());
        }
        
        CheckBox sleepCheck = challengeCheckBoxes.get("sleep_hours");
        if (sleepCheck != null) {
            sleepCheck.setSelected(todaysData.isSleepCompleted());
        }
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