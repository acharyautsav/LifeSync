package lifesync.ui_main;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeView extends Application {

    // Soft purple pastel color palette
    private static final Color COLOR_BG_START = Color.web("#F8F7FC");  // Very light lavender
    private static final Color COLOR_BG_END = Color.web("#EFE9F4");    // Light lavender-gray
    
    private static final Color COLOR_ACCENT = Color.web("#D0BDF4");    // Soft lavender
    private static final Color COLOR_TEXT_PRIMARY = Color.web("#4A4063"); // Dark purple-gray
    private static final Color COLOR_TEXT_SECONDARY = Color.web("#8E82A6"); // Medium purple-gray
    
    private static final Color COLOR_BUTTON_PRIMARY = Color.web("#D8D0F0"); // Light lavender
    private static final Color COLOR_BUTTON_SECONDARY = Color.web("#E2DCF7"); // Lighter lavender
    
    private static final Color COLOR_BUTTON_HOVER = Color.web("#C5B6E3"); // Slightly darker lavender for hover

    @Override
    public void start(Stage primaryStage) {
        // Create the root layout with a subtle gradient
        StackPane root = new StackPane();
        
        // Create a soft, subtle gradient
        LinearGradient softGradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, COLOR_BG_START),
            new Stop(1, COLOR_BG_END)
        );
        root.setBackground(new Background(new BackgroundFill(softGradient, null, null)));

        createWelcomeContent(root, primaryStage);

        // Create a scene that adapts to screen size
        Scene scene = new Scene(root);
        primaryStage.setTitle("Welcome to LifeSync");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Maximize the window initially

        // Show stage
        primaryStage.show();
    }
    
    // Changed from private to public so it can be accessed from LoginView
    public void createWelcomeContent(StackPane root, Stage primaryStage) {
        // Create a container for the main content
        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(450);
        contentBox.setPadding(new Insets(40));
        
        // Add a clean white panel behind the content
        StackPane contentPanel = new StackPane(contentBox);
        contentPanel.setBackground(new Background(new BackgroundFill(
            Color.web("#FFFFFF"), new javafx.scene.layout.CornerRadii(20), null)));
        contentPanel.setEffect(new DropShadow(15, Color.web("#C5B6E320")));
        contentPanel.setPadding(new Insets(40));
        contentPanel.setMaxWidth(500);

        // Welcome Text with clean styling
        Text welcomeText = new Text("Welcome to LifeSync");
        welcomeText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 38));
        welcomeText.setFill(COLOR_TEXT_PRIMARY);
        welcomeText.setTextAlignment(TextAlignment.CENTER);

        // Subtitle text
        Text subtitleText = new Text("Your personal life management system");
        subtitleText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 15));
        subtitleText.setFill(COLOR_TEXT_SECONDARY);
        subtitleText.setTextAlignment(TextAlignment.CENTER);

        // Clean button styling with pastel colors
        String buttonAdminStyle = String.format(
            "-fx-background-color: %s; " +
            "-fx-font-size: 15px; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-weight: normal; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-text-fill: %s; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);",
            toRgbaString(COLOR_BUTTON_PRIMARY),
            toRgbaString(COLOR_TEXT_PRIMARY)
        );
        
        String buttonUserStyle = String.format(
            "-fx-background-color: %s; " +
            "-fx-font-size: 15px; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-weight: normal; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-text-fill: %s; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);",
            toRgbaString(COLOR_BUTTON_SECONDARY),
            toRgbaString(COLOR_TEXT_PRIMARY)
        );

        String buttonHoverStyle = String.format(
            "-fx-background-color: %s; " +
            "-fx-font-size: 15px; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-weight: normal; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-text-fill: %s; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 6, 0, 0, 2);",
            toRgbaString(COLOR_BUTTON_HOVER),
            toRgbaString(COLOR_TEXT_PRIMARY)
        );

        // Create buttons with clean styling
        Button loginAsAdminButton = new Button("Login as Admin");
        Button loginAsUserButton = new Button("Login as User");
        
        // Apply base styles
        loginAsAdminButton.setStyle(buttonAdminStyle);
        loginAsUserButton.setStyle(buttonUserStyle);
        
        // Set button widths
        loginAsAdminButton.setPrefWidth(250);
        loginAsUserButton.setPrefWidth(250);
        
        // Modified button actions
        loginAsAdminButton.setOnAction(e -> {
            // Clear the root and create admin login content
            root.getChildren().clear();
            LoginViewAdmin loginViewAdmin = new LoginViewAdmin();
            loginViewAdmin.createLoginContent(root, primaryStage);
        });
        loginAsUserButton.setOnAction(e -> {
            // Clear the root and create login view content
            root.getChildren().clear();
            LoginView loginView = new LoginView();
            loginView.createLoginContent(root, primaryStage);
        });
        
        // Add hover effects
        loginAsAdminButton.setOnMouseEntered(e -> loginAsAdminButton.setStyle(buttonHoverStyle));
        loginAsAdminButton.setOnMouseExited(e -> loginAsAdminButton.setStyle(buttonAdminStyle));
        
        loginAsUserButton.setOnMouseEntered(e -> loginAsUserButton.setStyle(buttonHoverStyle));
        loginAsUserButton.setOnMouseExited(e -> loginAsUserButton.setStyle(buttonUserStyle));

        // Add all elements to the content box
        contentBox.getChildren().addAll(
            welcomeText,
            subtitleText,
            new Region() {{ setPrefHeight(10); }},
            loginAsAdminButton,
            loginAsUserButton
        );

        // Add subtle animations
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), contentPanel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(1), contentPanel);
        scaleIn.setFromX(0.98);
        scaleIn.setFromY(0.98);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        
        // Play animations
        fadeIn.play();
        scaleIn.play();

        // Add a subtle decorative element - a small purple accent line at the top of the panel
        Rectangle accentLine = new Rectangle(80, 4);
        accentLine.setFill(COLOR_ACCENT);
        accentLine.setArcWidth(4);
        accentLine.setArcHeight(4);
        StackPane.setAlignment(accentLine, Pos.TOP_CENTER);
        StackPane.setMargin(accentLine, new Insets(0, 0, 0, 0));
        contentPanel.getChildren().add(accentLine);

        // Add the content panel to the root
        root.getChildren().add(contentPanel);
        
        // Make VBox dynamically resize based on screen
        Scene scene = primaryStage.getScene();
        if (scene != null) {
            contentBox.maxWidthProperty().bind(scene.widthProperty().multiply(0.5)); // Adjust width to 50% of screen
        }
    }
    
    // Helper method to convert JavaFX Color to rgba string for CSS
    private String toRgbaString(Color color) {
        return String.format("rgba(%d, %d, %d, %.2f)",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255),
            color.getOpacity());
    }

    public static void main(String[] args) {
        launch(args);
    }
}