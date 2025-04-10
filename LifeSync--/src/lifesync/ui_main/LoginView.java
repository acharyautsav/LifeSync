package lifesync.ui_main;

import databasePackage.DbConnection;
import databasePackage.UserSession;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginView extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorMessage;
    private Button loginButton; // Added to make it accessible in navigation methods
    private DbConnection dbConnection;

    // Soft purple pastel color palette (same as WelcomeView)
    private static final Color COLOR_BG_START = Color.web("#F8F7FC");  // Very light lavender
    private static final Color COLOR_BG_END = Color.web("#EFE9F4");    // Light lavender-gray
    
    private static final Color COLOR_ACCENT = Color.web("#D0BDF4");    // Soft lavender
    private static final Color COLOR_TEXT_PRIMARY = Color.web("#4A4063"); // Dark purple-gray
    private static final Color COLOR_TEXT_SECONDARY = Color.web("#8E82A6"); // Medium purple-gray
    
    private static final Color COLOR_BUTTON_PRIMARY = Color.web("#D8D0F0"); // Light lavender
    private static final Color COLOR_BUTTON_HOVER = Color.web("#C5B6E3"); // Slightly darker lavender for hover

    @Override
    public void start(Stage primaryStage) {
        // Initialize database connection
        dbConnection = DbConnection.getInstance();
        
        // Create the root layout with a subtle gradient
        StackPane root = new StackPane();
        
        // Create the login content
        createLoginContent(root, primaryStage);
        
        // Create a scene that adapts to screen size
        Scene scene = new Scene(root);
        primaryStage.setTitle("LifeSync - Login");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Maximize the window initially

        // Show stage
        primaryStage.show();
    }
    
    // New method to create login content without creating a new window
    public void createLoginContent(StackPane root, Stage primaryStage) {
        // Initialize database connection if not already done
        if (dbConnection == null) {
            dbConnection = DbConnection.getInstance();
        }
        
        // Create a soft, subtle gradient
        LinearGradient softGradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, COLOR_BG_START),
            new Stop(1, COLOR_BG_END)
        );
        root.setBackground(new Background(new BackgroundFill(softGradient, null, null)));

        // Create a container for the main content
        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(450);
        contentBox.setPadding(new Insets(40));
        
        // Add a clean white panel behind the content
        StackPane contentPanel = new StackPane(contentBox);
        contentPanel.setBackground(new Background(new BackgroundFill(
            Color.web("#FFFFFF"), new CornerRadii(20), null)));
        contentPanel.setEffect(new DropShadow(15, Color.web("#C5B6E320")));
        contentPanel.setPadding(new Insets(40));
        contentPanel.setMaxWidth(500);

        // Login Text with clean styling
        Text loginText = new Text("Login to LifeSync");
        loginText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 38));
        loginText.setFill(COLOR_TEXT_PRIMARY);
        loginText.setTextAlignment(TextAlignment.CENTER);

        // Create fields with clean styling
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        // Style the text fields
        String textFieldStyle = String.format(
            "-fx-background-color: %s; " +
            "-fx-font-size: 15px; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 12px 15px; " +
            "-fx-background-radius: 8px; " +
            "-fx-prompt-text-fill: %s;",
            "#F8F7FC",
            toRgbaString(COLOR_TEXT_SECONDARY)
        );
        
        usernameField.setStyle(textFieldStyle);
        passwordField.setStyle(textFieldStyle);
        usernameField.setPrefWidth(250);
        passwordField.setPrefWidth(250);
        
        // Error message label (for validation)
        errorMessage = new Label();
        errorMessage.setTextFill(Color.web("#E57373"));
        errorMessage.setFont(Font.font("Segoe UI", 13));
        
        // Login Button with clean styling (same style as WelcomeView buttons)
        String buttonStyle = String.format(
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
        
        loginButton = new Button("Login"); // Initialize the class field
        loginButton.setStyle(buttonStyle);
        loginButton.setPrefWidth(250);
        
        // Add hover effects (same as WelcomeView)
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(buttonHoverStyle));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(buttonStyle));
        
        // "Don't have an account?" text
        Text signUpText = new Text("Don't have an account?");
        signUpText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        signUpText.setFill(COLOR_TEXT_SECONDARY);
        
        // Create a container for the "or" section (same layout as WelcomeView "or" section)
        HBox orSection = new HBox(15, new Separator(), signUpText, new Separator());
        orSection.setAlignment(Pos.CENTER);
        orSection.setPadding(new Insets(5, 0, 5, 0));
        
        // Style the separators in the or section
        for (int i = 0; i < orSection.getChildren().size(); i++) {
            if (orSection.getChildren().get(i) instanceof Separator) {
                Separator sep = (Separator) orSection.getChildren().get(i);
                sep.setStyle("-fx-background-color: #E2DCF7;");
                sep.setPrefWidth(60);
            }
        }
        
        // Sign up button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle(buttonStyle);
        signUpButton.setPrefWidth(250);
        
        // Add hover effects to sign up button
        signUpButton.setOnMouseEntered(e -> signUpButton.setStyle(buttonHoverStyle));
        signUpButton.setOnMouseExited(e -> signUpButton.setStyle(buttonStyle));
        
        // Back button to return to welcome screen
        Button backButton = new Button("Back");
        backButton.setStyle(buttonStyle);
        backButton.setPrefWidth(250);
        
        // Add hover effects to back button
        backButton.setOnMouseEntered(e -> backButton.setStyle(buttonHoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));
        
        // Add back button action to return to welcome screen
        backButton.setOnAction(e -> {
            root.getChildren().clear();
            WelcomeView welcomeView = new WelcomeView();
            welcomeView.createWelcomeContent(root, primaryStage);
            // Update the title
            primaryStage.setTitle("Welcome to LifeSync");
        });
        
        // Preserve the functionality but use the unified attemptLogin method
        loginButton.setOnAction(e -> {
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showAlert("Please fill in both fields.", Alert.AlertType.WARNING);
            } else {
                // Use the no-parameter version which gets values from the fields
                attemptLogin(primaryStage);
            }
        });
        
        signUpButton.setOnAction(e -> {
            root.getChildren().clear();
            new RegisterView().createRegisterContent(root, primaryStage);
        });

        // Add all elements to the content box
        contentBox.getChildren().addAll(
            loginText,
            new Region() {{ setPrefHeight(10); }},
            usernameField,
            passwordField,
            errorMessage,
            new Region() {{ setPrefHeight(5); }},
            loginButton,
            orSection,
            signUpButton,
            new Region() {{ setPrefHeight(10); }},
            backButton
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

        // Update the title
        primaryStage.setTitle("LifeSync - Login");
        
        // Make VBox dynamically resize based on screen
        Scene scene = primaryStage.getScene();
        if (scene != null) {
            contentBox.maxWidthProperty().bind(scene.widthProperty().multiply(0.5)); // Adjust width to 50% of screen
        }
    }
    
    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Login Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Legacy method for backward compatibility - delegates to the main method
    private boolean attemptLogin(String username, String password) {
        // Set the field values
        usernameField.setText(username);
        passwordField.setText(password);
        
        // Call the main login method with a dummy stage (it won't be used for navigation here)
        attemptLogin(null);
        
        // Return true if the user is logged in
        return UserSession.getInstance().isLoggedIn();
    }
    
    // Main login method that uses the field values
    private void attemptLogin(Stage stage) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please enter both username and password.", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            // Use DbConnection to authenticate
            DbConnection dbConnection = DbConnection.getInstance();
            java.util.Map<String, Object> result = dbConnection.authenticateUser(username, password);
            
            boolean success = (boolean) result.get("success");
            
            if (success) {
                int userId = (int) result.get("userId");
                boolean isAdmin = (boolean) result.get("isAdmin");
                
                // Set the user session
                UserSession.getInstance().startSession(userId, username, isAdmin);
                
                // Show success message
                showAlert("Login successful!", Alert.AlertType.INFORMATION);
                
                // Only navigate if a stage was provided
                if (stage != null) {
                    // Navigate to appropriate dashboard based on user type
                    if (isAdmin) {
                        navigateToAdminDashboard(stage);
                    } else {
                        navigateToUserDashboard(stage);
                    }
                }
            } else {
                showAlert("Invalid username or password.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Error during login: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    // Updated to take a Stage parameter
    private void navigateToAdminDashboard(Stage stage) {
        try {
            // Create a new root container for the DashboardViewAdmin
            StackPane adminDashboardRoot = new StackPane();
            
            // Initialize the DashboardViewAdmin content in the existing stage
            DashboardViewAdmin adminDashboard = new DashboardViewAdmin();
            adminDashboard.createDashboardContent(adminDashboardRoot, stage);
            
            // Replace the current scene's content with the DashboardViewAdmin
            stage.getScene().setRoot(adminDashboardRoot);
        } catch (Exception e) {
            showAlert("Error navigating to admin dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    // Updated to take a Stage parameter
    private void navigateToUserDashboard(Stage stage) {
        try {
            // Create a new root container for the DashboardView
            StackPane dashboardRoot = new StackPane();
            
            // Initialize the DashboardView content in the existing stage
            DashboardView dashboard = new DashboardView();
            dashboard.createDashboardContent(dashboardRoot, stage);
            
            // Replace the current scene's content with the DashboardView
            stage.getScene().setRoot(dashboardRoot);
        } catch (Exception e) {
            showAlert("Error navigating to dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    // Helper method to convert JavaFX Color to rgba string for CSS (same as WelcomeView)
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