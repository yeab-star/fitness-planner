package fitnessplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginView {
    private TextField emailField;
    private PasswordField passwordField;
    private TextField visiblePasswordField;

    public LoginView() {
    }

    public void show(Stage primaryStage) {
        Label titleLabel = new Label("💪 FitPlan Login");
        titleLabel.setStyle(
            "-fx-font-size: 48px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 8, 0, 0, 3);"
        );

        emailField = new TextField();
        emailField.setPromptText("Email");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setVisible(false);
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());

        Button toggleBtn = new Button("👁️");
        toggleBtn.setOnAction(e -> togglePasswordVisibility());

        HBox passBox = new HBox(5, passwordField, visiblePasswordField, toggleBtn);
        Button loginBtn = new Button("Login");
        Hyperlink forgotPasswordLink = new Hyperlink("Forgot Password?");
        Hyperlink signupLink = new Hyperlink("Don't have an account? Sign Up");

        VBox layout = new VBox(15, titleLabel, emailField, passBox, loginBtn, forgotPasswordLink, signupLink);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: transparent;");
        layout.setMaxWidth(500);

        loginBtn.setOnAction(e -> handleLogin(primaryStage));
        forgotPasswordLink.setOnAction(e -> showPasswordReset());
        signupLink.setOnAction(e -> new RegistrationView().show(new Stage()));

        // Create root with immersive fitness background
        StackPane root = new StackPane();
        root.getStyleClass().add("login-background");
        root.getChildren().add(layout);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        primaryStage.setTitle("💪 FitPlan - Login");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Maximize for desktop
        primaryStage.show();
    }

    private void togglePasswordVisibility() {
        boolean visible = visiblePasswordField.isVisible();
        if (visible) {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
        } else {
            visiblePasswordField.setText(passwordField.getText());
            passwordField.setVisible(false);
            visiblePasswordField.setVisible(true);
        }
    }

    private void handleLogin(Stage loginStage) {
        String email = emailField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();

        if (email.isEmpty() || password.isEmpty() || password.length() < 8) {
            showAlert("Error", "Valid email and 8+ char password required!");
            return;
        }

        String query = "SELECT id, name, age, height, weight, gender, goal, password_hash, streak FROM users WHERE email = ?";
        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, hash)) {
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        email,
                        hash,
                        rs.getInt("age"),
                        rs.getDouble("height"),
                        rs.getDouble("weight"),
                        rs.getString("gender"),
                        rs.getString("goal")
                    );
                    user.setStreak(rs.getInt("streak"));
                    loginStage.close();
                    new DashboardView(user).show(new Stage());
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showAlert("Error", "Invalid credentials!");
    }

    private void showPasswordReset() {
        Stage resetStage = new Stage();
        resetStage.setTitle("Password Reset");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        Label questionLabel = new Label("Enter your email first");
        questionLabel.setStyle("-fx-text-fill: #E0E0E0;");
        
        TextField answerField = new TextField();
        answerField.setPromptText("Security Answer");
        answerField.setDisable(true); // Disable until email is entered
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password (8+ characters)");
        newPasswordField.setDisable(true);

        Button submitBtn = new Button("Reset Password");
        submitBtn.setDisable(true);

        // Enable fields when email is entered
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                // Fetch security question from database
                String secQuestion = getSecurityQuestion(newValue.trim());
                if (secQuestion != null && !secQuestion.isEmpty()) {
                    questionLabel.setText("Security Question: " + secQuestion);
                    answerField.setDisable(false);
                    answerField.requestFocus();
                } else {
                    questionLabel.setText("Email not found or no security question set.");
                    answerField.setDisable(true);
                    newPasswordField.setDisable(true);
                    submitBtn.setDisable(true);
                }
            } else {
                questionLabel.setText("Enter your email first");
                answerField.setDisable(true);
                newPasswordField.setDisable(true);
                submitBtn.setDisable(true);
            }
        });

        // Enable password field when answer is entered
        answerField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                newPasswordField.setDisable(false);
                newPasswordField.requestFocus();
            } else {
                newPasswordField.setDisable(true);
                submitBtn.setDisable(true);
            }
        });

        // Enable submit button when all fields are filled
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitBtn.setDisable(
                emailField.getText().trim().isEmpty() ||
                answerField.getText().trim().isEmpty() ||
                newValue.trim().isEmpty() ||
                newValue.trim().length() < 8
            );
        });

        submitBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String answer = answerField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            
            if (email.isEmpty() || answer.isEmpty() || newPassword.isEmpty()) {
                showAlert("Error", "All fields are required!");
                return;
            }
            
            if (newPassword.length() < 8) {
                showAlert("Error", "Password must be at least 8 characters!");
                return;
            }
            
            // Verify security answer against database
            if (verifySecurityAnswer(email, answer)) {
                // Update password in database
                String updateQuery = "UPDATE users SET password_hash = ? WHERE email = ?";
                try (var conn = DatabaseConnection.getConnection();
                     var stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                    stmt.setString(2, email);
                    if (stmt.executeUpdate() > 0) {
                        showAlert("Success", "Password reset successfully!");
                        resetStage.close();
                    } else {
                        showAlert("Error", "Failed to reset password.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Database error.");
                }
            } else {
                showAlert("Error", "Invalid security answer.");
            }
        });
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> resetStage.close());
        
        VBox layout = new VBox(15,
            new Label("Reset Your Password"),
            emailField,
            questionLabel,
            answerField,
            newPasswordField,
            submitBtn,
            cancelBtn
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #121212;");

        Scene scene = new Scene(layout, 400, 350);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        resetStage.setScene(scene);
        resetStage.show();
    }

    // Fetch security question from database
    private String getSecurityQuestion(String email) {
        String query = "SELECT security_question FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("security_question");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Verify security answer from database
    private boolean verifySecurityAnswer(String email, String answer) {
        String query = "SELECT security_answer_hash FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("security_answer_hash");
                return BCrypt.checkpw(answer, storedHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}