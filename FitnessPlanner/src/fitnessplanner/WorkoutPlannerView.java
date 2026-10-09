package fitnessplanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WorkoutPlannerView {
    private User user;

    public WorkoutPlannerView(User user) {
        this.user = user;
    }

    public void show(Stage primaryStage) {
        Label label = new Label("Workout Planner (Coming Soon)");
        Button backBtn = new Button("◀️ Back to Dashboard");
        backBtn.setOnAction(e -> {
            primaryStage.close();
            new DashboardView(user).show(new Stage());
        });

        VBox layout = new VBox(20, label, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #121212; -fx-text-fill: white;");

        Scene scene = new Scene(layout, 500, 400);
        scene.getStylesheets().add(getClass().getResource("fitness.css").toExternalForm());
        primaryStage.setTitle("FitPlan - Workout Planner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}