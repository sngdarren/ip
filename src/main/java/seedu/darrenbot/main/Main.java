package seedu.darrenbot.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Try to make main Class
 */

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello World! This is DarrenBot");
        Scene scene = new Scene(new StackPane(helloWorld), 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
