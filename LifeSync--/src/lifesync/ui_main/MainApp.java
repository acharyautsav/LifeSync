package lifesync.ui_main;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
	@Override
	public void start(Stage primaryStage) {
		WelcomeView welcome = new WelcomeView(); // Create Dashboard instance
		welcome.start(primaryStage); // Load the Dashboard UI
	}

	public static void main(String[] args) {
		launch(args); // Launch JavaFX Application
	}
}
