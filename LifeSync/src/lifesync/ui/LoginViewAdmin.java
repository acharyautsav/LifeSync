package lifesync.ui;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginViewAdmin extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorMessage;

    @Override
    public void start(Stage primaryStage) {

        // Root layout with cool gradient background
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #F7D1D7, #736DED);");

        // Sidebar (60% width) with soft shadow and gradient background
        VBox sidebar = new VBox(30);
        sidebar.setAlignment(Pos.CENTER);
        sidebar.setPadding(new javafx.geometry.Insets(20));
        
        // Applying the linear gradient to the sidebar
        LinearGradient sidebarGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#F7D1D7")), new Stop(1, Color.web("#736DED")));
        sidebar.setBackground(new Background(new BackgroundFill(sidebarGradient, null, null)));

        // Welcome message with custom font style
        Label welcomeLabel = new Label("Welcome to LifeSync");
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-text-fill: #3F3D56;");

        // Adding elements to the sidebar
        sidebar.getChildren().addAll(welcomeLabel);

        // Right side: Login Form (40% of the screen) with soft background
        VBox loginForm = new VBox(15);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new javafx.geometry.Insets(20));
        loginForm.setStyle("-fx-background-color: #EADEF9; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Login Header
        Label loginLabel = new Label("Login Here");
        loginLabel.setStyle("-fx-font-size: 24px; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-text-fill: #3F3D56;");

        // Username and Password fields with subtle focus effects
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-color: #F3F3F9;");
        usernameField.setOnMouseClicked(e -> usernameField.setStyle("-fx-background-color: #F3F3F9;"));

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-color: #F3F3F9;");
        passwordField.setOnMouseClicked(e -> passwordField.setStyle("-fx-background-color: #F3F3F9;"));

        // Login Button with refined hover effects
        Button loginButton = createStyledButton("Login");
        addButtonHoverEffect(loginButton);

        // Action for login button
        loginButton.setOnAction(e -> {
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showAlert("Please fill in both fields.", Alert.AlertType.WARNING);
            } else {
                boolean loginSuccess = attemptLogin(usernameField.getText(), passwordField.getText());
                if (loginSuccess) {
                    showAlert("Login Successful!", Alert.AlertType.INFORMATION);
                    DashboardView dashboard = new DashboardView();
                    new DashboardView().start(primaryStage);
                    dashboard.start(primaryStage);  // Pass the same stage
                } else {
                    showAlert("Invalid username or password.", Alert.AlertType.ERROR);
                }
            }
        });

        // Error message label (for internal validation)
        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Add fields and login button to the login form
        loginForm.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton, errorMessage);

        // Set up the layout
        root.setLeft(sidebar);
        root.setCenter(loginForm);

        // Create the scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("LifeSync - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Login Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean attemptLogin(String username, String password) {
        return "admin".equals(username) && "password123".equals(password);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #736DED; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 25px; -fx-text-fill: white;");
        return button;
    }

    private void addButtonHoverEffect(Button button) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #7A50C4; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 25px; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #736DED; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 25px; -fx-text-fill: white;"));

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), button);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);

        button.setOnMouseEntered(e -> scaleTransition.play());
        button.setOnMouseExited(e -> {
            scaleTransition.setFromX(1.05);
            scaleTransition.setFromY(1.05);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
