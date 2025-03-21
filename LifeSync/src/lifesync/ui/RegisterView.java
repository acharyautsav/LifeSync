package lifesync.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.geometry.Insets;
import javafx.stage.Stage;

public class RegisterView extends Application {

    private TextField usernameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label errorMessage;

    @Override
    public void start(Stage primaryStage) {

        // Root layout
        BorderPane root = new BorderPane();

        // Sidebar (60% width)
        VBox sidebar = new VBox(20);
        sidebar.setAlignment(Pos.CENTER);
        sidebar.setPadding(new javafx.geometry.Insets(20));

        // Apply gradient background to the sidebar
        LinearGradient sidebarGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#F7D1D7")), new Stop(1, Color.web("#736DED")));
        sidebar.setBackground(new Background(new BackgroundFill(sidebarGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Set sidebar width to 60% of screen width
        sidebar.setPrefWidth(0.4 * 800);  // Assuming 800 is the screen width, you can adjust as needed

        // Welcome message and Login section
        Label welcomeLabel = new Label("Create an Account");
        welcomeLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #3F3D56;"  // Dark purple text
        );

        Label loginQuestion = new Label("Already have an account?");
        loginQuestion.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #3F3D56;"  // Dark purple text
        );

        // Login button with hover effect
        Button loginButton = createStyledButton("Log In");
        addButtonHoverEffect(loginButton);
        loginButton.setOnAction(e -> new LoginView().start(primaryStage));

        // Adding elements to the sidebar
        sidebar.getChildren().addAll(welcomeLabel, loginQuestion, loginButton);

        // Right side: Registration Form (40% of the screen)
        VBox registerForm = new VBox(15);
        registerForm.setAlignment(Pos.CENTER);
        registerForm.setPadding(new javafx.geometry.Insets(20));

        // Apply the requested style to the main content
        registerForm.setStyle("-fx-background-color: #EADEF9; -fx-background-radius: 20px;");

        // Registration Header
        Label registerLabel = new Label("Register Here");
        registerLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #3F3D56;"  // Dark purple text
        );

        // Username, Email, Password, Confirm Password fields
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-border-radius: 5px;");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-border-radius: 5px;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-border-radius: 5px;");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-border-radius: 5px;");

        // Register Button with hover effect
        Button registerButton = createStyledButton("Register");
        addButtonHoverEffect(registerButton);

        // Action for Register Button
        registerButton.setOnAction(e -> {
            if (usernameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                showAlert("Please fill in all fields.", Alert.AlertType.WARNING);
            } else if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showAlert("Passwords do not match.", Alert.AlertType.ERROR);
            } else {
                // Here you can replace the print statement with actual registration logic
                showAlert("Registration Successful!", Alert.AlertType.INFORMATION);
                System.out.println("Registration Successful!"); // Replace with actual registration logic
            }
        });

        // Error message label
        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Add fields and register button to the register form
        registerForm.getChildren().addAll(registerLabel, usernameField, emailField, passwordField,
                confirmPasswordField, registerButton, errorMessage);

        // Set up the layout
        root.setLeft(sidebar);
        root.setCenter(registerForm);

        // Create the scene
        Scene scene = new Scene(root, 800, 600); // Ensure a specific window size for the app
        primaryStage.setTitle("LifeSync - Register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create a styled button
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #736DED; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 25px; -fx-text-fill: white;");
        return button;
    }

    // Method to add hover effects to buttons
    private void addButtonHoverEffect(Button button) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #7A50C4; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 25px; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #736DED; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 25px; -fx-text-fill: white;"));
    }

    // Method to show alerts (Error, Warning, Information)
    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.WARNING ? "Warning" : (alertType == Alert.AlertType.ERROR ? "Error" : "Information"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
