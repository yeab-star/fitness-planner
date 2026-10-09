package fitnessplanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// ⬇️ COMMENT THIS OUT FOR NOW
// import com.mysql.cj.jdbc.Driver;

public class FitnessPlanner extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("✅ JavaFX Working!");
        Scene scene = new Scene(new StackPane(label), 400, 200);
        stage.setTitle("FitPlan");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}