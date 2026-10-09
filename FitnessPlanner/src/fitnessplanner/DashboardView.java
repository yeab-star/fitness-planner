package fitnessplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.animation.*;
import javafx.util.Duration;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardView {
    private User user;
    private Map<String, CheckBox> exerciseCheckBoxes;
    private Stage primaryStage;
    private VBox mainContentArea;
    private Button selectedButton;
    private List<Map<String, Object>> userPlans;
    private Label notificationBadge;
    private int notificationCount = 3;
    private UserPlanStorage planStorage;

    public DashboardView(User user) {
        this.user = user;
        this.exerciseCheckBoxes = new HashMap<>();
        this.userPlans = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.planStorage = new UserPlanStorage();
        this.planStorage.initializeTable();
        // Load plans from database instead of sample data
        loadTodaysPlans();
        // Don't call initializeNotifications() here - badge doesn't exist yet
        // It will be called in show() method after UI is created
    }
    
    private void loadTodaysPlans() {
        LocalDate today = LocalDate.now();
        this.userPlans = planStorage.getTodaysPlans(user.getId(), today);
    }
    
    private void initializeNotifications() {
        addNotification("👋 Welcome", "Welcome back, " + user.getName() + "! Ready to crush your goals?");
        addNotification("🎯 Daily Goal", "Complete your daily challenges to earn points!");
        addNotification("💪 Motivation", "Every workout counts. Let's make today amazing!");
    }
    


    public void show(Stage stage) {
        this.primaryStage = stage;
        BorderPane root = new BorderPane();
        
        // Gender-specific local background with blur effect
        String backgroundClass;
        if (user.getGender() != null && user.getGender().equalsIgnoreCase("Female")) {
            backgroundClass = "female-dashboard-background";
        } else {
            backgroundClass = "male-dashboard-background";
        }
        
        root.getStyleClass().add(backgroundClass);

        // Menu Bar at the very top
        MenuBar menuBar = createMenuBar();
        
        // Top bar with notifications
        HBox topBar = createTopBar();
        
        // Combine menu and top bar
        VBox topSection = new VBox(menuBar, topBar);
        root.setTop(topSection);
        
        // Sidebar with menu
        VBox sidebar = createProfessionalSidebar();
        root.setLeft(sidebar);

        // Main content with semi-transparent background
        ScrollPane mainContent = createMainContent();
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        
        // Responsive window settings
        primaryStage.setTitle("💪 FitPlan Pro - Your Fitness Journey");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        
        // Add window resize listener for responsive layout
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustLayoutForWidth(newVal.doubleValue());
        });
        
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            adjustLayoutForHeight(newVal.doubleValue());
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initialize notifications after UI is created (badge now exists)
        initializeNotifications();
        
        // Show welcome notification
        showMotivationalMessage();
    }
    
    private void adjustLayoutForWidth(double width) {
        // Adjust sidebar width based on window width
        if (width < 1200) {
            // Smaller screens - compact sidebar
            // Already handled by fixed width
        } else if (width < 1600) {
            // Medium screens - normal sidebar
            // Default size
        } else {
            // Large screens - comfortable spacing
            // Default size with more padding
        }
    }
    
    private void adjustLayoutForHeight(double height) {
        // Adjust content spacing based on window height
        if (height < 800) {
            // Smaller screens - compact spacing
            mainContentArea.setSpacing(15);
        } else if (height < 1000) {
            // Medium screens - normal spacing
            mainContentArea.setSpacing(20);
        } else {
            // Large screens - comfortable spacing
            mainContentArea.setSpacing(25);
        }
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: transparent;");
        
        // View Menu (simplified)
        Menu viewMenu = new Menu("👁️ View");
        MenuItem dashboardView = new MenuItem("Dashboard");
        dashboardView.setOnAction(e -> showDashboardContent());
        viewMenu.getItems().add(dashboardView);
        
        menuBar.getMenus().add(viewMenu);
        return menuBar;
    }

    private VBox createProfessionalSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setStyle("-fx-background-color: transparent;");
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(20));
        sidebar.setAlignment(Pos.TOP_LEFT);

        // Logo/Brand
        Label brandLabel = new Label("💪 FitPlan Pro");
        brandLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 0 20 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 5, 0, 0, 2);"
        );

        // User Profile Section
        VBox profileSection = createProfileSection();
        
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: rgba(88, 166, 255, 0.5);");
        
        // Menu Label
        Label menuLabel = new Label("📋 MENU");
        menuLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-padding: 10 0 5 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );

        // Navigation Menu (always visible)
        VBox navigationMenu = new VBox(5);
        navigationMenu.setVisible(true);
        navigationMenu.setManaged(true);
        
        // Navigation Buttons
        Button dashboardBtn = createSidebarNavButton("🏠", "Dashboard", true);
        Button challengesBtn = createSidebarNavButton("🎯", "Challenges", false);
        Button toolsBtn = createSidebarNavButton("🛠️", "Tools", false);
        Button profileBtn = createSidebarNavButton("👤", "User Profile", false);
        Button changePasswordBtn = createSidebarNavButton("🔒", "Change Password", false);
        
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(88, 166, 255, 0.5);");
        
        Button logoutBtn = createSidebarNavButton("🚪", "Logout", false);
        logoutBtn.setMinWidth(120);
        logoutBtn.setPrefWidth(150);
        logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #f85149;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 15;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1);"
        );
        
        navigationMenu.getChildren().addAll(
            dashboardBtn,
            toolsBtn,
            profileBtn,
            changePasswordBtn,
            sep2,
            logoutBtn,
            challengesBtn
        );

        // Event handlers with button state management
        dashboardBtn.setOnAction(e -> {
            setActiveButton(dashboardBtn);
            showDashboardContent();
        });
        challengesBtn.setOnAction(e -> {
            setActiveButton(challengesBtn);
            showCombinedChallenges();
        });
        toolsBtn.setOnAction(e -> {
            setActiveButton(toolsBtn);
            showToolsContent();
        });
        profileBtn.setOnAction(e -> {
            setActiveButton(profileBtn);
            showProfileContent();
        });
        changePasswordBtn.setOnAction(e -> {
            setActiveButton(changePasswordBtn);
            showChangePasswordContent();
        });
        logoutBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Logout");
            confirm.setHeaderText("Are you sure you want to logout?");
            confirm.setContentText("Your progress will be saved.");
            if (confirm.showAndWait().get() == ButtonType.OK) {
                primaryStage.close();
                new LoginView().show(new Stage());
            }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            brandLabel,
            profileSection,
            sep1,
            menuLabel,
            navigationMenu,
            spacer
        );
        
        return sidebar;
    }

    private VBox createProfileSection() {
        VBox profileBox = new VBox(8);
        profileBox.setStyle(
            "-fx-background-color: rgba(33, 38, 45, 0.85);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 15;" +
            "-fx-border-color: rgba(88, 166, 255, 0.5);" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 10, 0, 0, 3);"
        );
        profileBox.setAlignment(Pos.CENTER_LEFT);

        Label avatarLabel = new Label("👤");
        avatarLabel.setStyle(
            "-fx-font-size: 48px;" +
            "-fx-background-color: rgba(31, 111, 235, 0.8);" +
            "-fx-background-radius: 50%;" +
            "-fx-pref-width: 60;" +
            "-fx-pref-height: 60;" +
            "-fx-alignment: center;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 8, 0, 0, 2);"
        );

        Label nameLabel = new Label(user.getName());
        nameLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );

        Label goalLabel = new Label("Goal: " + user.getGoal());
        goalLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0, 0, 1);"
        );

        Label streakLabel = new Label("🔥 " + user.getStreak() + " day streak");
        streakLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #f85149;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );

        profileBox.getChildren().addAll(avatarLabel, nameLabel, goalLabel, streakLabel);
        return profileBox;
    }

    private Button createSidebarNavButton(String icon, String text, boolean selected) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        
        updateButtonStyle(btn, selected);
        
        btn.setOnMouseEntered(e -> {
            if (btn != selectedButton) {
                btn.setStyle(
                    "-fx-background-color: rgba(88, 166, 255, 0.3);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 12 15;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-scale-x: 1.02;" +
                    "-fx-scale-y: 1.02;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 5, 0, 0, 2);"
                );
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (btn != selectedButton) {
                updateButtonStyle(btn, false);
            }
        });
        
        if (selected) {
            selectedButton = btn;
        }
        
        return btn;
    }
    
    private void updateButtonStyle(Button btn, boolean selected) {
        btn.setStyle(
            "-fx-background-color: " + (selected ? "rgba(31, 111, 235, 0.8)" : "transparent") + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: " + (selected ? "bold" : "normal") + ";" +
            "-fx-padding: 12 15;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1);"
        );
    }
    
    private void setActiveButton(Button btn) {
        if (selectedButton != null) {
            updateButtonStyle(selectedButton, false);
        }
        selectedButton = btn;
        updateButtonStyle(btn, true);
    }



    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-padding: 15 30;"
        );
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Notification button with badge
        StackPane notificationStack = new StackPane();
        Button notificationBtn = new Button("🔔");
        notificationBtn.setStyle(
            "-fx-background-color: #21262d;" +
            "-fx-text-fill: #8b949e;" +
            "-fx-font-size: 20px;" +
            "-fx-pref-width: 50;" +
            "-fx-pref-height: 50;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        );
        
        notificationBadge = new Label(String.valueOf(notificationCount));
        notificationBadge.setStyle(
            "-fx-background-color: #f85149;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 2 6;" +
            "-fx-background-radius: 10;" +
            "-fx-min-width: 20;" +
            "-fx-alignment: center;"
        );
        notificationBadge.setTranslateX(15);
        notificationBadge.setTranslateY(-15);
        
        notificationBtn.setOnAction(e -> showNotifications());
        notificationBtn.setOnMouseEntered(e -> {
            notificationBtn.setStyle(
                "-fx-background-color: #30363d;" +
                "-fx-text-fill: #58a6ff;" +
                "-fx-font-size: 20px;" +
                "-fx-pref-width: 50;" +
                "-fx-pref-height: 50;" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.1;" +
                "-fx-scale-y: 1.1;"
            );
        });
        notificationBtn.setOnMouseExited(e -> {
            notificationBtn.setStyle(
                "-fx-background-color: #21262d;" +
                "-fx-text-fill: #8b949e;" +
                "-fx-font-size: 20px;" +
                "-fx-pref-width: 50;" +
                "-fx-pref-height: 50;" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;"
            );
        });
        
        notificationStack.getChildren().addAll(notificationBtn, notificationBadge);

        topBar.getChildren().addAll(spacer, notificationStack);
        return topBar;
    }

    private ScrollPane createMainContent() {
        mainContentArea = new VBox(25);
        mainContentArea.setPadding(new Insets(30));
        mainContentArea.setAlignment(Pos.TOP_LEFT);
        mainContentArea.setStyle("-fx-background-color: transparent;");
        mainContentArea.setMinHeight(Region.USE_PREF_SIZE);

        showDashboardContent();

        ScrollPane scrollPane = new ScrollPane(mainContentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(25));
        scrollPane.setPannable(true);
        return scrollPane;
    }

    private void showDashboardContent() {
        mainContentArea.getChildren().clear();
        
        // Get real data
        UserService userService = new UserService();
        UserMetrics todaysMetrics = userService.getTodaysMetrics(user.getId());
        double latestWeight = userService.getLatestWeight(user.getId());
        Map<String, Double> sleepData = userService.getWeeklySleepData(user.getId());

        // Welcome message centered at top
        Label welcomeMessage = new Label("Welcome back, " + user.getName() + "! 👋");
        welcomeMessage.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 8, 0, 0, 3);"
        );
        welcomeMessage.setAlignment(Pos.CENTER);
        welcomeMessage.setMaxWidth(Double.MAX_VALUE);
        
        // Page title
        Label pageTitle = new Label("💪 Dashboard Overview");
        pageTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");

        Label dateLabel = new Label("📅 " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #8b949e;");

        // Stats Cards - Responsive FlowPane
        FlowPane statsFlow = new FlowPane();
        statsFlow.setHgap(20);
        statsFlow.setVgap(20);
        statsFlow.setPadding(new Insets(20, 0, 0, 0));
        statsFlow.setAlignment(Pos.CENTER);

        // Quick Stats Cards
        VBox caloriesCard = createStatCard("🔥", "Calories", todaysMetrics.getCalories() + " kcal", "#f85149");
        VBox waterCard = createStatCard("💧", "Water", todaysMetrics.getWaterMl() + " ml", "#58a6ff");
        VBox sleepCard = createStatCard("😴", "Sleep", todaysMetrics.getSleepHours() + " hrs", "#a371f7");
        VBox weightCard = createStatCard("⚖️", "Weight", latestWeight + " kg", "#3fb950");

        statsFlow.getChildren().addAll(caloriesCard, waterCard, sleepCard, weightCard);
        
        // Daily Challenges Section - Get challenge data for notifications
        LocalDate today = LocalDate.now();
        CheckboxChallengeStorage storage = new CheckboxChallengeStorage();
        Map<String, Map<String, Boolean>> todayStatus = storage.getDailyChallengeStatus(user.getId(), today);
        
        List<String> exercises = DailyRecommendationService.getDailyExercises(today);
        List<String> nutrition = DailyRecommendationService.getDailyNutrition(today);
        List<String> hydration = DailyRecommendationService.getDailyHydration(today);
        List<String> recovery = DailyRecommendationService.getDailyRecovery(today);
        
        int totalItems = exercises.size() + nutrition.size() + hydration.size() + recovery.size();
        long completedItems = todayStatus.values().stream()
            .flatMap(map -> map.values().stream())
            .mapToLong(b -> b ? 1 : 0)
            .sum();
        
        VBox challengesSection = createDailyChallengesSection();
        
        // Weekly Progress Summary Section
        VBox weeklyProgressSection = createWeeklyProgressSummary();
        
        // Recommendations based on goal
        VBox recommendationsCard = createRecommendationsCard();
        
        // Today's Plan Tracker
        VBox planTrackerCard = createPlanTrackerCard();

        // Wellness Card with inputs
        VBox wellnessCard = createWellnessCard(todaysMetrics, latestWeight, userService);
        
        // Responsive bottom row
        FlowPane bottomFlow = new FlowPane();
        bottomFlow.setHgap(20);
        bottomFlow.setVgap(20);
        bottomFlow.setAlignment(Pos.CENTER);
        bottomFlow.setPrefWrapLength(1200);
        bottomFlow.getChildren().addAll(wellnessCard, recommendationsCard, planTrackerCard);

        // Quick Actions - Responsive with prominent styling
        VBox actionSection = new VBox(15);
        actionSection.setAlignment(Pos.CENTER);
        actionSection.setPadding(new Insets(20, 0, 20, 0));
        
        Label actionTitle = new Label("⚡ Quick Actions");
        actionTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );
        
        FlowPane buttonRow = new FlowPane();
        buttonRow.setHgap(15);
        buttonRow.setVgap(15);
        buttonRow.setAlignment(Pos.CENTER);
        
        Button logWeightBtn = createActionButton("📝 Log Weight", e -> showWeightLog(), "#1f6feb");
        logWeightBtn.setMinWidth(150);
        logWeightBtn.setPrefWidth(180);
        
        buttonRow.getChildren().add(logWeightBtn);
        
        actionSection.getChildren().addAll(actionTitle, buttonRow);

        mainContentArea.getChildren().addAll(
            welcomeMessage,
            pageTitle, dateLabel, statsFlow,
            challengesSection,
            weeklyProgressSection,
            bottomFlow, actionSection
        );
        
        // Check for unfinished tasks and notify user
        checkUnfinishedTasks(todayStatus, totalItems, completedItems);
        
        // Check for unfinished plans and notify user
        checkUnfinishedPlans();
    }

    private void showNutritionContent() {
        mainContentArea.getChildren().clear();
        
        Label title = new Label("🥗 Today's Nutrition");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");

        VBox nutritionForm = new VBox(15);
        nutritionForm.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 25;" +
            "-fx-border-color: #30363d;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        nutritionForm.setMaxWidth(600);

        TextField mealField = new TextField();
        mealField.setPromptText("Meal name (e.g., Breakfast)");
        mealField.getStyleClass().add("text-field");

        TextField caloriesField = new TextField();
        caloriesField.setPromptText("Calories");
        caloriesField.getStyleClass().add("text-field");

        TextField proteinField = new TextField();
        proteinField.setPromptText("Protein (g)");
        proteinField.getStyleClass().add("text-field");

        TextField carbsField = new TextField();
        carbsField.setPromptText("Carbs (g)");
        carbsField.getStyleClass().add("text-field");

        TextField fatsField = new TextField();
        fatsField.setPromptText("Fats (g)");
        fatsField.getStyleClass().add("text-field");

        Button saveBtn = createActionButton("💾 Save Meal", e -> {
            showAlert("Success", "Meal logged successfully!");
            mealField.clear();
            caloriesField.clear();
            proteinField.clear();
            carbsField.clear();
            fatsField.clear();
        }, "#238636");

        nutritionForm.getChildren().addAll(
            new Label("Log Your Meal"),
            mealField, caloriesField, proteinField, carbsField, fatsField, saveBtn
        );

        mainContentArea.getChildren().addAll(title, nutritionForm);
    }

    private void showExerciseContent() {
        mainContentArea.getChildren().clear();
        
        Label title = new Label("🏋️ Today's Exercise");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");

        VBox exerciseForm = new VBox(15);
        exerciseForm.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 25;" +
            "-fx-border-color: #30363d;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        exerciseForm.setMaxWidth(600);

        TextField exerciseField = new TextField();
        exerciseField.setPromptText("Exercise name (e.g., Push-ups)");
        exerciseField.getStyleClass().add("text-field");

        TextField setsField = new TextField();
        setsField.setPromptText("Sets");
        setsField.getStyleClass().add("text-field");

        TextField repsField = new TextField();
        repsField.setPromptText("Reps");
        repsField.getStyleClass().add("text-field");

        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");
        durationField.getStyleClass().add("text-field");

        Button saveBtn = createActionButton("💾 Save Exercise", e -> {
            showAlert("Success", "Exercise logged successfully!");
            exerciseField.clear();
            setsField.clear();
            repsField.clear();
            durationField.clear();
        }, "#238636");

        exerciseForm.getChildren().addAll(
            new Label("Log Your Workout"),
            exerciseField, setsField, repsField, durationField, saveBtn
        );

        mainContentArea.getChildren().addAll(title, exerciseForm);
    }

    private void showProfileContent() {
        mainContentArea.getChildren().clear();
        
        Label title = new Label("👤 User Profile");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");

        VBox profileCard = new VBox(15);
        profileCard.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 30;" +
            "-fx-border-color: #30363d;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );
        profileCard.setMaxWidth(600);

        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 80px;");

        Label nameLabel = createProfileLabel("Name:", user.getName());
        Label emailLabel = createProfileLabel("Email:", user.getEmail());
        Label ageLabel = createProfileLabel("Age:", user.getAge() + " years");
        Label heightLabel = createProfileLabel("Height:", user.getHeight() + " cm");
        Label weightLabel = createProfileLabel("Weight:", user.getWeight() + " kg");
        Label genderLabel = createProfileLabel("Gender:", user.getGender());
        Label goalLabel = createProfileLabel("Goal:", user.getGoal());
        Label streakLabel = createProfileLabel("Streak:", "🔥 " + user.getStreak() + " days");

        // Edit Profile button
        Button editProfileBtn = createActionButton("✏️ Edit Profile", e -> showProfileEditContent(), "#1f6feb");
        editProfileBtn.setMaxWidth(200);

        profileCard.getChildren().addAll(
            avatar, nameLabel, emailLabel, ageLabel, heightLabel, 
            weightLabel, genderLabel, goalLabel, streakLabel, editProfileBtn
        );

        mainContentArea.getChildren().addAll(title, profileCard);
    }

    private Label createProfileLabel(String label, String value) {
        Label lbl = new Label(label + " " + value);
        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #c9d1d9; -fx-padding: 5;");
        return lbl;
    }
    
    private void showDailyChallenges() {
        try {
            // Use the new improved daily challenge view with recommendations
            ImprovedDailyChallengeView improvedView = new ImprovedDailyChallengeView(user);
            improvedView.show();
        } catch (Exception e) {
            System.err.println("Error with improved daily challenge view, using fallback: " + e.getMessage());
            try {
                // Fallback to simple version
                SimpleDailyChallengeView simpleDailyChallengeView = new SimpleDailyChallengeView(user);
                simpleDailyChallengeView.show();
            } catch (Exception e2) {
                System.err.println("Error with simple view: " + e2.getMessage());
                showErrorMessage("Unable to load daily challenges. Please try again later.");
            }
        }
    }
    
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showCombinedChallenges() {
        mainContentArea.getChildren().clear();
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setMaxWidth(1200);
        
        // Back button
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        );
        backButton.setOnAction(e -> showDashboardContent());
        backButtonBox.getChildren().add(backButton);
        
        // Header
        Label title = new Label("🎯 Daily Challenges");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        title.setWrapText(true);
        
        Label subtitle = new Label("Complete your daily challenges to earn points and build streaks!");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #8b949e;");
        subtitle.setWrapText(true);
        
        // Get today's data
        LocalDate today = LocalDate.now();
        CheckboxChallengeStorage storage = new CheckboxChallengeStorage();
        Map<String, Map<String, Boolean>> todayStatus = storage.getDailyChallengeStatus(user.getId(), today);
        
        List<String> exercises = DailyRecommendationService.getDailyExercises(today);
        List<String> nutrition = DailyRecommendationService.getDailyNutrition(today);
        List<String> hydration = DailyRecommendationService.getDailyHydration(today);
        List<String> recovery = DailyRecommendationService.getDailyRecovery(today);
        
        // Create tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMinHeight(400);
        tabPane.setPrefHeight(600);
        tabPane.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        // Daily Challenges Tab
        Tab dailyTab = new Tab("📅 Daily Challenges");
        dailyTab.setContent(createDailyChallengesTab(exercises, nutrition, hydration, recovery, todayStatus, storage, today));
        
        // Weekly Summary Tab
        Tab weeklyTab = new Tab("📊 Weekly Summary");
        weeklyTab.setContent(createWeeklySummaryTab(storage));
        
        // History Tab
        Tab historyTab = new Tab("📜 History");
        historyTab.setContent(createHistoryTab(storage));
        
        tabPane.getTabs().addAll(dailyTab, weeklyTab, historyTab);
        
        layout.getChildren().addAll(backButtonBox, title, subtitle, tabPane);
        
        // Wrap in ScrollPane for full responsiveness
        ScrollPane mainScroll = new ScrollPane(layout);
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        mainContentArea.getChildren().add(mainScroll);
    }
    
    private VBox createDailyChallengesTab(List<String> exercises, List<String> nutrition, 
                                          List<String> hydration, List<String> recovery,
                                          Map<String, Map<String, Boolean>> todayStatus,
                                          CheckboxChallengeStorage storage, LocalDate today) {
        VBox tab = new VBox(20);
        tab.setPadding(new Insets(20));
        
        // Progress summary
        int totalItems = exercises.size() + nutrition.size() + hydration.size() + recovery.size();
        long completedItems = todayStatus.values().stream()
            .flatMap(map -> map.values().stream())
            .filter(b -> b)
            .count();
        double progressPercentage = totalItems > 0 ? (completedItems * 100.0) / totalItems : 0;
        
        Label progressLabel = new Label(String.format("Progress: %.0f%% (%d/%d completed)", 
            progressPercentage, completedItems, totalItems));
        progressLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");
        
        ProgressBar progressBar = new ProgressBar(progressPercentage / 100.0);
        progressBar.setPrefWidth(500);
        progressBar.setPrefHeight(20);
        
        // Categories in responsive FlowPane (wraps on smaller screens)
        FlowPane categoriesFlow = new FlowPane();
        categoriesFlow.setHgap(20);
        categoriesFlow.setVgap(20);
        categoriesFlow.setAlignment(Pos.CENTER);
        categoriesFlow.setPrefWrapLength(800); // Wrap at 800px
        
        VBox exerciseBox = createChallengeCategory("🏋️ Exercise", "EXERCISE", exercises, todayStatus.get("EXERCISE"), storage, today);
        VBox nutritionBox = createChallengeCategory("🥗 Nutrition", "NUTRITION", nutrition, todayStatus.get("NUTRITION"), storage, today);
        VBox hydrationBox = createChallengeCategory("💧 Hydration", "HYDRATION", hydration, todayStatus.get("HYDRATION"), storage, today);
        VBox recoveryBox = createChallengeCategory("😴 Recovery", "RECOVERY", recovery, todayStatus.get("RECOVERY"), storage, today);
        
        categoriesFlow.getChildren().addAll(exerciseBox, nutritionBox, hydrationBox, recoveryBox);
        
        // Wrap in ScrollPane for smaller screens
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(categoriesFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        tab.getChildren().addAll(progressLabel, progressBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return tab;
    }
    
    private VBox createChallengeCategory(String categoryTitle, String categoryType, 
                                         List<String> items, Map<String, Boolean> completionStatus,
                                         CheckboxChallengeStorage storage, LocalDate date) {
        VBox categoryBox = new VBox(10);
        categoryBox.setStyle(
            "-fx-background-color: rgba(33, 38, 45, 0.8);" +
            "-fx-border-color: #30363d;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 15;" +
            "-fx-min-width: 280;" +
            "-fx-pref-width: 350;" +
            "-fx-max-width: 400;"
        );
        
        Label titleLabel = new Label(categoryTitle);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        
        VBox itemsBox = new VBox(8);
        
        for (String item : items) {
            CheckBox checkBox = new CheckBox(item);
            checkBox.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 13px;");
            checkBox.setWrapText(true);
            checkBox.setMaxWidth(280);
            
            boolean isCompleted = completionStatus != null && completionStatus.getOrDefault(item, false);
            checkBox.setSelected(isCompleted);
            
            checkBox.setOnAction(e -> {
                storage.updateSingleChallenge(user.getId(), date, categoryType, item, checkBox.isSelected());
                if (checkBox.isSelected()) {
                    addNotification("✅ Challenge Completed", "Great job: " + item);
                }
                // Refresh the challenges view
                showCombinedChallenges();
            });
            
            itemsBox.getChildren().add(checkBox);
        }
        
        scrollPane.setContent(itemsBox);
        
        long completed = completionStatus != null ? completionStatus.values().stream().filter(b -> b).count() : 0;
        Label statsLabel = new Label(String.format("✓ %d/%d completed", completed, items.size()));
        statsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8b949e;");
        
        categoryBox.getChildren().addAll(titleLabel, scrollPane, statsLabel);
        return categoryBox;
    }
    
    private VBox createWeeklySummaryTab(CheckboxChallengeStorage storage) {
        VBox tab = new VBox(20);
        tab.setPadding(new Insets(20));
        
        Label title = new Label("📊 This Week's Progress");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        title.setWrapText(true);
        
        List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData = storage.getWeeklyChallengeData(user.getId());
        
        if (weeklyData.isEmpty()) {
            Label noData = new Label("No data yet. Complete daily challenges to see your progress!");
            noData.setStyle("-fx-font-size: 16px; -fx-text-fill: #8b949e;");
            noData.setWrapText(true);
            tab.getChildren().addAll(title, noData);
            return tab;
        }
        
        // Weekly stats
        double totalCompletion = 0;
        int totalPoints = 0;
        for (CheckboxChallengeStorage.WeeklyChallengeDay day : weeklyData) {
            totalCompletion += day.getOverallCompletion();
            totalPoints += day.getTotalPoints();
        }
        double avgCompletion = totalCompletion / weeklyData.size();
        
        FlowPane statsFlow = new FlowPane();
        statsFlow.setHgap(20);
        statsFlow.setVgap(20);
        statsFlow.setAlignment(Pos.CENTER);
        
        VBox avgCard = createStatCard("📊", "Avg Completion", String.format("%.1f%%", avgCompletion), "#58a6ff");
        VBox pointsCard = createStatCard("🎯", "Total Points", String.valueOf(totalPoints), "#3fb950");
        VBox daysCard = createStatCard("📅", "Days Active", weeklyData.size() + "/7", "#f85149");
        
        statsFlow.getChildren().addAll(avgCard, pointsCard, daysCard);
        
        // Day by day breakdown
        VBox daysBox = new VBox(10);
        for (CheckboxChallengeStorage.WeeklyChallengeDay day : weeklyData) {
            HBox dayRow = new HBox(15);
            dayRow.setAlignment(Pos.CENTER_LEFT);
            dayRow.setStyle(
                "-fx-background-color: rgba(33, 38, 45, 0.8);" +
                "-fx-padding: 15;" +
                "-fx-border-radius: 8px;"
            );
            
            Label dayLabel = new Label(day.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMM dd")));
            dayLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-min-width: 120;");
            dayLabel.setWrapText(true);
            
            ProgressBar dayProgress = new ProgressBar(day.getOverallCompletion() / 100.0);
            dayProgress.setMinWidth(150);
            dayProgress.setPrefWidth(250);
            dayProgress.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(dayProgress, Priority.ALWAYS);
            
            Label percentLabel = new Label(String.format("%.0f%%", day.getOverallCompletion()));
            percentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8b949e; -fx-min-width: 45;");
            
            Label pointsLabel = new Label(day.getTotalPoints() + " pts");
            pointsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #3fb950; -fx-min-width: 55;");
            
            dayRow.getChildren().addAll(dayLabel, dayProgress, percentLabel, pointsLabel);
            daysBox.getChildren().add(dayRow);
        }
        
        ScrollPane scrollPane = new ScrollPane(daysBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        tab.getChildren().addAll(title, statsFlow, scrollPane);
        return tab;
    }
    
    private VBox createHistoryTab(CheckboxChallengeStorage storage) {
        VBox tab = new VBox(20);
        tab.setPadding(new Insets(20));
        
        Label title = new Label("📜 Challenge History");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // All-time stats
        Map<String, Object> allTimeStats = storage.getAllTimeStats(user.getId());
        
        // Get all historical data to calculate avg completion and best streak
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(365); // Last year
        List<CheckboxChallengeStorage.WeeklyChallengeDay> allData = storage.getDateRangeChallengeData(user.getId(), startDate, endDate);
        
        // Calculate average completion
        double avgCompletion = 0.0;
        if (!allData.isEmpty()) {
            double totalCompletion = 0;
            for (CheckboxChallengeStorage.WeeklyChallengeDay day : allData) {
                totalCompletion += day.getOverallCompletion();
            }
            avgCompletion = totalCompletion / allData.size();
        }
        
        // Calculate best streak
        int bestStreak = 0;
        int currentStreak = 0;
        for (CheckboxChallengeStorage.WeeklyChallengeDay day : allData) {
            if (day.getOverallCompletion() >= 80) {
                currentStreak++;
                bestStreak = Math.max(bestStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }
        
        FlowPane statsFlow = new FlowPane();
        statsFlow.setHgap(20);
        statsFlow.setVgap(20);
        statsFlow.setAlignment(Pos.CENTER);
        
        // Get values with null checks and defaults
        String totalDays = allTimeStats.get("totalDays") != null ? allTimeStats.get("totalDays").toString() : "0";
        String totalPoints = allTimeStats.get("totalPoints") != null ? allTimeStats.get("totalPoints").toString() : "0";
        
        VBox totalDaysCard = createStatCard("📅", "Total Days", totalDays, "#58a6ff");
        VBox totalPointsCard = createStatCard("🎯", "Total Points", totalPoints, "#3fb950");
        VBox avgCompletionCard = createStatCard("📊", "Avg Completion", 
            String.format("%.1f%%", avgCompletion), "#a371f7");
        VBox bestStreakCard = createStatCard("🔥", "Best Streak", 
            bestStreak + " days", "#f85149");
        
        statsFlow.getChildren().addAll(totalDaysCard, totalPointsCard, avgCompletionCard, bestStreakCard);
        
        // Recent history (last 30 days)
        Label recentTitle = new Label("Recent Activity (Last 30 Days)");
        recentTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Get last 30 days of data
        LocalDate recentEndDate = LocalDate.now();
        LocalDate recentStartDate = recentEndDate.minusDays(30);
        List<CheckboxChallengeStorage.WeeklyChallengeDay> recentData = storage.getDateRangeChallengeData(user.getId(), recentStartDate, recentEndDate);
        
        VBox historyBox = new VBox(10);
        for (CheckboxChallengeStorage.WeeklyChallengeDay day : recentData) {
            HBox dayRow = new HBox(15);
            dayRow.setAlignment(Pos.CENTER_LEFT);
            dayRow.setStyle(
                "-fx-background-color: rgba(33, 38, 45, 0.8);" +
                "-fx-padding: 12;" +
                "-fx-border-radius: 8px;"
            );
            
            Label dayLabel = new Label(day.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            dayLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-min-width: 100;");
            dayLabel.setWrapText(true);
            
            ProgressBar dayProgress = new ProgressBar(day.getOverallCompletion() / 100.0);
            dayProgress.setMinWidth(150);
            dayProgress.setPrefWidth(200);
            dayProgress.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(dayProgress, Priority.ALWAYS);
            
            Label percentLabel = new Label(String.format("%.0f%%", day.getOverallCompletion()));
            percentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8b949e; -fx-min-width: 45;");
            
            dayRow.getChildren().addAll(dayLabel, dayProgress, percentLabel);
            historyBox.getChildren().add(dayRow);
        }
        
        ScrollPane scrollPane = new ScrollPane(historyBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        tab.getChildren().addAll(title, statsFlow, recentTitle, scrollPane);
        return tab;
    }
    
    private void showToolsContent() {
        mainContentArea.getChildren().clear();
        
        Label title = new Label("🛠️ Fitness Tools");
        title.getStyleClass().addAll("title-label", "glow-blue");

        VBox toolsContainer = new VBox(30);
        toolsContainer.setAlignment(Pos.CENTER);
        toolsContainer.setPadding(new Insets(40));

        // BMI Calculator Card
        VBox bmiCard = createToolCard(
            "📏 BMI Calculator", 
            "Calculate your Body Mass Index",
            "Find out if you're in a healthy weight range",
            e -> showBMICalculator()
        );

        // Calorie Calculator Card  
        VBox calorieCard = createToolCard(
            "🔥 Calorie Calculator",
            "Calculate daily calorie needs", 
            "Determine how many calories you need per day",
            e -> showCalorieCalculator()
        );

        // Reminder Tool Card
        VBox reminderCard = createToolCard(
            "⏰ Set Reminders",
            "Set workout and meal reminders",
            "Never miss a workout or meal again", 
            e -> showReminderDialog()
        );

        toolsContainer.getChildren().addAll(bmiCard, calorieCard, reminderCard);
        mainContentArea.getChildren().addAll(title, toolsContainer);
    }
    
    private VBox createToolCard(String title, String subtitle, String description, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(400);
        card.setPadding(new Insets(30));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title-label");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("subtitle-label");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("label");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(350);
        descLabel.setAlignment(Pos.CENTER);

        Button actionBtn = new Button("Open Tool");
        actionBtn.getStyleClass().addAll("button", "button-primary");
        actionBtn.setOnAction(action);

        card.getChildren().addAll(titleLabel, subtitleLabel, descLabel, actionBtn);
        return card;
    }
    
    private void showProfileEditContent() {
        mainContentArea.getChildren().clear();
        
        Label title = new Label("✏️ Edit Profile");
        title.getStyleClass().addAll("title-label", "glow-blue");

        VBox editContainer = new VBox(30);
        editContainer.setAlignment(Pos.CENTER);
        editContainer.setPadding(new Insets(40));

        // Profile Edit Form
        VBox editForm = new VBox(20);
        editForm.getStyleClass().add("card");
        editForm.setAlignment(Pos.CENTER);
        editForm.setPrefWidth(500);
        editForm.setPadding(new Insets(40));

        Label formTitle = new Label("👤 Personal Information");
        formTitle.getStyleClass().add("card-title-label");

        // Name field
        HBox nameBox = new HBox(15);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("label");
        nameLabel.setPrefWidth(100);
        TextField nameField = new TextField(user.getName());
        nameField.getStyleClass().add("text-field");
        nameField.setPrefWidth(300);
        nameBox.getChildren().addAll(nameLabel, nameField);

        // Email field
        HBox emailBox = new HBox(15);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("label");
        emailLabel.setPrefWidth(100);
        TextField emailField = new TextField(user.getEmail());
        emailField.getStyleClass().add("text-field");
        emailField.setPrefWidth(300);
        emailBox.getChildren().addAll(emailLabel, emailField);

        // Age field
        HBox ageBox = new HBox(15);
        ageBox.setAlignment(Pos.CENTER_LEFT);
        Label ageLabel = new Label("Age:");
        ageLabel.getStyleClass().add("label");
        ageLabel.setPrefWidth(100);
        TextField ageField = new TextField(String.valueOf(user.getAge()));
        ageField.getStyleClass().add("text-field");
        ageField.setPrefWidth(300);
        ageBox.getChildren().addAll(ageLabel, ageField);

        // Height field
        HBox heightBox = new HBox(15);
        heightBox.setAlignment(Pos.CENTER_LEFT);
        Label heightLabel = new Label("Height (cm):");
        heightLabel.getStyleClass().add("label");
        heightLabel.setPrefWidth(100);
        TextField heightField = new TextField(String.valueOf(user.getHeight()));
        heightField.getStyleClass().add("text-field");
        heightField.setPrefWidth(300);
        heightBox.getChildren().addAll(heightLabel, heightField);

        // Weight field
        HBox weightBox = new HBox(15);
        weightBox.setAlignment(Pos.CENTER_LEFT);
        Label weightLabel = new Label("Weight (kg):");
        weightLabel.getStyleClass().add("label");
        weightLabel.setPrefWidth(100);
        TextField weightField = new TextField(String.valueOf(user.getWeight()));
        weightField.getStyleClass().add("text-field");
        weightField.setPrefWidth(300);
        weightBox.getChildren().addAll(weightLabel, weightField);

        // Gender field
        HBox genderBox = new HBox(15);
        genderBox.setAlignment(Pos.CENTER_LEFT);
        Label genderLabel = new Label("Gender:");
        genderLabel.getStyleClass().add("label");
        genderLabel.setPrefWidth(100);
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female");
        genderCombo.setValue(user.getGender());
        genderCombo.getStyleClass().add("combo-box");
        genderCombo.setPrefWidth(300);
        genderBox.getChildren().addAll(genderLabel, genderCombo);

        // Goal field
        HBox goalBox = new HBox(15);
        goalBox.setAlignment(Pos.CENTER_LEFT);
        Label goalLabel = new Label("Goal:");
        goalLabel.getStyleClass().add("label");
        goalLabel.setPrefWidth(100);
        ComboBox<String> goalCombo = new ComboBox<>();
        goalCombo.getItems().addAll("Lose Weight", "Gain Muscle", "Maintain Weight", "Improve Fitness");
        goalCombo.setValue(user.getGoal());
        goalCombo.getStyleClass().add("combo-box");
        goalCombo.setPrefWidth(300);
        goalBox.getChildren().addAll(goalLabel, goalCombo);

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveBtn = new Button("💾 Save Changes");
        saveBtn.getStyleClass().addAll("button", "button-primary");
        saveBtn.setOnAction(e -> {
            try {
                // Update user object
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setAge(Integer.parseInt(ageField.getText()));
                user.setHeight(Double.parseDouble(heightField.getText()));
                user.setWeight(Double.parseDouble(weightField.getText()));
                user.setGender(genderCombo.getValue());
                user.setGoal(goalCombo.getValue());
                
                // Save to database (you would implement this)
                showSuccessMessage("Profile updated successfully! 🎉");
                showDashboardContent(); // Return to dashboard
                
            } catch (NumberFormatException ex) {
                showErrorMessage("Please enter valid numbers for age, height, and weight.");
            }
        });

        Button cancelBtn = new Button("❌ Cancel");
        cancelBtn.getStyleClass().addAll("button", "button-danger");
        cancelBtn.setOnAction(e -> showDashboardContent());

        buttonBox.getChildren().addAll(saveBtn, cancelBtn);

        editForm.getChildren().addAll(formTitle, nameBox, emailBox, ageBox, heightBox, weightBox, genderBox, goalBox, buttonBox);
        editContainer.getChildren().add(editForm);
        mainContentArea.getChildren().addAll(title, editContainer);
    }
    
    private void showChangePasswordContent() {
        mainContentArea.getChildren().clear();
        
        Label title = new Label("🔒 Change Password");
        title.getStyleClass().addAll("title-label", "glow-blue");

        VBox passwordContainer = new VBox(30);
        passwordContainer.setAlignment(Pos.CENTER);
        passwordContainer.setPadding(new Insets(40));

        // Password Change Form
        VBox passwordForm = new VBox(20);
        passwordForm.getStyleClass().add("card");
        passwordForm.setAlignment(Pos.CENTER);
        passwordForm.setPrefWidth(500);
        passwordForm.setPadding(new Insets(40));

        Label formTitle = new Label("🔐 Update Your Password");
        formTitle.getStyleClass().add("card-title-label");
        
        Label infoLabel = new Label("Enter your current password and choose a new one");
        infoLabel.getStyleClass().add("subtitle-label");
        infoLabel.setStyle("-fx-text-fill: #8b949e;");

        // Current Password field
        HBox currentPassBox = new HBox(15);
        currentPassBox.setAlignment(Pos.CENTER_LEFT);
        Label currentPassLabel = new Label("Current Password:");
        currentPassLabel.getStyleClass().add("label");
        currentPassLabel.setPrefWidth(150);
        PasswordField currentPassField = new PasswordField();
        currentPassField.setPromptText("Enter current password");
        currentPassField.getStyleClass().add("text-field");
        currentPassField.setPrefWidth(300);
        currentPassBox.getChildren().addAll(currentPassLabel, currentPassField);

        // New Password field
        HBox newPassBox = new HBox(15);
        newPassBox.setAlignment(Pos.CENTER_LEFT);
        Label newPassLabel = new Label("New Password:");
        newPassLabel.getStyleClass().add("label");
        newPassLabel.setPrefWidth(150);
        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter new password (8+ chars)");
        newPassField.getStyleClass().add("text-field");
        newPassField.setPrefWidth(300);
        newPassBox.getChildren().addAll(newPassLabel, newPassField);

        // Confirm Password field
        HBox confirmPassBox = new HBox(15);
        confirmPassBox.setAlignment(Pos.CENTER_LEFT);
        Label confirmPassLabel = new Label("Confirm Password:");
        confirmPassLabel.getStyleClass().add("label");
        confirmPassLabel.setPrefWidth(150);
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");
        confirmPassField.getStyleClass().add("text-field");
        confirmPassField.setPrefWidth(300);
        confirmPassBox.getChildren().addAll(confirmPassLabel, confirmPassField);

        // Password requirements label
        Label requirementsLabel = new Label("✓ Password must be at least 8 characters long");
        requirementsLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveBtn = new Button("🔒 Change Password");
        saveBtn.getStyleClass().addAll("button", "button-primary");
        saveBtn.setOnAction(e -> {
            String currentPass = currentPassField.getText();
            String newPass = newPassField.getText();
            String confirmPass = confirmPassField.getText();
            
            // Validation
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showErrorMessage("All fields are required!");
                return;
            }
            
            if (newPass.length() < 8) {
                showErrorMessage("New password must be at least 8 characters long!");
                return;
            }
            
            if (!newPass.equals(confirmPass)) {
                showErrorMessage("New passwords do not match!");
                return;
            }
            
            // Verify current password
            String query = "SELECT password_hash FROM users WHERE id = ?";
            try (var conn = DatabaseConnection.getConnection();
                 var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, user.getId());
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (!BCrypt.checkpw(currentPass, storedHash)) {
                        showErrorMessage("Current password is incorrect!");
                        return;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorMessage("Failed to verify password.");
                return;
            }
            
            // Update password
            String updateQuery = "UPDATE users SET password_hash = ? WHERE id = ?";
            try (var conn = DatabaseConnection.getConnection();
                 var stmt = conn.prepareStatement(updateQuery)) {
                String newHash = BCrypt.hashpw(newPass, BCrypt.gensalt());
                stmt.setString(1, newHash);
                stmt.setInt(2, user.getId());
                stmt.executeUpdate();
                
                user.setPassword(newHash);
                showSuccessMessage("Password changed successfully! 🎉");
                showDashboardContent(); // Return to dashboard
                
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorMessage("Failed to update password.");
            }
        });

        Button cancelBtn = new Button("❌ Cancel");
        cancelBtn.getStyleClass().addAll("button", "button-danger");
        cancelBtn.setOnAction(e -> showDashboardContent());

        buttonBox.getChildren().addAll(saveBtn, cancelBtn);

        passwordForm.getChildren().addAll(formTitle, infoLabel, currentPassBox, newPassBox, confirmPassBox, requirementsLabel, buttonBox);
        passwordContainer.getChildren().add(passwordForm);
        mainContentArea.getChildren().addAll(title, passwordContainer);
    }
    
    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private VBox createStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 20;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 2;" +
            "-fx-pref-width: 200;" +
            "-fx-pref-height: 140;"
        );
        card.setAlignment(Pos.CENTER);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 40px;");

        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8b949e;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }

    private VBox createWellnessCard(UserMetrics metrics, double weight, UserService userService) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-border-color: #3fb950;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 25;" +
            "-fx-min-width: 350;" +
            "-fx-pref-width: 400;" +
            "-fx-max-width: 500;"
        );

        Label title = new Label("💚 Daily Wellness Tracker");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3fb950;");

        // Input fields with labels
        VBox waterBox = createInputBox("💧 Water (ml)", String.valueOf(metrics.getWaterMl()));
        VBox caloriesBox = createInputBox("🔥 Calories", String.valueOf(metrics.getCalories()));
        VBox sleepBox = createInputBox("😴 Sleep (hours)", String.valueOf(metrics.getSleepHours()));

        TextField waterInput = (TextField) waterBox.getChildren().get(1);
        TextField caloriesInput = (TextField) caloriesBox.getChildren().get(1);
        TextField sleepInput = (TextField) sleepBox.getChildren().get(1);

        Button saveBtn = createActionButton("💾 Save Metrics", e -> {
            try {
                int water = Integer.parseInt(waterInput.getText());
                int calories = Integer.parseInt(caloriesInput.getText());
                double sleep = Double.parseDouble(sleepInput.getText());
                
                UserMetrics newMetrics = new UserMetrics();
                newMetrics.setWaterMl(water);
                newMetrics.setCalories(calories);
                newMetrics.setSleepHours(sleep);
                
                if (userService.saveTodaysMetrics(user.getId(), newMetrics)) {
                    // Also save to progress tracker for automatic graph updates
                    ProgressTracker tracker = new ProgressTracker();
                    tracker.saveDailyProgress(user.getId(), user.getWeight(), calories, water, sleep);
                    
                    showAlert("Success", "✅ Metrics saved! Graphs updated automatically.");
                    showDashboardContent(); // Refresh to show updated graphs
                } else {
                    showAlert("Error", "Failed to save metrics.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid numbers.");
            }
        }, "#238636");

        card.getChildren().addAll(title, waterBox, caloriesBox, sleepBox, saveBtn);
        return card;
    }

    private VBox createInputBox(String label, String value) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        
        TextField field = new TextField(value);
        field.getStyleClass().add("text-field");
        
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private VBox createSleepCard(Map<String, Double> sleepData) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-border-color: #a371f7;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 25;" +
            "-fx-pref-width: 550;"
        );

        Label title = new Label("😴 Weekly Sleep Rhythm");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #a371f7;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-tick-label-fill: #8b949e;");
        NumberAxis yAxis = new NumberAxis(0, 10, 2);
        yAxis.setStyle("-fx-tick-label-fill: #8b949e;");
        yAxis.setLabel("Hours");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setPrefSize(500, 250);
        barChart.setStyle("-fx-background-color: transparent;");
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] fullDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            double hours = sleepData.getOrDefault(fullDays[i], 6.5);
            series.getData().add(new XYChart.Data<>(days[i], hours));
        }
        barChart.getData().add(series);

        double avgSleep = sleepData.values().stream().mapToDouble(Double::doubleValue).average().orElse(6.5);
        Label avgLabel = new Label(String.format("📊 Average: %.1f hours", avgSleep));
        avgLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 14px;");

        card.getChildren().addAll(title, barChart, avgLabel);
        return card;
    }

    private Button createActionButton(String text, javafx.event.EventHandler<ActionEvent> handler, String color) {
        Button btn = new Button(text);
        btn.setMinWidth(150);
        btn.setPrefWidth(180);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-pref-height: 45;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle()));
        btn.setOnAction(handler);
        return btn;
    }

    private void showWeightLog() {
        mainContentArea.getChildren().clear();

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setMaxWidth(600);

        // Back button at the top
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: rgba(88, 166, 255, 0.2);" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        ));
        backButton.setOnAction(e -> showDashboardContent());
        
        backButtonBox.getChildren().add(backButton);

        Label titleLabel = new Label("📝 Log Your Weight");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");

        Label instructionLabel = new Label("Enter your current weight:");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #c9d1d9;");

        TextField weightField = new TextField();
        weightField.setPromptText("Weight (kg)");
        weightField.getStyleClass().add("text-field");
        weightField.setPrefWidth(250);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveBtn = createActionButton("💾 Save", e -> {
            try {
                double weight = Double.parseDouble(weightField.getText());
                UserService service = new UserService();
                if (service.saveWeightLog(user.getId(), weight)) {
                    showAlert("Success", "Weight updated!");
                    showDashboardContent(); // Refresh dashboard
                } else {
                    showAlert("Error", "Failed to save weight.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Enter a valid number!");
            }
        }, "#238636");

        Button cancelBtn = createActionButton("❌ Cancel", e -> showDashboardContent(), "#f85149");

        buttonBox.getChildren().addAll(saveBtn, cancelBtn);

        layout.getChildren().addAll(backButtonBox, titleLabel, instructionLabel, weightField, buttonBox);

        mainContentArea.getChildren().add(layout);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    
    private VBox createRecommendationsCard() {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-border-color: #3fb950;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 25;" +
            "-fx-min-width: 350;" +
            "-fx-pref-width: 450;" +
            "-fx-max-width: 600;"
        );
        
        Label title = new Label("🎯 Personalized Recommendations");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3fb950;");
        
        Label goalLabel = new Label("Based on your goal: " + user.getGoal());
        goalLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8b949e; -fx-font-style: italic;");
        
        VBox recommendations = new VBox(10);
        
        // Generate recommendations based on user goal
        List<String> foodRecs = getFoodRecommendations(user.getGoal());
        List<String> exerciseRecs = getExerciseRecommendations(user.getGoal());
        
        Label foodTitle = new Label("🥗 Recommended Foods:");
        foodTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #c9d1d9;");
        recommendations.getChildren().add(foodTitle);
        
        for (String food : foodRecs) {
            Label foodLabel = new Label("  • " + food);
            foodLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");
            recommendations.getChildren().add(foodLabel);
        }
        
        Label exerciseTitle = new Label("\n🏋️ Recommended Exercises:");
        exerciseTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #c9d1d9;");
        recommendations.getChildren().add(exerciseTitle);
        
        for (String exercise : exerciseRecs) {
            Label exerciseLabel = new Label("  • " + exercise);
            exerciseLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");
            recommendations.getChildren().add(exerciseLabel);
        }
        
        card.getChildren().addAll(title, goalLabel, recommendations);
        return card;
    }
    
    private List<String> getFoodRecommendations(String goal) {
        List<String> foods = new ArrayList<>();
        if (goal.toLowerCase().contains("lose") || goal.toLowerCase().contains("weight loss")) {
            foods.add("Grilled chicken breast (lean protein)");
            foods.add("Leafy greens (spinach, kale)");
            foods.add("Quinoa (complex carbs)");
            foods.add("Greek yogurt (low-fat)");
            foods.add("Berries (antioxidants)");
        } else if (goal.toLowerCase().contains("gain") || goal.toLowerCase().contains("muscle")) {
            foods.add("Lean beef (protein & iron)");
            foods.add("Eggs (complete protein)");
            foods.add("Sweet potatoes (energy)");
            foods.add("Salmon (omega-3 & protein)");
            foods.add("Nuts & seeds (healthy fats)");
        } else {
            foods.add("Balanced meals with protein");
            foods.add("Whole grains");
            foods.add("Fresh vegetables");
            foods.add("Lean meats");
            foods.add("Healthy fats (avocado, nuts)");
        }
        return foods;
    }
    
    private List<String> getExerciseRecommendations(String goal) {
        List<String> exercises = new ArrayList<>();
        if (goal.toLowerCase().contains("lose") || goal.toLowerCase().contains("weight loss")) {
            exercises.add("HIIT Cardio (30 min)");
            exercises.add("Running/Jogging (45 min)");
            exercises.add("Cycling (40 min)");
            exercises.add("Jump rope (20 min)");
            exercises.add("Swimming (30 min)");
        } else if (goal.toLowerCase().contains("gain") || goal.toLowerCase().contains("muscle")) {
            exercises.add("Bench Press (4 sets x 8 reps)");
            exercises.add("Squats (4 sets x 10 reps)");
            exercises.add("Deadlifts (3 sets x 8 reps)");
            exercises.add("Pull-ups (3 sets x 12 reps)");
            exercises.add("Shoulder Press (3 sets x 10 reps)");
        } else {
            exercises.add("Full body workout (45 min)");
            exercises.add("Cardio mix (30 min)");
            exercises.add("Strength training (40 min)");
            exercises.add("Yoga/Stretching (20 min)");
            exercises.add("Core exercises (15 min)");
        }
        return exercises;
    }
    
    private VBox createPlanTrackerCard() {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-border-color: #a371f7;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 25;" +
            "-fx-min-width: 350;" +
            "-fx-pref-width: 450;" +
            "-fx-max-width: 600;"
        );
        
        Label title = new Label("📋 Today's Plan");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #a371f7;");
        
        VBox plansList = new VBox(10);
        
        for (Map<String, Object> plan : userPlans) {
            HBox planItem = createPlanItem(plan);
            plansList.getChildren().add(planItem);
        }
        
        Button addPlanBtn = createActionButton("➕ Add New Plan", e -> showAddPlanDialog(), "#1f6feb");
        
        card.getChildren().addAll(title, plansList, addPlanBtn);
        return card;
    }
    
    private HBox createPlanItem(Map<String, Object> plan) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle(
            "-fx-background-color: rgba(33, 38, 45, 0.8);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;"
        );
        
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected((Boolean) plan.get("completed"));
        checkBox.setStyle("-fx-text-fill: #c9d1d9;");
        
        VBox textBox = new VBox(5);
        Label nameLabel = new Label((String) plan.get("name"));
        nameLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label timeLabel = new Label("⏰ " + plan.get("time"));
        timeLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        
        textBox.getChildren().addAll(nameLabel, timeLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Delete button
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #f85149;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        );
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(
            "-fx-background-color: rgba(248, 81, 73, 0.2);" +
            "-fx-text-fill: #f85149;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #f85149;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        
        deleteBtn.setOnAction(e -> {
            // Confirm deletion
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Plan");
            confirm.setHeaderText("Are you sure?");
            confirm.setContentText("Do you want to delete the plan: " + plan.get("name") + "?");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                int planId = (Integer) plan.get("id");
                if (planStorage.deletePlan(planId)) {
                    addNotification("🗑️ Plan Deleted", "Plan removed: " + plan.get("name"));
                    loadTodaysPlans(); // Reload plans
                    showDashboardContent(); // Refresh dashboard
                } else {
                    showAlert("Error", "Failed to delete plan!");
                }
            }
        });
        
        checkBox.setOnAction(e -> {
            plan.put("completed", checkBox.isSelected());
            // Save to database
            int planId = (Integer) plan.get("id");
            planStorage.updatePlanCompletion(planId, checkBox.isSelected());
            
            if (checkBox.isSelected()) {
                showAppreciationMessage((String) plan.get("name"));
                addNotification("✅ Plan Completed", "Great job completing: " + plan.get("name"));
            }
            
            // Check for unfinished plans and notify
            checkUnfinishedPlans();
        });
        
        item.getChildren().addAll(checkBox, textBox, spacer, deleteBtn);
        return item;
    }
    
    private void showAddPlanDialog() {
        mainContentArea.getChildren().clear();
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setMaxWidth(600);
        layout.setAlignment(Pos.CENTER);
        
        // Back button at the top
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: rgba(88, 166, 255, 0.2);" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        ));
        backButton.setOnAction(e -> showDashboardContent());
        
        backButtonBox.getChildren().add(backButton);
        
        Label titleLabel = new Label("Add New Plan");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Plan name");
        nameField.getStyleClass().add("text-field");
        
        TextField timeField = new TextField();
        timeField.setPromptText("Time (e.g., 07:00 AM)");
        timeField.getStyleClass().add("text-field");
        
        Button saveBtn = createActionButton("💾 Save Plan", e -> {
            String planName = nameField.getText();
            String planTime = timeField.getText();
            
            if (planName.isEmpty() || planTime.isEmpty()) {
                showAlert("Error", "Please fill in all fields!");
                return;
            }
            
            // Save to database
            LocalDate today = LocalDate.now();
            if (planStorage.savePlan(user.getId(), planName, planTime, today)) {
                addNotification("📋 Plan Added", "New plan added: " + planName + " at " + planTime);
                loadTodaysPlans(); // Reload plans from database
                showDashboardContent();
            } else {
                showAlert("Error", "Failed to save plan!");
            }
        }, "#238636");
        
        layout.getChildren().addAll(
            backButtonBox,
            titleLabel,
            nameField, timeField, saveBtn
        );
        
        mainContentArea.getChildren().add(layout);
    }
    
    private void showAppreciationMessage(String planName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Great Job! 🎉");
        alert.setHeaderText("Congratulations!");
        alert.setContentText("Amazing work completing '" + planName + "'!\n\n" +
            "You're crushing your goals! Keep up the fantastic effort! 💪\n\n" +
            "Streak: " + user.getStreak() + " days 🔥");
        alert.showAndWait();
    }
    
    private void showMotivationalMessage() {
        if (userPlans.stream().noneMatch(p -> (Boolean) p.get("completed"))) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Daily Reminder 💪");
                alert.setHeaderText("Time to Crush Your Goals!");
                alert.setContentText("You have " + userPlans.size() + " tasks planned for today.\n\n" +
                    "Remember: Every small step counts!\n" +
                    "Let's make today count! 🔥");
                alert.show();
            }));
            timeline.play();
        }
    }
    
    private void showNotifications() {
        mainContentArea.getChildren().clear();
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setMaxWidth(700);
        layout.setAlignment(Pos.CENTER);
        
        // Back button at the top
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: rgba(88, 166, 255, 0.2);" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        ));
        backButton.setOnAction(e -> showDashboardContent());
        
        backButtonBox.getChildren().add(backButton);
        
        Label title = new Label("🔔 Notifications (" + notifications.size() + ")");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");
        
        ScrollPane scrollPane = new ScrollPane();
        VBox notifList = new VBox(10);
        
        // Show real-time notifications
        if (notifications.isEmpty()) {
            Label noNotif = new Label("No notifications yet");
            noNotif.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 14px; -fx-padding: 20;");
            notifList.getChildren().add(noNotif);
        } else {
            for (Map<String, String> notif : notifications) {
                notifList.getChildren().add(
                    createNotificationItem(
                        notif.get("title"),
                        notif.get("message"),
                        notif.get("time")
                    )
                );
            }
        }
        
        scrollPane.setContent(notifList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        Button clearBtn = createActionButton("Clear All", e -> {
            notifications.clear();
            notificationCount = 0;
            notificationBadge.setText("0");
            notificationBadge.setVisible(false);
            showDashboardContent();
        }, "#f85149");
        
        layout.getChildren().addAll(backButtonBox, title, scrollPane, clearBtn);
        
        mainContentArea.getChildren().add(layout);
    }
    
    private VBox createNotificationItem(String title, String message, String time) {
        VBox item = new VBox(5);
        item.setStyle(
            "-fx-background-color: rgba(33, 38, 45, 0.8);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 15;"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #58a6ff; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 13px;");
        
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");
        
        item.getChildren().addAll(titleLabel, messageLabel, timeLabel);
        return item;
    }
    
    private void showReminderDialog() {
        mainContentArea.getChildren().clear();
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setMaxWidth(700);
        layout.setAlignment(Pos.TOP_CENTER);
        
        // Back button at the top
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: rgba(88, 166, 255, 0.2);" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        ));
        backButton.setOnAction(e -> showDashboardContent());
        
        backButtonBox.getChildren().add(backButton);
        
        Label titleLabel = new Label("⏰ Manage Your Reminders");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");
        
        // Initialize reminder storage
        ReminderStorage reminderStorage = new ReminderStorage();
        reminderStorage.initializeTable();
        
        // Display existing reminders
        VBox remindersList = new VBox(10);
        remindersList.setStyle("-fx-padding: 10;");
        
        ScrollPane remindersScroll = new ScrollPane(remindersList);
        remindersScroll.setFitToWidth(true);
        remindersScroll.setPrefHeight(200);
        remindersScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Load and display reminders
        loadReminders(remindersList, reminderStorage);
        
        // Add new reminder section
        VBox addReminderBox = new VBox(15);
        addReminderBox.setStyle(
            "-fx-background-color: rgba(33, 38, 45, 0.9);" +
            "-fx-border-color: #30363d;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 20;"
        );
        
        Label addLabel = new Label("➕ Add New Reminder");
        addLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Workout", "Meal", "Water", "Sleep", "Medication", "Custom");
        typeCombo.setPromptText("Select reminder type");
        typeCombo.getStyleClass().add("combo-box");
        typeCombo.setPrefWidth(300);
        
        TextField timeField = new TextField();
        timeField.setPromptText("Time (e.g., 07:00 AM)");
        timeField.getStyleClass().add("text-field");
        timeField.setPrefWidth(300);
        
        TextField messageField = new TextField();
        messageField.setPromptText("Reminder message");
        messageField.getStyleClass().add("text-field");
        messageField.setPrefWidth(300);
        
        Button addBtn = createActionButton("💾 Add Reminder", e -> {
            String type = typeCombo.getValue();
            String time = timeField.getText();
            String message = messageField.getText();
            
            if (type == null || time.isEmpty() || message.isEmpty()) {
                showAlert("Error", "Please fill in all fields!");
                return;
            }
            
            if (reminderStorage.saveReminder(user.getId(), type, time, message)) {
                addNotification("⏰ Reminder Added", "New reminder set for " + time);
                typeCombo.setValue(null);
                timeField.clear();
                messageField.clear();
                loadReminders(remindersList, reminderStorage);
            } else {
                showAlert("Error", "Failed to save reminder!");
            }
        }, "#238636");
        
        addReminderBox.getChildren().addAll(addLabel, typeCombo, timeField, messageField, addBtn);
        
        layout.getChildren().addAll(backButtonBox, titleLabel, remindersScroll, addReminderBox);
        
        mainContentArea.getChildren().add(layout);
    }
    
    private void loadReminders(VBox container, ReminderStorage storage) {
        container.getChildren().clear();
        List<Map<String, Object>> reminders = storage.getUserReminders(user.getId());
        
        if (reminders.isEmpty()) {
            Label noReminders = new Label("No reminders set yet. Add one below!");
            noReminders.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 14px; -fx-padding: 20;");
            container.getChildren().add(noReminders);
            return;
        }
        
        for (Map<String, Object> reminder : reminders) {
            HBox reminderItem = new HBox(15);
            reminderItem.setAlignment(Pos.CENTER_LEFT);
            reminderItem.setStyle(
                "-fx-background-color: rgba(33, 38, 45, 0.9);" +
                "-fx-border-color: #30363d;" +
                "-fx-border-radius: 8px;" +
                "-fx-padding: 15;"
            );
            
            CheckBox enabledCheck = new CheckBox();
            enabledCheck.setSelected((Boolean) reminder.get("enabled"));
            enabledCheck.setStyle("-fx-text-fill: white;");
            enabledCheck.setOnAction(e -> {
                int id = (Integer) reminder.get("id");
                storage.updateReminderStatus(id, enabledCheck.isSelected());
            });
            
            VBox textBox = new VBox(5);
            Label typeLabel = new Label(reminder.get("type") + " - " + reminder.get("time"));
            typeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            
            Label messageLabel = new Label((String) reminder.get("message"));
            messageLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
            
            textBox.getChildren().addAll(typeLabel, messageLabel);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button deleteBtn = new Button("🗑️");
            deleteBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #f85149;" +
                "-fx-font-size: 16px;" +
                "-fx-cursor: hand;"
            );
            deleteBtn.setOnAction(e -> {
                int id = (Integer) reminder.get("id");
                if (storage.deleteReminder(id)) {
                    loadReminders(container, storage);
                    addNotification("🗑️ Reminder Deleted", "Reminder removed successfully");
                }
            });
            
            reminderItem.getChildren().addAll(enabledCheck, textBox, spacer, deleteBtn);
            container.getChildren().add(reminderItem);
        }
    }
    
    private void showBMICalculator() {
        mainContentArea.getChildren().clear();
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setMaxWidth(600);
        layout.setAlignment(Pos.CENTER);
        
        // Back button at the top
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Tools");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: rgba(88, 166, 255, 0.2);" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        ));
        backButton.setOnAction(e -> showToolsContent());
        
        backButtonBox.getChildren().add(backButton);
        
        Label title = new Label("📊 BMI Calculator");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");
        
        Label heightLabel = new Label("Height (cm):");
        heightLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        TextField heightField = new TextField();
        heightField.setPromptText("Enter height in cm");
        heightField.getStyleClass().add("text-field");
        
        Label weightLabel = new Label("Weight (kg):");
        weightLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        TextField weightField = new TextField();
        weightField.setPromptText("Enter weight in kg");
        weightField.getStyleClass().add("text-field");
        
        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3fb950; -fx-padding: 10;");
        
        Label categoryLabel = new Label("");
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8b949e; -fx-padding: 5;");
        
        Button calculateBtn = createActionButton("🧮 Calculate BMI", e -> {
            try {
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());
                
                if (height <= 0 || weight <= 0) {
                    showAlert("Error", "Please enter valid positive numbers!");
                    return;
                }
                
                double heightInMeters = height / 100.0;
                double bmi = weight / (heightInMeters * heightInMeters);
                
                String category;
                String color;
                if (bmi < 18.5) {
                    category = "Underweight";
                    color = "#58a6ff";
                } else if (bmi < 25) {
                    category = "Normal weight";
                    color = "#3fb950";
                } else if (bmi < 30) {
                    category = "Overweight";
                    color = "#f85149";
                } else {
                    category = "Obese";
                    color = "#da3633";
                }
                
                resultLabel.setText(String.format("Your BMI: %.2f", bmi));
                resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-padding: 10;");
                categoryLabel.setText("Category: " + category);
                categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + color + "; -fx-padding: 5;");
                
                // Add notification
                addNotification("BMI Calculated", "Your BMI is " + String.format("%.2f", bmi) + " (" + category + ")");
                
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid numbers!");
            }
        }, "#238636");
        
        layout.getChildren().addAll(backButtonBox, title, heightLabel, heightField, weightLabel, weightField, calculateBtn, resultLabel, categoryLabel);
        
        mainContentArea.getChildren().add(layout);
    }
    
    private void showCalorieCalculator() {
        mainContentArea.getChildren().clear();
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setMaxWidth(600);
        layout.setAlignment(Pos.CENTER);
        
        // Back button at the top
        HBox backButtonBox = new HBox();
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button backButton = new Button("← Back to Tools");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: rgba(88, 166, 255, 0.2);" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        ));
        backButton.setOnAction(e -> showToolsContent());
        
        backButtonBox.getChildren().add(backButton);
        
        Label title = new Label("🔥 Daily Calorie Calculator");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #f85149;");
        
        Label ageLabel = new Label("Age:");
        ageLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        TextField ageField = new TextField();
        ageField.setPromptText("Enter age");
        ageField.getStyleClass().add("text-field");
        
        Label weightLabel = new Label("Weight (kg):");
        weightLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        TextField weightField = new TextField();
        weightField.setPromptText("Enter weight in kg");
        weightField.getStyleClass().add("text-field");
        
        Label heightLabel = new Label("Height (cm):");
        heightLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        TextField heightField = new TextField();
        heightField.setPromptText("Enter height in cm");
        heightField.getStyleClass().add("text-field");
        
        Label genderLabel = new Label("Gender:");
        genderLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList("Male", "Female"));
        genderCombo.setValue("Male");
        genderCombo.getStyleClass().add("combo-box");
        
        Label activityLabel = new Label("Activity Level:");
        activityLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 14px;");
        ComboBox<String> activityCombo = new ComboBox<>(FXCollections.observableArrayList(
            "Sedentary (little or no exercise)",
            "Lightly active (1-3 days/week)",
            "Moderately active (3-5 days/week)",
            "Very active (6-7 days/week)",
            "Extra active (athlete)"
        ));
        activityCombo.setValue("Moderately active (3-5 days/week)");
        activityCombo.getStyleClass().add("combo-box");
        
        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f85149; -fx-padding: 10;");
        
        Label infoLabel = new Label("");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8b949e; -fx-padding: 5; -fx-wrap-text: true;");
        infoLabel.setMaxWidth(400);
        
        Button calculateBtn = createActionButton("🧮 Calculate Calories", e -> {
            try {
                int age = Integer.parseInt(ageField.getText());
                double weight = Double.parseDouble(weightField.getText());
                double height = Double.parseDouble(heightField.getText());
                String gender = genderCombo.getValue();
                String activity = activityCombo.getValue();
                
                if (age <= 0 || weight <= 0 || height <= 0) {
                    showAlert("Error", "Please enter valid positive numbers!");
                    return;
                }
                
                // Calculate BMR using Mifflin-St Jeor Equation
                double bmr;
                if (gender.equals("Male")) {
                    bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
                } else {
                    bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
                }
                
                // Apply activity multiplier
                double activityMultiplier;
                if (activity.contains("Sedentary")) {
                    activityMultiplier = 1.2;
                } else if (activity.contains("Lightly")) {
                    activityMultiplier = 1.375;
                } else if (activity.contains("Moderately")) {
                    activityMultiplier = 1.55;
                } else if (activity.contains("Very")) {
                    activityMultiplier = 1.725;
                } else {
                    activityMultiplier = 1.9;
                }
                
                double dailyCalories = bmr * activityMultiplier;
                
                resultLabel.setText(String.format("Daily Calories: %.0f kcal", dailyCalories));
                infoLabel.setText(String.format(
                    "BMR: %.0f kcal/day\n" +
                    "To lose weight: %.0f kcal/day\n" +
                    "To gain weight: %.0f kcal/day",
                    bmr, dailyCalories - 500, dailyCalories + 500
                ));
                
                // Add notification
                addNotification("Calories Calculated", String.format("Your daily calorie need: %.0f kcal", dailyCalories));
                
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid numbers!");
            }
        }, "#238636");
        
        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(15, backButtonBox, title, ageLabel, ageField, weightLabel, weightField, 
                                heightLabel, heightField, genderLabel, genderCombo, 
                                activityLabel, activityCombo, calculateBtn, resultLabel, infoLabel);
        content.setAlignment(Pos.CENTER);
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        layout.getChildren().add(scrollPane);
        
        mainContentArea.getChildren().add(layout);
    }
    
    private List<Map<String, String>> notifications = new ArrayList<>();
    
    private void addNotification(String title, String message) {
        Map<String, String> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("time", "Just now");
        notifications.add(0, notification); // Add to beginning
        
        notificationCount = notifications.size();
        
        // Only update badge if it exists (UI has been created)
        if (notificationBadge != null) {
            notificationBadge.setText(String.valueOf(notificationCount));
            notificationBadge.setVisible(true);
        }
    }
    
    private VBox createDailyChallengesSection() {
        VBox section = new VBox(15);
        section.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-border-color: #3fb950;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 15, 0, 0, 5);"
        );
        
        VBox headerBox = new VBox(15);
        
        Label title = new Label("🎯 Daily Challenges");
        title.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );
        
        FlowPane buttonFlow = new FlowPane();
        buttonFlow.setHgap(10);
        buttonFlow.setVgap(10);
        buttonFlow.setAlignment(Pos.CENTER_LEFT);
        
        Button viewAllBtn = createActionButton("📋 View All Challenges", e -> showCombinedChallenges(), "#1f6feb");
        viewAllBtn.setMinWidth(180);
        viewAllBtn.setPrefWidth(200);
        
        buttonFlow.getChildren().add(viewAllBtn);
        headerBox.getChildren().addAll(title, buttonFlow);
        
        Label subtitle = new Label("Complete today's challenges to earn points!");
        subtitle.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #8b949e;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0, 0, 1);"
        );
        
        // Get today's recommendations (matching CombinedChallengeView)
        LocalDate today = LocalDate.now();
        List<String> exercises = DailyRecommendationService.getDailyExercises(today);
        List<String> nutrition = DailyRecommendationService.getDailyNutrition(today);
        List<String> hydration = DailyRecommendationService.getDailyHydration(today);
        List<String> recovery = DailyRecommendationService.getDailyRecovery(today);
        
        // Get storage for saving progress
        CheckboxChallengeStorage storage = new CheckboxChallengeStorage();
        Map<String, Map<String, Boolean>> todayStatus = storage.getDailyChallengeStatus(user.getId(), today);
        
        // Create challenge categories with checkboxes - Use FlowPane for responsive layout
        FlowPane challengeContainer = new FlowPane();
        challengeContainer.setHgap(15);
        challengeContainer.setVgap(15);
        challengeContainer.setAlignment(Pos.CENTER);
        challengeContainer.setPrefWrapLength(1000); // Wrap at 1000px
        
        VBox exerciseBox = createInteractiveChallengeCategory("🏋️ Exercise", "EXERCISE", exercises, todayStatus.get("EXERCISE"), storage, today);
        VBox nutritionBox = createInteractiveChallengeCategory("🥗 Nutrition", "NUTRITION", nutrition, todayStatus.get("NUTRITION"), storage, today);
        VBox hydrationBox = createInteractiveChallengeCategory("💧 Hydration", "HYDRATION", hydration, todayStatus.get("HYDRATION"), storage, today);
        VBox recoveryBox = createInteractiveChallengeCategory("😴 Recovery", "RECOVERY", recovery, todayStatus.get("RECOVERY"), storage, today);
        
        challengeContainer.getChildren().addAll(exerciseBox, nutritionBox, hydrationBox, recoveryBox);
        
        // Progress summary
        int totalItems = exercises.size() + nutrition.size() + hydration.size() + recovery.size();
        long completedItems = todayStatus.values().stream()
            .flatMap(map -> map.values().stream())
            .mapToLong(b -> b ? 1 : 0)
            .sum();
        
        double progressPercentage = totalItems > 0 ? (completedItems * 100.0) / totalItems : 0;
        int points = DailyRecommendationService.calculatePoints(
            todayStatus.get("EXERCISE"), todayStatus.get("NUTRITION"), 
            todayStatus.get("HYDRATION"), todayStatus.get("RECOVERY"));
        
        Label progressLabel = new Label(String.format("📊 Progress: %.0f%% Complete (%d/%d) | 🎯 Points: %d", 
            progressPercentage, completedItems, totalItems, points));
        progressLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );
        
        section.getChildren().addAll(headerBox, subtitle, challengeContainer, progressLabel);
        return section;
    }
    
    private VBox createInteractiveChallengeCategory(String categoryTitle, String categoryType, 
                                                     List<String> items, Map<String, Boolean> completionStatus,
                                                     CheckboxChallengeStorage storage, LocalDate date) {
        VBox categoryBox = new VBox(10);
        categoryBox.setStyle(
            "-fx-background-color: rgba(33, 38, 45, 0.8);" +
            "-fx-border-color: #30363d;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 15;" +
            "-fx-min-width: 250;" +
            "-fx-pref-width: 280;" +
            "-fx-max-width: 350;"
        );
        
        Label titleLabel = new Label(categoryTitle);
        titleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 2, 0, 0, 1);"
        );
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        scrollPane.setMaxHeight(200);
        
        VBox itemsBox = new VBox(8);
        itemsBox.setStyle("-fx-padding: 5;");
        
        // Show ALL items with checkboxes
        for (String item : items) {
            CheckBox checkBox = new CheckBox(item);
            checkBox.setStyle(
                "-fx-text-fill: #c9d1d9;" +
                "-fx-font-size: 12px;"
            );
            checkBox.setWrapText(true);
            checkBox.setMaxWidth(220);
            
            // Set initial state from storage
            boolean isCompleted = completionStatus != null && completionStatus.getOrDefault(item, false);
            checkBox.setSelected(isCompleted);
            
            // Save progress when checkbox is clicked
            checkBox.setOnAction(e -> {
                storage.updateSingleChallenge(user.getId(), date, categoryType, item, checkBox.isSelected());
                
                // Show encouragement message
                if (checkBox.isSelected()) {
                    addNotification("Challenge Completed! 🎉", "Great job completing: " + item);
                }
                
                // Refresh dashboard to update progress
                showDashboardContent();
            });
            
            itemsBox.getChildren().add(checkBox);
        }
        
        scrollPane.setContent(itemsBox);
        
        // Category completion stats
        long completed = completionStatus != null ? completionStatus.values().stream().filter(b -> b).count() : 0;
        Label statsLabel = new Label(String.format("✓ %d/%d completed", completed, items.size()));
        statsLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #8b949e;" +
            "-fx-font-weight: bold;"
        );
        
        categoryBox.getChildren().addAll(titleLabel, scrollPane, statsLabel);
        return categoryBox;
    }
    
    private VBox createChallengeCard(Map<String, Object> challenge, ChallengeManager challengeManager) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: rgba(33, 38, 45, 0.9);" +
            "-fx-border-color: " + getChallengeColor((String) challenge.get("type")) + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10px;" +
            "-fx-padding: 15;" +
            "-fx-pref-width: 280;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 3);"
        );
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Challenge title
        Label titleLabel = new Label((String) challenge.get("title"));
        titleLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 2, 0, 0, 1);"
        );
        
        // Challenge description
        Label descLabel = new Label((String) challenge.get("description"));
        descLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #c9d1d9;" +
            "-fx-wrap-text: true;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0, 0, 1);"
        );
        descLabel.setMaxWidth(250);
        
        // Progress bar
        double current = (Double) challenge.get("current");
        double target = (Double) challenge.get("target");
        double progress = Math.min(current / target * 100, 100);
        
        ProgressBar progressBar = new ProgressBar(progress / 100);
        progressBar.setPrefWidth(250);
        progressBar.getStyleClass().add("progress-bar");
        
        // Progress text
        String unit = (String) challenge.get("unit");
        Label progressLabel = new Label(String.format("%.0f / %.0f %s (%.0f%%)", current, target, unit, progress));
        progressLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #8b949e;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0, 0, 1);"
        );
        
        // Points
        int points = (Integer) challenge.get("points");
        boolean completed = (Boolean) challenge.get("completed");
        Label pointsLabel = new Label((completed ? "✅ " : "🎯 ") + points + " points");
        pointsLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + (completed ? "#3fb950" : "#f85149") + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 2, 0, 0, 1);"
        );
        
        // Update button for manual input
        Button updateBtn = new Button("📝 Update");
        updateBtn.setStyle(
            "-fx-background-color: " + getChallengeColor((String) challenge.get("type")) + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-pref-width: 80;" +
            "-fx-pref-height: 25;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        updateBtn.setOnAction(e -> showChallengeUpdateDialog(challenge, challengeManager));
        
        card.getChildren().addAll(titleLabel, descLabel, progressBar, progressLabel, pointsLabel, updateBtn);
        return card;
    }
    
    private String getChallengeColor(String type) {
        switch (type) {
            case "EXERCISE": return "#f85149";
            case "NUTRITION": return "#3fb950";
            case "HYDRATION": return "#58a6ff";
            case "SLEEP": return "#a371f7";
            default: return "#8b949e";
        }
    }
    
    private void showChallengeUpdateDialog(Map<String, Object> challenge, ChallengeManager challengeManager) {
        mainContentArea.getChildren().clear();
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setMaxWidth(600);
        layout.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label((String) challenge.get("title"));
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label currentLabel = new Label("Current Progress: " + challenge.get("current") + " " + challenge.get("unit"));
        currentLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 16px;");
        
        TextField valueField = new TextField();
        valueField.setPromptText("Enter new value");
        valueField.getStyleClass().add("text-field");
        
        Button saveBtn = createActionButton("💾 Save", e -> {
            try {
                double newValue = Double.parseDouble(valueField.getText());
                String type = (String) challenge.get("type");
                
                // Update challenge progress
                challengeManager.updateChallengeProgress(user.getId(), type, newValue);
                
                // Also update corresponding metrics if applicable
                updateMetricsFromChallenge(type, newValue);
                
                showAlert("Success", "✅ Challenge progress updated! Dashboard will refresh.");
                showDashboardContent(); // Refresh dashboard
                
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number.");
            }
        }, "#238636");
        
        Button cancelBtn = createActionButton("❌ Cancel", e -> showDashboardContent(), "#f85149");
        
        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);
        
        layout.getChildren().addAll(titleLabel, currentLabel, valueField, buttons);
        
        mainContentArea.getChildren().add(layout);
    }
    
    private void updateMetricsFromChallenge(String challengeType, double value) {
        // Update corresponding metrics when challenge is updated
        UserService userService = new UserService();
        UserMetrics metrics = userService.getTodaysMetrics(user.getId());
        
        switch (challengeType) {
            case "HYDRATION":
                metrics.setWaterMl((int) value);
                break;
            case "SLEEP":
                metrics.setSleepHours(value);
                break;
            case "NUTRITION":
                // Could update calories if it's a calorie-based nutrition challenge
                break;
        }
        
        userService.saveTodaysMetrics(user.getId(), metrics);
        
        // Also save to progress tracker
        ProgressTracker tracker = new ProgressTracker();
        tracker.saveDailyProgress(user.getId(), user.getWeight(), metrics.getCalories(), metrics.getWaterMl(), metrics.getSleepHours());
    }
    
    private VBox createWeeklyProgressSummary() {
        VBox section = new VBox(20);
        section.setStyle(
            "-fx-background-color: rgba(22, 27, 34, 0.9);" +
            "-fx-border-color: #a371f7;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 15, 0, 0, 5);"
        );
        
        VBox header = new VBox(15);
        
        Label title = new Label("📊 Weekly Progress Summary");
        title.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );
        
        Button viewDetailedBtn = createActionButton("📈 View Detailed Progress", e -> showCombinedChallenges(), "#a371f7");
        
        header.getChildren().addAll(title, viewDetailedBtn);
        
        // Get weekly challenge data
        CheckboxChallengeStorage checkboxStorage = new CheckboxChallengeStorage();
        checkboxStorage.initializeTables();
        List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData = checkboxStorage.getWeeklyChallengeData(user.getId());
        
        if (weeklyData.isEmpty()) {
            Label noDataLabel = new Label("📊 No weekly data yet. Complete daily challenges to see your progress!");
            noDataLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 30;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
            );
            section.getChildren().addAll(header, noDataLabel);
            return section;
        }
        
        // Calculate weekly statistics
        double totalCompletion = 0;
        int totalPoints = 0;
        int daysWithData = 0;
        
        for (CheckboxChallengeStorage.WeeklyChallengeDay dayData : weeklyData) {
            totalCompletion += dayData.getOverallCompletion();
            totalPoints += dayData.getTotalPoints();
            if (dayData.getOverallCompletion() > 0) {
                daysWithData++;
            }
        }
        
        double avgCompletion = daysWithData > 0 ? totalCompletion / daysWithData : 0;
        
        // Weekly stats cards - Use FlowPane for responsive layout
        FlowPane statsRow = new FlowPane();
        statsRow.setHgap(20);
        statsRow.setVgap(20);
        statsRow.setAlignment(Pos.CENTER);
        
        VBox avgCard = createWeeklyStatCard("📊 Average Completion", String.format("%.1f%%", avgCompletion), "#58a6ff");
        VBox pointsCard = createWeeklyStatCard("🎯 Total Points", String.valueOf(totalPoints), "#3fb950");
        VBox activeDaysCard = createWeeklyStatCard("📅 Active Days", daysWithData + "/7", "#f85149");
        
        // Calculate streak
        int currentStreak = calculateCurrentStreak(weeklyData);
        VBox streakCard = createWeeklyStatCard("🔥 Current Streak", currentStreak + " days", "#a371f7");
        
        statsRow.getChildren().addAll(avgCard, pointsCard, activeDaysCard, streakCard);
        
        // Weekly progress bar
        VBox progressBox = new VBox(10);
        progressBox.setAlignment(Pos.CENTER);
        
        Label progressLabel = new Label("This Week's Overall Progress");
        progressLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );
        
        ProgressBar weeklyProgressBar = new ProgressBar(avgCompletion / 100.0);
        weeklyProgressBar.setMaxWidth(Double.MAX_VALUE);
        weeklyProgressBar.setPrefHeight(20);
        weeklyProgressBar.getStyleClass().add("progress-bar");
        
        Label percentLabel = new Label(String.format("%.1f%% Complete", avgCompletion));
        percentLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #8b949e;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0, 0, 1);"
        );
        
        progressBox.getChildren().addAll(progressLabel, weeklyProgressBar, percentLabel);
        
        // Quick insights
        VBox insightsBox = new VBox(10);
        Label insightsTitle = new Label("💡 Quick Insights");
        insightsTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0, 0, 1);"
        );
        
        String insight = generateWeeklyInsight(avgCompletion, currentStreak, totalPoints);
        Label insightLabel = new Label("• " + insight);
        insightLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #c9d1d9;" +
            "-fx-wrap-text: true;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0, 0, 1);"
        );
        insightLabel.setMaxWidth(800);
        
        insightsBox.getChildren().addAll(insightsTitle, insightLabel);
        
        section.getChildren().addAll(header, statsRow, progressBox, insightsBox);
        return section;
    }
    
    private VBox createWeeklyStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: rgba(33, 38, 45, 0.8);" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 20;" +
            "-fx-pref-width: 180;" +
            "-fx-alignment: center;"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #8b949e;" +
            "-fx-font-weight: bold;" +
            "-fx-alignment: center;"
        );
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-alignment: center;"
        );
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private int calculateCurrentStreak(List<CheckboxChallengeStorage.WeeklyChallengeDay> weeklyData) {
        int streak = 0;
        // Count consecutive days with >80% completion from most recent
        for (int i = weeklyData.size() - 1; i >= 0; i--) {
            if (weeklyData.get(i).getOverallCompletion() >= 80) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }
    
    private String generateWeeklyInsight(double avgCompletion, int streak, int totalPoints) {
        if (avgCompletion >= 90) {
            return "Outstanding performance! You're crushing your goals with " + String.format("%.0f%%", avgCompletion) + " completion rate.";
        } else if (avgCompletion >= 75) {
            return "Great consistency! You're on track with " + String.format("%.0f%%", avgCompletion) + " completion. Keep up the momentum!";
        } else if (avgCompletion >= 50) {
            return "Good progress with " + String.format("%.0f%%", avgCompletion) + " completion. Focus on daily consistency to reach the next level.";
        } else if (avgCompletion > 0) {
            return "Getting started! Every step counts. Try to complete at least one challenge daily to build momentum.";
        } else {
            return "Ready to begin your fitness journey? Start with daily challenges to track your progress!";
        }
    }
    
    private void checkUnfinishedTasks(Map<String, Map<String, Boolean>> todayStatus, int totalItems, long completedItems) {
        // Calculate unfinished tasks
        long unfinishedItems = totalItems - completedItems;
        
        if (unfinishedItems > 0) {
            double completionPercentage = totalItems > 0 ? (completedItems * 100.0) / totalItems : 0;
            
            // Create notification based on completion level
            String notificationTitle;
            String notificationMessage;
            
            if (completionPercentage == 0) {
                notificationTitle = "🎯 Start Your Day Right!";
                notificationMessage = "You have " + totalItems + " challenges waiting for you today. Let's get started!";
            } else if (completionPercentage < 25) {
                notificationTitle = "💪 Keep Going!";
                notificationMessage = "You've completed " + completedItems + "/" + totalItems + " challenges. " + unfinishedItems + " more to go!";
            } else if (completionPercentage < 50) {
                notificationTitle = "🔥 You're Making Progress!";
                notificationMessage = "Great start! " + completedItems + "/" + totalItems + " done. Complete " + unfinishedItems + " more to reach 50%!";
            } else if (completionPercentage < 75) {
                notificationTitle = "⭐ Halfway There!";
                notificationMessage = "Awesome! " + completedItems + "/" + totalItems + " completed. Just " + unfinishedItems + " challenges left!";
            } else if (completionPercentage < 100) {
                notificationTitle = "🎉 Almost Done!";
                notificationMessage = "Amazing work! Only " + unfinishedItems + " challenge(s) remaining. You can do it!";
            } else {
                return; // All tasks completed, no notification needed
            }
            
            // Add notification
            addNotification(notificationTitle, notificationMessage);
            
            // Show breakdown of unfinished tasks by category
            StringBuilder breakdown = new StringBuilder();
            for (Map.Entry<String, Map<String, Boolean>> entry : todayStatus.entrySet()) {
                String category = entry.getKey();
                Map<String, Boolean> items = entry.getValue();
                long categoryUnfinished = items.values().stream().filter(b -> !b).count();
                
                if (categoryUnfinished > 0) {
                    String categoryIcon = getCategoryIcon(category);
                    if (breakdown.length() > 0) breakdown.append(", ");
                    breakdown.append(categoryIcon).append(" ").append(categoryUnfinished);
                }
            }
            
            if (breakdown.length() > 0) {
                addNotification("📋 Task Breakdown", "Unfinished: " + breakdown.toString());
            }
        } else {
            // All tasks completed!
            addNotification("🏆 Perfect Day!", "Congratulations! You've completed all " + totalItems + " challenges today! 🎉");
        }
    }
    
    private String getCategoryIcon(String category) {
        switch (category) {
            case "EXERCISE": return "🏋️";
            case "NUTRITION": return "🥗";
            case "HYDRATION": return "💧";
            case "RECOVERY": return "😴";
            default: return "📌";
        }
    }
    
    private void checkUnfinishedPlans() {
        LocalDate today = LocalDate.now();
        int unfinishedCount = planStorage.getUnfinishedPlansCount(user.getId(), today);
        
        if (unfinishedCount > 0) {
            List<String> unfinishedPlans = planStorage.getUnfinishedPlanNames(user.getId(), today);
            
            String title = "📋 Unfinished Plans";
            String message = "You have " + unfinishedCount + " unfinished plan(s):\n";
            
            for (int i = 0; i < Math.min(3, unfinishedPlans.size()); i++) {
                message += "• " + unfinishedPlans.get(i) + "\n";
            }
            
            if (unfinishedPlans.size() > 3) {
                message += "... and " + (unfinishedPlans.size() - 3) + " more";
            }
            
            addNotification(title, message.trim());
        }
    }

}