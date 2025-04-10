package lifesync.ui_main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import databasePackage.DbConnection;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RegisterView {

    private static final Color COLOR_BG_START = Color.web("#F3EEFF");
    private static final Color COLOR_BG_MID = Color.web("#E9DFFF");
    private static final Color COLOR_BG_END = Color.web("#E0D3F5");
    private static final Color COLOR_ACCENT = Color.web("#D0BDF4");
    private static final Color COLOR_TEXT_PRIMARY = Color.web("#4A4063");
    private static final Color COLOR_TEXT_SECONDARY = Color.web("#8E82A6");
    private static final Color COLOR_BUTTON_PRIMARY = Color.web("#D8D0F0");
    private static final Color COLOR_BUTTON_HOVER = Color.web("#C5B6E3");
    
    // Database connection manager
    private DbConnection dbConnection;

    public void createRegisterContent(StackPane root, Stage primaryStage) {
        // Initialize database connection
        dbConnection = DbConnection.getInstance();
        
        root.getChildren().clear();
        
        // Enhanced background gradient
        LinearGradient enhancedGradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, COLOR_BG_START),
            new Stop(0.5, COLOR_BG_MID),
            new Stop(1, COLOR_BG_END)
        );
        root.setBackground(new Background(new BackgroundFill(enhancedGradient, null, null)));
        addBackgroundShapes(root);

        // Main content container
        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(450);
        contentBox.setPadding(new Insets(40));

        // Form panel
        StackPane formPanel = new StackPane(contentBox);
        formPanel.setBackground(new Background(new BackgroundFill(
            Color.WHITE, new CornerRadii(20), null)));
        formPanel.setEffect(new DropShadow(15, Color.web("#C5B6E320")));
        formPanel.setPadding(new Insets(40));
        formPanel.setMaxWidth(500);

        // Header
        Text headerText = new Text("Create an Account");
        headerText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 38));
        headerText.setFill(COLOR_TEXT_PRIMARY);

        // Subtitle
        Text subtitleText = new Text("Join LifeSync and manage your life effortlessly");
        subtitleText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 15));
        subtitleText.setFill(COLOR_TEXT_SECONDARY);

        // Input fields
        TextField usernameField = createStyledTextField("Username");
        TextField emailField = createStyledTextField("Email");
        PasswordField passwordField = createStyledPasswordField("Password");
        PasswordField confirmPasswordField = createStyledPasswordField("Confirm Password");

        // Register button
        Button registerButton = createStyledButton("Register");
        registerButton.setOnMouseEntered(e -> registerButton.setStyle(getButtonHoverStyle()));
        registerButton.setOnMouseExited(e -> registerButton.setStyle(getButtonBaseStyle()));

        registerButton.setOnAction(e -> handleRegistration(
            usernameField.getText(),
            emailField.getText(),
            passwordField.getText(),
            confirmPasswordField.getText(),
            usernameField,
            emailField,
            passwordField,
            confirmPasswordField
        ));

        // Back to login button
        Button backButton = createStyledButton("Back to Login");
        backButton.setOnMouseEntered(e -> backButton.setStyle(getButtonHoverStyle()));
        backButton.setOnMouseExited(e -> backButton.setStyle(getButtonBaseStyle()));
        backButton.setOnAction(e -> {
            root.getChildren().clear();
            new LoginView().createLoginContent(root, primaryStage);
        });

        // Layout
        contentBox.getChildren().addAll(
            headerText,
            subtitleText,
            usernameField,
            emailField,
            passwordField,
            confirmPasswordField,
            registerButton,
            createOrSeparator(),
            backButton
        );

        // Animations
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), formPanel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(1), formPanel);
        scaleIn.setFromX(0.98);
        scaleIn.setFromY(0.98);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        
        fadeIn.play();
        scaleIn.play();

        root.getChildren().add(formPanel);
        primaryStage.setTitle("LifeSync - Register");
    }

    private void handleRegistration(String username, String email, String password, 
                                   String confirmPassword, TextField usernameField,
                                   TextField emailField, PasswordField passwordField,
                                   PasswordField confirmPasswordField) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields.", Alert.AlertType.WARNING);
        } else if (!password.equals(confirmPassword)) {
            showAlert("Passwords do not match.", Alert.AlertType.ERROR);
        } else {
            if (saveUserToDatabase(username, email, password)) {
                showAlert("Registration Successful!", Alert.AlertType.INFORMATION);
                usernameField.clear();
                emailField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
            }
        }
    }

    private HBox createOrSeparator() {
        Text orText = new Text("or");
        orText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        orText.setFill(COLOR_TEXT_SECONDARY);
        
        HBox orSection = new HBox(15, new Separator(), orText, new Separator());
        orSection.setAlignment(Pos.CENTER);
        orSection.setPadding(new Insets(5, 0, 5, 0));
        
        for (int i = 0; i < orSection.getChildren().size(); i++) {
            if (orSection.getChildren().get(i) instanceof Separator) {
                Separator sep = (Separator) orSection.getChildren().get(i);
                sep.setStyle("-fx-background-color: #E2DCF7;");
                sep.setPrefWidth(60);
            }
        }
        return orSection;
    }

    private boolean saveUserToDatabase(String username, String email, String password) {
        // Check if user already exists
        if (dbConnection.userExists(username)) {
            showAlert("Username already exists. Please choose another username.", Alert.AlertType.WARNING);
            return false;
        }
        
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Note: In a real app, you should hash this password
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ User registered successfully: " + username);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                if (ex.getMessage().contains("email")) {
                    showAlert("Email already registered. Please use another email address.", Alert.AlertType.WARNING);
                } else {
                    showAlert("Username already exists. Please choose another username.", Alert.AlertType.WARNING);
                }
            } else {
                showAlert("Registration Error: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
            return false;
        }
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #F3EEFF; -fx-padding: 12px; " +
                      "-fx-font-size: 14px; -fx-background-radius: 8px; " +
                      "-fx-font-family: 'Segoe UI'; -fx-border-color: #D8D0F0;");
        field.setPrefWidth(250);
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #F3EEFF; -fx-padding: 12px; " +
                      "-fx-font-size: 14px; -fx-background-radius: 8px; " +
                      "-fx-font-family: 'Segoe UI'; -fx-border-color: #D8D0F0;");
        field.setPrefWidth(250);
        return field;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(getButtonBaseStyle());
        button.setPrefWidth(250);
        return button;
    }

    private String getButtonBaseStyle() {
        return "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + "; " +
               "-fx-font-size: 15px; -fx-font-family: 'Segoe UI'; " +
               "-fx-padding: 12px 20px; -fx-background-radius: 8px; " +
               "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";";
    }

    private String getButtonHoverStyle() {
        return "-fx-background-color: " + toRgbaString(COLOR_BUTTON_HOVER) + "; " +
               "-fx-font-size: 15px; -fx-font-family: 'Segoe UI'; " +
               "-fx-padding: 12px 20px; -fx-background-radius: 8px; " +
               "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";";
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String toRgbaString(Color color) {
        return String.format("rgba(%d,%d,%d,%.2f)",
            (int)(color.getRed()*255),
            (int)(color.getGreen()*255),
            (int)(color.getBlue()*255),
            color.getOpacity());
    }

    private void addBackgroundShapes(StackPane root) {
        for (int i = 0; i < 6; i++) {
            Circle circle = new Circle(Math.random() * 120 + 40);
            circle.setFill(i%2 == 0 ? COLOR_BG_END : COLOR_BG_MID.deriveColor(0,1,1,0.4));
            circle.setEffect(new GaussianBlur(40));
            
            TranslateTransition tt = new TranslateTransition(
                Duration.seconds(20 + Math.random()*30), circle
            );
            tt.setByX(30);
            tt.setByY(20);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.setAutoReverse(true);
            tt.play();
            
            StackPane.setMargin(circle, new Insets(
                Math.random()*800-400, 
                0, 
                0, 
                Math.random()*1000-500
            ));
            root.getChildren().add(circle);
        }
    }
}