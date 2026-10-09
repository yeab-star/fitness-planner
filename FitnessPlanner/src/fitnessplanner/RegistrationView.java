package fitnessplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegistrationView {
    private TextField nameField;
    private TextField ageField;
    private TextField heightField;
    private TextField weightField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private ComboBox<String> genderCombo;
    private ComboBox<String> goalCombo;
    // Security question fields
    private ComboBox<String> securityQuestionCombo;
    private TextField securityAnswerField;

    public void show(Stage primaryStage) {
        nameField = new TextField();
        nameField.setPromptText("Full Name");
        ageField = new TextField();
        ageField.setPromptText("Age (e.g., 25)");
        heightField = new TextField();
        heightField.setPromptText("Height (cm, e.g., 175)");
        weightField = new TextField();
        weightField.setPromptText("Weight (kg, e.g., 70)");
        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female");
        genderCombo.setPromptText("Select Gender");

        goalCombo = new ComboBox<>();
        goalCombo.getItems().addAll("Lose Weight", "Gain Muscle", "Maintain");
        goalCombo.setPromptText("Fitness Goal");

        // Security Question Setup
        securityQuestionCombo = new ComboBox<>();
        securityQuestionCombo.getItems().addAll(
            "What is your favorite color?",
            "What is your mother's maiden name?",
            "What was your first pet's name?",
            "What city were you born in?",
            "What is your favorite food?",
            "What is your favorite movie?",
            "What is your favorite book?",
            "What is your favorite sports team?"
        );
        securityQuestionCombo.setPromptText("Select Security Question");

        securityAnswerField = new TextField();
        securityAnswerField.setPromptText("Security Answer");

        Button registerBtn = new Button("✅ Register");
        registerBtn.getStyleClass().addAll("button", "button-primary");
        registerBtn.setPrefWidth(200);
        registerBtn.setPrefHeight(40);
        registerBtn.setOnAction(e -> handleRegistration(primaryStage));

        Button cancelBtn = new Button("❌ Cancel");
        cancelBtn.getStyleClass().addAll("button", "button-danger");
        cancelBtn.setPrefWidth(200);
        cancelBtn.setPrefHeight(40);
        cancelBtn.setOnAction(e -> primaryStage.close());

        // Button container
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerBtn, cancelBtn);

        VBox layout = new VBox(12,
            new Label("Create Your Account"),
            nameField, ageField, heightField, weightField, emailField,
            passwordField, confirmPasswordField,
            genderCombo, goalCombo,
            securityQuestionCombo, securityAnswerField, // Add security fields
            buttonBox
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        layout.setMaxWidth(500);

        // Wrap in StackPane with login background
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.getStyleClass().add("login-background");
        root.getChildren().add(layout);

        Scene scene = new Scene(root, 500, 650); // Taller window for more fields
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        primaryStage.setTitle("FitPlan - Sign Up");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleRegistration(Stage stage) {
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String heightStr = heightField.getText().trim();
        String weightStr = weightField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String gender = genderCombo.getValue();
        String goal = goalCombo.getValue();
        // Get security question and answer
        String securityQuestion = securityQuestionCombo.getValue();
        String securityAnswer = securityAnswerField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Name, email, and passwords are required!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match!");
            return;
        }
        if (gender == null) {
            showAlert("Error", "Please select a gender.");
            return;
        }
        if (goal == null) {
            showAlert("Error", "Please select a fitness goal.");
            return;
        }
        // Validate security question
        if (securityQuestion == null || securityAnswer.trim().isEmpty()) {
            showAlert("Error", "Please select a security question and provide an answer!");
            return;
        }

        Integer age = null;
        Double height = null;
        Double weight = null;
        try {
            if (!ageStr.isEmpty()) age = Integer.parseInt(ageStr);
            if (!heightStr.isEmpty()) height = Double.parseDouble(heightStr);
            if (!weightStr.isEmpty()) weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Age, height, and weight must be numbers!");
            return;
        }

        String dbGender = "Male".equals(gender) ? "MALE" : "FEMALE";
        String dbGoal = "Lose Weight".equals(goal) ? "LOSE_WEIGHT" : 
                       "Gain Muscle".equals(goal) ? "GAIN_MUSCLE" : "MAINTAIN";

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String hashedSecurityAnswer = BCrypt.hashpw(securityAnswer.trim(), BCrypt.gensalt());

        String query = "INSERT INTO users (name, age, height, weight, email, password_hash, gender, goal, security_question, security_answer_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setObject(2, age);
            stmt.setObject(3, height);
            stmt.setObject(4, weight);
            stmt.setString(5, email);
            stmt.setString(6, hashedPassword);
            stmt.setString(7, dbGender);
            stmt.setString(8, dbGoal);
            stmt.setString(9, securityQuestion);
            stmt.setString(10, hashedSecurityAnswer);
            stmt.executeUpdate();
            showAlert("Success", "Account created! You can now log in.");
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                showAlert("Error", "Email already exists!");
            } else {
                showAlert("Error", "Failed to create account.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}