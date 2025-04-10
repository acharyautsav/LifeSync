package lifesync.ui_main;

import java.util.List;

import databasePackage.databaseManager;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
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

public class DashboardViewAdmin extends Application {

    // Soft purple pastel color palette (from WelcomeView)
    private static final Color COLOR_BG_START = Color.web("#F3EEFF");
    private static final Color COLOR_BG_MID = Color.web("#E9DFFF");
    private static final Color COLOR_BG_END = Color.web("#E0D3F5");
    private static final Color COLOR_ACCENT = Color.web("#D0BDF4");
    private static final Color COLOR_TEXT_PRIMARY = Color.web("#4A4063");
    private static final Color COLOR_TEXT_SECONDARY = Color.web("#8E82A6");
    private static final Color COLOR_BUTTON_PRIMARY = Color.web("#D8D0F0");
    private static final Color COLOR_BUTTON_HOVER = Color.web("#C5B6E3");
    private static final Color COLOR_PANEL_BG = Color.web("#FFFFFF");
    private static final Color COLOR_ROW_ALT = Color.web("#F9F7FF");
    private static final Color COLOR_BORDER = Color.web("#E6E0F0");

    @Override
    public void start(Stage primaryStage) {
        // Root layout should be BorderPane
        BorderPane root = new BorderPane();
        root.setPrefSize(1000, 700);

        // Background setup
        StackPane backgroundPane = createBackground();
        
        // Main content area
        VBox mainContent = createMainContent();
        backgroundPane.getChildren().add(mainContent);
        
        // Set backgroundPane as the center of BorderPane
        root.setCenter(backgroundPane);

        // Sidebar
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // Scene setup
        Scene scene = new Scene(root);
        primaryStage.setTitle("Admin Panel - LifeSync");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    public void createDashboardContent(StackPane root, Stage primaryStage) {
        // Clear any existing content
        root.getChildren().clear();
        
        // Create the main BorderPane layout
        BorderPane borderPane = new BorderPane();
        
        // Background setup
        StackPane backgroundPane = createBackground();
        borderPane.setCenter(backgroundPane);

        // Sidebar
        VBox sidebar = createSidebar(primaryStage);
        borderPane.setLeft(sidebar);

        // Main content area
        VBox mainContent = createMainContent();
        backgroundPane.getChildren().add(mainContent);

        // Add the borderPane to the root
        root.getChildren().add(borderPane);
        
        // Make the dashboard responsive
        Scene scene = primaryStage.getScene();
        if (scene != null) {
            borderPane.prefWidthProperty().bind(scene.widthProperty());
            borderPane.prefHeightProperty().bind(scene.heightProperty());
        }
    }
    
    
    private StackPane createBackground() {
        StackPane backgroundPane = new StackPane();

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, COLOR_BG_START),
                new Stop(0.5, COLOR_BG_MID),
                new Stop(1, COLOR_BG_END)
        );
        backgroundPane.setBackground(new Background(new BackgroundFill(gradient, null, null)));

        addBackgroundShapes(backgroundPane); // Use WelcomeView background shapes

