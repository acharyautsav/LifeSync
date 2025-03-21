package lifesync.ui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeView extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the root layout with a cool-toned sunset gradient using #F7D1D7 and #736DED
        StackPane root = new StackPane();
        LinearGradient sunsetGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, 
                new Stop(0, Color.web("#F7D1D7")), new Stop(1, Color.web("#736DED"))); // Light pink to soft purple gradient
        root.setBackground(new Background(new BackgroundFill(sunsetGradient, null, null)));

        // Welcome Text with aesthetic styling
        Text welcomeText = new Text("Welcome to LifeSync");
        welcomeText.setFont(Font.font("Dancing Script", 50)); // Elegant font for sunset theme
        welcomeText.setFill(Color.WHITE);

        // Fade-in effect for welcome text
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), welcomeText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Buttons for "Login as Admin", "Login as User", and "Continue as Guest"
        Button loginAsAdminButton = new Button("Login as Admin");
        Button loginAsUserButton = new Button("Login as User");
        Button continueAsGuestButton = new Button("Continue as Guest");

        // Set button styles with sunset color tones
        loginAsAdminButton.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;");
        loginAsAdminButton.setOnAction(e -> new LoginViewAdmin().start(primaryStage));
        loginAsUserButton.setStyle("-fx-background-color: #B9A1E6; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;");
        loginAsUserButton.setOnAction(e -> new LoginView().start(primaryStage));
        continueAsGuestButton.setStyle("-fx-background-color: #B8A0D5; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;");

        // Refined hover effect for buttons with soft transitions
        loginAsAdminButton.setOnMouseEntered(e -> loginAsAdminButton.setStyle("-fx-background-color: #7A50C4; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;"));
        loginAsAdminButton.setOnMouseExited(e -> loginAsAdminButton.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;"));

        loginAsUserButton.setOnMouseEntered(e -> loginAsUserButton.setStyle("-fx-background-color: #7A50C4; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;"));
        loginAsUserButton.setOnMouseExited(e -> loginAsUserButton.setStyle("-fx-background-color: #B9A1E6; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;"));

        continueAsGuestButton.setOnMouseEntered(e -> continueAsGuestButton.setStyle("-fx-background-color: #7A50C4; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;"));
        continueAsGuestButton.setOnMouseExited(e -> continueAsGuestButton.setStyle("-fx-background-color: #B8A0D5; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10px; -fx-background-radius: 20px; -fx-text-fill: white;"));

        // "Or" text between buttons with subtle typography style
        Text orHeader = new Text("Or");
        orHeader.setFont(Font.font("Arial", 20));
        orHeader.setFill(Color.WHITE);

        // Add buttons and "Or" header to VBox layout with refined alignment
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(welcomeText, loginAsAdminButton, loginAsUserButton, orHeader, continueAsGuestButton);
        
        // Add a smooth fade transition for the entire VBox
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), vbox);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.setCycleCount(1);
        fadeOut.play();

        // Set the root layout to the scene
        root.getChildren().add(vbox);

        // Scene settings
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Welcome to LifeSync");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
