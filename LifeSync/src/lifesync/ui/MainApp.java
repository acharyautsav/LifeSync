package lifesync.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
	@Override
	public void start(Stage primaryStage) {
		DashboardView dashboard = new DashboardView(); // Create Dashboard instance
		dashboard.start(primaryStage); // Load the Dashboard UI
	}

	public static void main(String[] args) {
		launch(args); // Launch JavaFX Application
	}
}