        return backgroundPane;
    }

    private VBox createSidebar(Stage primaryStage) {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setPrefWidth(250);
        sidebar.setBackground(new Background(new BackgroundFill(COLOR_PANEL_BG, CornerRadii.EMPTY, Insets.EMPTY))); // White panel

        Text title = new Text("Admin Panel");
        title.setFont(Font.font("Segoe UI",  20));
        title.setFill(COLOR_TEXT_PRIMARY);

        Button manageUsersButton = createStyledButton("Manage Users");
        Button logoutButton = createStyledButton("Logout");

        manageUsersButton.setOnAction(e -> showManageUsersView(primaryStage));
        logoutButton.setOnAction(e -> {
            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            
            // Create a new root container for the WelcomeView
            StackPane welcomeRoot = new StackPane();
            
            // Re-initialize the WelcomeView content in the existing stage
            new WelcomeView().createWelcomeContent(welcomeRoot, stage);
            
            // Replace the current scene's content with the WelcomeView
            stage.getScene().setRoot(welcomeRoot);
        });

        sidebar.getChildren().addAll(title, manageUsersButton, logoutButton);

        return sidebar;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);

        Text headerLabel = new Text("Welcome to the Admin Panel");
        headerLabel.setFont(Font.font("Segoe UI",  28));
        headerLabel.setFill(COLOR_TEXT_PRIMARY);

        mainContent.getChildren().add(headerLabel);

        return mainContent;
    }

    // Styled button with WelcomeView style
    private Button createStyledButton(String text) {
        Button button = new Button(text);

        String buttonStyle = String.format(
                "-fx-background-color: %s; " +
                        "-fx-font-size: 15px; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-weight: normal; " +
                        "-fx-padding: 10px; " +
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
                        "-fx-padding: 10px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-text-fill: %s; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 6, 0, 0, 2);",
                toRgbaString(COLOR_BUTTON_HOVER),
                toRgbaString(COLOR_TEXT_PRIMARY)
        );

        button.setStyle(buttonStyle);
        button.setOnMouseEntered(e -> button.setStyle(buttonHoverStyle));
        button.setOnMouseExited(e -> button.setStyle(buttonStyle));

        return button;
    }

    // Helper method to convert JavaFX Color to rgba string for CSS (from WelcomeView)
    private String toRgbaString(Color color) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                color.getOpacity());
    }

    private void showManageUsersView(Stage primaryStage) {
        // Get the root node from the scene
        Parent root = primaryStage.getScene().getRoot();
        
        // Find the background StackPane in the hierarchy
        StackPane backgroundPane;
        
        if (root instanceof BorderPane) {
            // If using the start() method structure
            BorderPane borderPane = (BorderPane) root;
            backgroundPane = (StackPane) borderPane.getCenter();
        } else if (root instanceof StackPane) {
            // If using the createDashboardContent() method structure
            StackPane mainRoot = (StackPane) root;
            BorderPane borderPane = (BorderPane) mainRoot.getChildren().get(0);
            backgroundPane = (StackPane) borderPane.getCenter();
        } else {
            // Fallback - create new structure if needed
            backgroundPane = new StackPane();
            primaryStage.getScene().setRoot(new BorderPane(backgroundPane));
        }
        
        // Clear any previous content from the background pane except for the background shapes
        while (backgroundPane.getChildren().size() > 6) { // Assuming first 6 are background shapes
            backgroundPane.getChildren().remove(backgroundPane.getChildren().size() - 1);
        }
        
        // Create a card-like container for the user management view
        VBox userManagementCard = new VBox(20);
        userManagementCard.setPadding(new Insets(30));
        userManagementCard.setMaxWidth(800);
        userManagementCard.setMaxHeight(600);
        userManagementCard.setAlignment(Pos.TOP_CENTER);
        
        // Add a white background with rounded corners and shadow for the card
        userManagementCard.setBackground(new Background(new BackgroundFill(
                COLOR_PANEL_BG, new CornerRadii(12), Insets.EMPTY)));
        userManagementCard.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.2)));
        
        // Create a header with title and description
        Text headerLabel = new Text("User Management");
        headerLabel.setFont(Font.font("Segoe UI",  24));
        headerLabel.setFill(COLOR_TEXT_PRIMARY);
        
        Text subHeaderLabel = new Text("View and manage user accounts");
        subHeaderLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subHeaderLabel.setFill(COLOR_TEXT_SECONDARY);
        
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(headerLabel, subHeaderLabel);
        
        // Create a container for the user table
        VBox tableContainer = new VBox(0);
        tableContainer.setStyle("-fx-border-color: " + toRgbaString(COLOR_BORDER) + ";" +
                               "-fx-border-width: 1px;" +
                               "-fx-border-radius: 8px;");
        
        // Create table header
        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(12, 15, 12, 15));
        tableHeader.setStyle("-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + ";" +
                            "-fx-background-radius: 8px 8px 0 0;");
        
        Label usernameHeader = createTableHeaderLabel("Username", 150);
        Label emailHeader = createTableHeaderLabel("Email", 200);
        Label passwordHeader = createTableHeaderLabel("Password", 150);
        Label actionsHeader = createTableHeaderLabel("Actions", 100);
        
        tableHeader.getChildren().addAll(usernameHeader, emailHeader, passwordHeader, actionsHeader);
        
        // Fetch users from the database
        List<databaseManager.User> users = databaseManager.getAllUsers();
        
        // Create the table content
        VBox tableContent = new VBox(0);
        
        for (int i = 0; i < users.size(); i++) {
            databaseManager.User user = users.get(i);
            HBox userRow = createUserTableRow(user, i % 2 == 0);
            tableContent.getChildren().add(userRow);
        }
        
        // Add a scroll pane for the table content
        ScrollPane scrollPane = new ScrollPane(tableContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0));
        
        // Assemble the table
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        
        // Add everything to the card
        userManagementCard.getChildren().addAll(headerBox, tableContainer);
        
        // Center the card in the background pane
        StackPane.setAlignment(userManagementCard, Pos.CENTER);
        backgroundPane.getChildren().add(userManagementCard);
    }
    
    private Label createTableHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        label.setTextFill(COLOR_TEXT_PRIMARY);
        label.setPrefWidth(width);
        return label;
    }
    
    private HBox createUserTableRow(databaseManager.User user, boolean isAlternate) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 15, 12, 15));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Set alternating row background for better readability
        if (isAlternate) {
            row.setBackground(new Background(new BackgroundFill(COLOR_ROW_ALT, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            row.setBackground(new Background(new BackgroundFill(COLOR_PANEL_BG, CornerRadii.EMPTY, Insets.EMPTY)));
        }
        
        // Username field
        TextField usernameField = new TextField(user.getUsername());
        usernameField.setPrefWidth(150);
        styleTextField(usernameField);
        
        // Email field
        TextField emailField = new TextField(user.getEmail());
        emailField.setPrefWidth(200);
        styleTextField(emailField);
        
        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New password");
        passwordField.setPrefWidth(150);
        styleTextField(passwordField);
        
        // Create a container for buttons
        HBox buttonContainer = new HBox(5);
        buttonContainer.setPrefWidth(150);
        
        // Update button
        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(70);
        styleActionButton(updateButton);
        
        // Delete button
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(70);
        styleActionButton(deleteButton);
        deleteButton.setStyle(
            "-fx-background-color: #FFD6D6;" +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 5px 10px;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;"
        );
        
        deleteButton.setOnMouseEntered(e -> 
            deleteButton.setStyle(
                "-fx-background-color: #FFB7B7;" +
                "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 5px 10px;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            )
        );
        
        deleteButton.setOnMouseExited(e -> 
            deleteButton.setStyle(
                "-fx-background-color: #FFD6D6;" +
                "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 5px 10px;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            )
        );
        
        updateButton.setOnAction(e -> {
            // Update user information in the database
            String newUsername = usernameField.getText();
            String newEmail = emailField.getText();
            String newPassword = passwordField.getText();

            boolean success = databaseManager.updateUser(user.getId(), newUsername, newEmail, newPassword);
            if (success) {
                showAlert("User updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Failed to update user. Please try again.", Alert.AlertType.ERROR);
            }
        });
        
        deleteButton.setOnAction(e -> {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete User");
            confirmAlert.setContentText("Are you sure you want to delete user " + user.getUsername() + "?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    // Delete user from database
                    boolean success = databaseManager.deleteUser(user.getId());
                    if (success) {
                        showAlert("User deleted successfully!", Alert.AlertType.INFORMATION);
                        
                        // Refresh the user list
                        Stage stage = (Stage) deleteButton.getScene().getWindow();
                        showManageUsersView(stage);
                    } else {
                        showAlert("Failed to delete user. Please try again.", Alert.AlertType.ERROR);
                    }
                }
            });
        });
        
        buttonContainer.getChildren().addAll(updateButton, deleteButton);
        
        row.getChildren().addAll(usernameField, emailField, passwordField, buttonContainer);
        
        return row;
    }
    
    private void styleTextField(TextField textField) {
        textField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + toRgbaString(COLOR_BORDER) + ";" +
            "-fx-border-radius: 4px;" +
            "-fx-padding: 5px;" +
            "-fx-font-size: 13px;" +
            "-fx-margin: 0 5px 0 0;"
        );
        textField.setMaxHeight(30);
        HBox.setMargin(textField, new Insets(0, 10, 0, 0));
    }
    
    private void styleActionButton(Button button) {
        button.setStyle(
            "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + ";" +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 5px 10px;" +
            "-fx-background-radius: 4px;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: " + toRgbaString(COLOR_BUTTON_HOVER) + ";" +
                "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 5px 10px;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + ";" +
                "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 5px 10px;" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            )
        );
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.WARNING ? "Warning" : (alertType == Alert.AlertType.ERROR ? "Error" : "Information"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add decorative shapes to the background (from WelcomeView)
    private void addBackgroundShapes(StackPane root) {
        // Add soft, blurred circles in the background
        for (int i = 0; i < 6; i++) {
            Circle circle = new Circle(Math.random() * 120 + 40);
            circle.setFill(i % 2 == 0 ? COLOR_BG_START.deriveColor(0, 1, 1, 0.4) : COLOR_BG_END.deriveColor(0, 1, 1, 0.2));
            circle.setEffect(new GaussianBlur(40));

            // Position circles randomly
            double x = Math.random() * 1000 - 500;
            double y = Math.random() * 800 - 400;

            StackPane.setMargin(circle, new Insets(y, 0, 0, x));
            root.getChildren().add(circle);

            // Add subtle animation to circles
            TranslateTransition tt = new TranslateTransition(Duration.seconds(20 + Math.random() * 30), circle);
            tt.setByX(30);
            tt.setByY(20);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.setAutoReverse(true);
            tt.play();
        }

        // Add a few larger, more transparent shapes for depth
        for (int i = 0; i < 3; i++) {
            Circle largeCircle = new Circle(Math.random() * 200 + 100);
            largeCircle.setFill(COLOR_BG_END.deriveColor(0, 1, 1, 0.15));
            largeCircle.setEffect(new GaussianBlur(60));

            // Position circles randomly
            double x = Math.random() * 1200 - 600;
            double y = Math.random() * 1000 - 500;

            StackPane.setMargin(largeCircle, new Insets(y, 0, 0, x));
            root.getChildren().add(largeCircle);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}