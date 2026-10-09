package fitnessplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileEditView {
    private User user;
    private Stage dashboardStage;
    private TextField nameField;
    private TextField ageField;
    private TextField heightField;
    private TextField weightField;
    private PasswordField currentPassField;
    private PasswordField newPassField;
    private PasswordField confirmPassField;
    private ComboBox<String> genderCombo;
    private ComboBox<String> goalCombo;

    public ProfileEditView(User user, Stage dashboardStage) {
        this.user = user;
        this.dashboardStage = dashboardStage;
    }

    public void show(Stage primaryStage) {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.getStyleClass().add("container");
        
        // Add back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("button", "button-primary");
        backButton.setOnAction(e -> primaryStage.close());
        
        nameField = new TextField(user.getName());
        ageField = new TextField(String.valueOf(user.getAge()));
        heightField = new TextField(String.valueOf(user.getHeight()));
        weightField = new TextField(String.valueOf(user.getWeight()));
        
        currentPassField = new PasswordField();
        currentPassField.setPromptText("Current Password (required)");
        newPassField = new PasswordField();
        newPassField.setPromptText("New Password (8+ chars)");
        confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm New Password");

        genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female");
        genderCombo.setValue(user.getGender());
        
        goalCombo = new ComboBox<>();
        goalCombo.getItems().addAll("Lose Weight", "Gain Muscle", "Maintain");
        goalCombo.setValue(
            user.getGoal().equals("LOSE_WEIGHT") ? "Lose Weight" : 
            user.getGoal().equals("GAIN_MUSCLE") ? "Gain Muscle" : "Maintain"
        );

        Button saveBtn = new Button("💾 Save Changes");
        saveBtn.setOnAction(e -> saveProfile(primaryStage));

        Button cancelBtn = new Button("❌ Cancel");
        cancelBtn.setOnAction(e -> primaryStage.close());

        // Profile Information Section
        Label profileTitle = new Label("📝 Profile Information");
        profileTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");
        
        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-text-fill: white;");
        Label ageLabel = new Label("Age:");
        ageLabel.setStyle("-fx-text-fill: white;");
        Label heightLabel = new Label("Height (cm):");
        heightLabel.setStyle("-fx-text-fill: white;");
        Label weightLabel = new Label("Weight (kg):");
        weightLabel.setStyle("-fx-text-fill: white;");
        Label genderLabel = new Label("Gender:");
        genderLabel.setStyle("-fx-text-fill: white;");
        Label goalLabel = new Label("Goal:");
        goalLabel.setStyle("-fx-text-fill: white;");
        
        // Password Change Section
        Separator passwordSeparator = new Separator();
        passwordSeparator.setStyle("-fx-background-color: rgba(88, 166, 255, 0.5);");
        
        Label passwordTitle = new Label("🔒 Change Password (Optional)");
        passwordTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #58a6ff;");
        
        Label passwordNote = new Label("Leave blank to keep current password");
        passwordNote.setStyle("-fx-font-size: 12px; -fx-text-fill: #8b949e; -fx-font-style: italic;");
        
        VBox layout = new VBox(12,
            backButton,
            new Label("Edit Your Profile"),
            profileTitle,
            nameLabel, nameField,
            ageLabel, ageField,
            heightLabel, heightField,
            weightLabel, weightField,
            genderLabel, genderCombo,
            goalLabel, goalCombo,
            passwordSeparator,
            passwordTitle,
            passwordNote,
            currentPassField,
            newPassField,
            confirmPassField,
            saveBtn,
            cancelBtn
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #121212; -fx-text-fill: white;");

        // Add scroll pane to ensure all content is visible
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #121212; -fx-background-color: #121212;");

        Scene scene = new Scene(scrollPane, 450, 700);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        primaryStage.setTitle("Edit Profile");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveProfile(Stage editStage) {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Error", "Name is required!");
            return;
        }
        // Handle password change if user wants to change it
        boolean passwordChangeRequested = !newPassField.getText().isEmpty() || !confirmPassField.getText().isEmpty();
        
        if (passwordChangeRequested) {
            // Validate current password is provided
            if (currentPassField.getText().isEmpty()) {
                showAlert("Error", "Please enter your current password to change it!");
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
                    if (!BCrypt.checkpw(currentPassField.getText(), storedHash)) {
                        showAlert("Error", "Current password is incorrect!");
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to verify password.");
                return;
            }
            
            // Validate new password
            if (newPassField.getText().isEmpty()) {
                showAlert("Error", "Please enter a new password!");
                return;
            }
            
            if (newPassField.getText().length() < 8) {
                showAlert("Error", "New password must be at least 8 characters long!");
                return;
            }
            
            if (!newPassField.getText().equals(confirmPassField.getText())) {
                showAlert("Error", "New passwords do not match!");
                return;
            }
            
            // Hash and set new password
            user.setPassword(BCrypt.hashpw(newPassField.getText(), BCrypt.gensalt()));
        }

        user.setName(nameField.getText().trim());
        try {
            user.setAge(Integer.parseInt(ageField.getText().trim()));
            user.setHeight(Double.parseDouble(heightField.getText().trim()));
            user.setWeight(Double.parseDouble(weightField.getText().trim()));
        } catch (NumberFormatException e) {
            showAlert("Error", "Age, height, and weight must be numbers!");
            return;
        }
        user.setGender(genderCombo.getValue());
        user.setGoal(
            "Lose Weight".equals(goalCombo.getValue()) ? "LOSE_WEIGHT" :
            "Gain Muscle".equals(goalCombo.getValue()) ? "GAIN_MUSCLE" : "MAINTAIN"
        );

        String updateQuery = "UPDATE users SET name = ?, age = ?, height = ?, weight = ?, gender = ?, goal = ?, password_hash = ? WHERE id = ?";
        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getAge());
            stmt.setDouble(3, user.getHeight());
            stmt.setDouble(4, user.getWeight());
            stmt.setString(5, user.getGender());
            stmt.setString(6, user.getGoal());
            stmt.setString(7, user.getPassword());
            stmt.setInt(8, user.getId());
            stmt.executeUpdate();
            showAlert("Success", "Profile updated successfully!");
            editStage.close();
            dashboardStage.close();
            new DashboardView(user).show(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save profile.");
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