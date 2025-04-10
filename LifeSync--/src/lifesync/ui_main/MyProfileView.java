package lifesync.ui_main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePackage.UserSession;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

public class MyProfileView extends Application {

    // Soft purple pastel color palette (same as DashboardView)
    private static final Color COLOR_BG_START = Color.web("#F3EEFF");
    private static final Color COLOR_BG_MID = Color.web("#E9DFFF");
    private static final Color COLOR_BG_END = Color.web("#E0D3F5");

    private static final Color COLOR_ACCENT = Color.web("#D0BDF4");
    private static final Color COLOR_TEXT_PRIMARY = Color.web("#4A4063");
    private static final Color COLOR_TEXT_SECONDARY = Color.web("#8E82A6");

    private static final Color COLOR_BUTTON_PRIMARY = Color.web("#D8D0F0");
    private static final Color COLOR_BUTTON_SECONDARY = Color.web("#E2DCF7");
    private static final Color COLOR_BUTTON_ACCENT = Color.web("#C5B6E3");

    private static final Color COLOR_PANEL_BG = Color.web("#FFFFFF");
    private static final Color COLOR_SIDEBAR_BG = Color.web("#F3EEFF", 0.9);

    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private Label usernameLabel;
    private Label emailDisplayLabel;

    private String currentUsername;  // Store the current username
    private String currentEmail;     // Store the current email

    @Override
    public void start(Stage primaryStage) {
        // Main container
        BorderPane root = new BorderPane();
        root.setPrefSize(1000, 700);

        // Create a background with gradient and shapes
        StackPane backgroundPane = createBackground();
        
        // Sidebar with soft purple styling
        VBox sidebar = createSidebar(primaryStage);
        
        // Main content area
        VBox mainContent = createMainContent(primaryStage);
        
        // Add components to root
        root.setLeft(sidebar);
        root.setCenter(mainContent);
        
        // Create the scene with the background and root
        StackPane sceneRoot = new StackPane(backgroundPane, root);
        Scene scene = new Scene(sceneRoot);
        
        // Set up the stage
        primaryStage.setTitle("LifeSync - My Profile");
        primaryStage.setScene(scene);
        
        // Maximize the window but keep the taskbar visible
        primaryStage.setMaximized(true);

        primaryStage.show();

        // Load user data after the stage is shown
        loadUserData();
    }


    public StackPane createBackground() {
        StackPane background = new StackPane();

        // Create a gradient background (same as DashboardView)
        LinearGradient enhancedGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, COLOR_BG_START),
                new Stop(0.5, COLOR_BG_MID),
                new Stop(1, COLOR_BG_END)
        );
        background.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundFill(enhancedGradient, null, null)));

        // Add decorative circles (same as DashboardView)
        addBackgroundShapes(background);

        return background;
    }

    
 // In MyProfileView.java
    private void switchToDashboardView(Stage primaryStage) {
        // Get the root BorderPane from the current scene
        BorderPane root = (BorderPane) ((StackPane) primaryStage.getScene().getRoot()).getChildren().get(1);
        
        // Create the dashboard content
        DashboardView dashboardView = new DashboardView();
        VBox dashboardContent = dashboardView.createMainContent(primaryStage);
        
        // Replace only the center content
        root.setCenter(dashboardContent);
        
        // Update the title
        primaryStage.setTitle("LifeSync - Dashboard");
    }


    // Do the same for other switch methods (switchToMoodTrackerView, switchToSleepTrackerView, switchToFinanceTrackerView)

    private void switchToMoodTrackerView(Stage primaryStage) {
        // Clear the existing content
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().clear();
        
        // Create the MoodTrackerView content
        MoodTrackerView moodTrackerView = new MoodTrackerView();
        
        // Create background
        StackPane backgroundPane = moodTrackerView.createBackground();
        
        // Create main content
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);
        
        // Create sidebar and main content
        VBox sidebar = moodTrackerView.createSidebar(primaryStage);
        VBox mainContent = moodTrackerView.createMainContent(primaryStage);
        
        // Set up the layout
        contentRoot.setLeft(sidebar);
        contentRoot.setCenter(mainContent);
        
        // Add the background and content to the root
        root.getChildren().addAll(backgroundPane, contentRoot);
        
        // Update the title
        primaryStage.setTitle("LifeSync - Mood Tracker");
        
        // Ensure table exists
        moodTrackerView.ensureTableExists();
    }

    private void switchToSleepTrackerView(Stage primaryStage) {
        // Clear the existing content
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().clear();
        
        // Create the SleepTrackerView content
        SleepTrackerView sleepTrackerView = new SleepTrackerView();
        
        // Create background
        StackPane backgroundPane = sleepTrackerView.createBackground();
        
        // Create main content
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);
        
        // Create sidebar and main content
        VBox sidebar = sleepTrackerView.createSidebar(primaryStage);
        VBox mainContent = sleepTrackerView.createMainContent(primaryStage);
        
        // Set up the layout
        contentRoot.setLeft(sidebar);
        contentRoot.setCenter(mainContent);
        
        // Add the background and content to the root
        root.getChildren().addAll(backgroundPane, contentRoot);
        
        // Update the title
        primaryStage.setTitle("LifeSync - Sleep Tracker");
        
        // Ensure table exists
        sleepTrackerView.ensureTableExists();
    }

    private void switchToFinanceTrackerView(Stage primaryStage) {
        // Clear the existing content
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().clear();
        
        // Create the FinanceTrackerView content
        FinanceTrackerView financeTrackerView = new FinanceTrackerView();
        
        // Create background
        StackPane backgroundPane = financeTrackerView.createBackground();
        
        // Create main content
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);
        
        // Create sidebar and main content
        VBox sidebar = financeTrackerView.createSidebar(primaryStage);
        VBox mainContent = financeTrackerView.createMainContent(primaryStage);
        
        // Set up the layout
        contentRoot.setLeft(sidebar);
        contentRoot.setCenter(mainContent);
        
        // Add the background and content to the root
        root.getChildren().addAll(backgroundPane, contentRoot);
        
        // Update the title
        primaryStage.setTitle("LifeSync - Finance Tracker");
        
        // Ensure table exists
        financeTrackerView.ensureTableExists();
    }
    
    
    private void reloadProfileView(Stage primaryStage) {
        // Get the root StackPane from the current scene
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        
        // Clear the existing content
        root.getChildren().clear();
        
        // Create a new instance of MyProfileView content
        MyProfileView profileView = new MyProfileView();
        
        // Use the current background
        StackPane backgroundPane = createBackground();
        
        // Create main content
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);
        
        // Create sidebar and main content
        VBox sidebar = profileView.createSidebar(primaryStage);
        VBox mainContent = profileView.createMainContent(primaryStage);
        
        // Set up the layout
        contentRoot.setLeft(sidebar);
        contentRoot.setCenter(mainContent);
        
        // Add the background and content to the root
        root.getChildren().addAll(backgroundPane, contentRoot);
        
        // Update the title
        primaryStage.setTitle("LifeSync - My Profile");
        
        // Load user data
        profileView.loadUserData();
    }
    
    
    public void createDashboardContent(StackPane root, Stage primaryStage) {
        // Create background
        StackPane backgroundPane = createBackground();
        
        // Create main content
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);
        
        // Create sidebar and main content
        VBox sidebar = createSidebar(primaryStage);
        VBox mainContent = createMainContent(primaryStage);
        
        // Set up layout
        contentRoot.setLeft(sidebar);
        contentRoot.setCenter(mainContent);
        
        // Add to root
        root.getChildren().addAll(backgroundPane, contentRoot);
        
        // Update title
        primaryStage.setTitle("LifeSync - My Profile");
        
        // Load user data
        loadUserData();
    }
    
    
    private void addBackgroundShapes(StackPane root) {
        // Add soft, blurred circles in the background (same as DashboardView)
        for (int i = 0; i < 6; i++) {
            Circle circle = new Circle(Math.random() * 120 + 40);
            Color circleColor = i % 2 == 0 ?
                    Color.web("#DFD1FF", 0.4) : // Transparent lavender
                    Color.web("#C9B8F0", 0.2);  // More transparent deeper lavender

            circle.setFill(circleColor);
            circle.setEffect(new javafx.scene.effect.GaussianBlur(40));

            // Position circles randomly
            double x = Math.random() * 1000 - 500;
            double y = Math.random() * 800 - 400;

            StackPane.setMargin(circle, new Insets(y, 0, 0, x));
            root.getChildren().add(circle);
        }

        // Add a few larger, more transparent shapes for depth (same as DashboardView)
        for (int i = 0; i < 3; i++) {
            Circle largeCircle = new Circle(Math.random() * 200 + 100);
            largeCircle.setFill(Color.web("#C9B8F0", 0.15));
            largeCircle.setEffect(new javafx.scene.effect.GaussianBlur(60));

            // Position circles randomly
            double x = Math.random() * 1200 - 600;
            double y = Math.random() * 1000 - 500;

            StackPane.setMargin(largeCircle, new Insets(y, 0, 0, x));
            root.getChildren().add(largeCircle);
        }
    }
    
    
    
    private void switchToMyProfileView(Stage primaryStage) {
        // Clear existing content and reload
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().clear();
        
        createDashboardContent(root, primaryStage);
        loadUserData();
    }

    public VBox createSidebar(Stage primaryStage) {
        // Sidebar with soft purple styling (same as DashboardView)
        VBox sidebar = new VBox(25);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(220);
        sidebar.setAlignment(Pos.TOP_CENTER);

        // Add a glass-like effect to sidebar (same as DashboardView)
        sidebar.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                COLOR_SIDEBAR_BG, new javafx.scene.layout.CornerRadii(0, 20, 20, 0, false), null)));
        sidebar.setEffect(new DropShadow(10, Color.web("#00000020")));

        // App title/logo (same as DashboardView)
        Text appTitle = new Text("LifeSync");
        appTitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        appTitle.setFill(COLOR_TEXT_PRIMARY);

        // Subtitle (same as DashboardView)
        Text appSubtitle = new Text("Your Life, Organized");
        appSubtitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        appSubtitle.setFill(COLOR_TEXT_SECONDARY);

        VBox titleBox = new VBox(5, appTitle, appSubtitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 20, 0));

        // Navigation buttons (same as DashboardView)
        Button homeButton = createSidebarButton("🏠  Home", false);
        Button moodButton = createSidebarButton("😊  Mood", false);
        Button sleepButton = createSidebarButton("🛏️  Sleep", false);
        Button financeButton = createSidebarButton("💰  Finance", false);
        Button profileButton = createSidebarButton("👤  My Profile", false);

        // Set button actions (same as DashboardView)
     // In createSidebar method, update the button actions:
        homeButton.setOnAction(e -> switchToDashboardView(primaryStage));
        moodButton.setOnAction(e -> switchToMoodTrackerView(primaryStage));
        sleepButton.setOnAction(e -> switchToSleepTrackerView(primaryStage));
        financeButton.setOnAction(e -> switchToFinanceTrackerView(primaryStage));
        profileButton.setOnAction(e -> {
            // Get the current stage
            Stage currentStage = (Stage) profileButton.getScene().getWindow();
            // Reload the profile view
            switchToMyProfileView(currentStage);
        });

        // Add spacer (same as DashboardView)
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Add all elements to sidebar
        sidebar.getChildren().addAll(
                titleBox,
                homeButton,
                moodButton,
                sleepButton,
                financeButton,
                spacer,
                profileButton
        );

        return sidebar;
    }

    public VBox createMainContent(Stage primaryStage) {
        // Main content area with white background and rounded corners (same as DashboardView)
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(20), null)));
        mainContent.setEffect(new DropShadow(15, Color.web("#00000015")));
        BorderPane.setMargin(mainContent, new Insets(20));

        // Header with title (same as DashboardView)
        HBox header = createHeader();

        // Profile editing section
        VBox profileSection = createProfileSection();

        // Add all sections to main content
        mainContent.getChildren().addAll(header, profileSection);

        return mainContent;
    }

    private HBox createHeader() {
        // Profile title
        Text profileTitle = new Text("My Profile");
        profileTitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 32));
        profileTitle.setFill(COLOR_TEXT_PRIMARY);

        // Subtitle
        Text subtitleText = new Text("Manage your personal information");
        subtitleText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        subtitleText.setFill(COLOR_TEXT_SECONDARY);

        VBox titleBox = new VBox(5, profileTitle, subtitleText);

        // Create header layout
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, spacer);

        return header;
    }

    private VBox createProfileSection() {
        // Section title
        Text sectionTitle = new Text("Edit Profile Information");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        sectionTitle.setFill(COLOR_TEXT_PRIMARY);

        // Username display
        Label usernameLabelText = new Label("Username:");
        usernameLabelText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        usernameLabelText.setTextFill(COLOR_TEXT_PRIMARY);
        usernameLabel = new Label(); // The label will be populated with data
        usernameLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        usernameLabel.setTextFill(COLOR_TEXT_SECONDARY);

        HBox usernameBox = new HBox(10, usernameLabelText, usernameLabel);
        usernameBox.setAlignment(Pos.CENTER_LEFT);

        // Name input field
        Label nameLabel = new Label("New Username:");
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        nameLabel.setTextFill(COLOR_TEXT_PRIMARY);
        nameField = new TextField();
        nameField.setPromptText("Enter your Username");
        nameField.setStyle(
                "-fx-background-color: #F8F7FC; " +
                        "-fx-border-color: #E2DCF7; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 12px; " +
                        "-fx-font-size: 14px;"
        );

        HBox nameBox = new HBox(20, nameLabel, nameField);
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Email display
        Label emailLabelText = new Label("Email:");
        emailLabelText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        emailLabelText.setTextFill(COLOR_TEXT_PRIMARY);
        emailDisplayLabel = new Label(); // The label will be populated with data
        emailDisplayLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        emailDisplayLabel.setTextFill(COLOR_TEXT_SECONDARY);

        HBox emailDisplayBox = new HBox(10, emailLabelText, emailDisplayLabel);
        emailDisplayBox.setAlignment(Pos.CENTER_LEFT);

        // Email input field
        Label emailLabel = new Label("New Email:");
        emailLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        emailLabel.setTextFill(COLOR_TEXT_PRIMARY);
        emailField = new TextField();
        emailField.setPromptText("Enter your new email");
        emailField.setStyle(
                "-fx-background-color: #F8F7FC; " +
                        "-fx-border-color: #E2DCF7; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 12px; " +
                        "-fx-font-size: 14px;"
        );
        HBox emailbox = new HBox(20, emailLabel, emailField);
        emailbox.setAlignment(Pos.CENTER_LEFT);

        // Password input field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        passwordLabel.setTextFill(COLOR_TEXT_PRIMARY);
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your new password");
        passwordField.setStyle(
                "-fx-background-color: #F8F7FC; " +
                        "-fx-border-color: #E2DCF7; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 12px; " +
                        "-fx-font-size: 14px;"
        );

        HBox passwordBox = new HBox(20, passwordLabel, passwordField);
        passwordBox.setAlignment(Pos.CENTER_LEFT);

     // Save changes button
        Button saveButton = createActionButton("Save Changes");
        saveButton.setOnAction(e -> saveProfileChanges());
        saveButton.setPrefWidth(150);

        // Add logout button
        Button logoutButton = createActionButton("Logout");
        logoutButton.setOnAction(e -> {
            // Redirect to the login page
            new LoginView().start(new Stage());
            // Close the current window
            ((Stage) logoutButton.getScene().getWindow()).close();
        });
        logoutButton.setPrefWidth(150);

        // Create a horizontal box to hold both buttons with some spacing
        HBox buttonBox = new HBox(15, saveButton, logoutButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Create section layout
        VBox section = new VBox(15);
        section.setPadding(new Insets(25));
        section.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(15), null)));
        section.setEffect(new DropShadow(8, Color.web("#00000010")));
        section.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
                COLOR_ACCENT, javafx.scene.layout.BorderStrokeStyle.SOLID,
                new javafx.scene.layout.CornerRadii(15), new javafx.scene.layout.BorderWidths(1))));

        // Add elements to section
        section.getChildren().addAll(sectionTitle, usernameBox, nameBox, emailDisplayBox, emailbox, passwordBox, buttonBox);
        return section;
    }

    private Button createSidebarButton(String text, boolean isActive) {
        Button button = new Button(text);

        if (isActive) {
            button.setStyle(
                    "-fx-background-color: " + toRgbaString(COLOR_BUTTON_ACCENT) + "; " +
                            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                            "-fx-font-family: 'Segoe UI'; " +
                            "-fx-font-size: 15px; " +
                            "-fx-padding: 12px 20px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-alignment: CENTER_LEFT; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
            );
        } else {
            button.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                            "-fx-font-family: 'Segoe UI'; " +
                            "-fx-font-size: 15px; " +
                            "-fx-padding: 12px 20px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-alignment: CENTER_LEFT;"
            );

            button.setOnMouseEntered(e -> button.setStyle(
                    "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + "; " +
                            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                            "-fx-font-family: 'Segoe UI'; " +
                            "-fx-font-size: 15px; " +
                            "-fx-padding: 12px 20px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-alignment: CENTER_LEFT;"
            ));

            button.setOnMouseExited(e -> button.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                            "-fx-font-family: 'Segoe UI'; " +
                            "-fx-font-size: 15px; " +
                            "-fx-padding: 12px 20px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-alignment: CENTER_LEFT;"
            ));
        }

        return button;
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + "; " +
                        "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                        "-fx-font-family: 'Segoe UI'; " +
                         //Removed font weight bold as requested
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + toRgbaString(COLOR_BUTTON_SECONDARY) + "; " +
                        "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        //Removed font weight bold as requested
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + "; " +
                        "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        //Removed font weight bold as requested
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 8px;"
        ));

        return button;
    }

    private String toRgbaString(Color color) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                color.getOpacity());
    }

    void loadUserData() {
        try {
            Connection connection = getConnection();
            // Modified query to use user_id from session
            String query = "SELECT username, email FROM users WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, UserSession.getInstance().getUserId()); // Get current user ID
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                currentUsername = resultSet.getString("username");
                currentEmail = resultSet.getString("email");

                usernameLabel.setText(currentUsername);
                emailDisplayLabel.setText(currentEmail);
            } else {
                usernameLabel.setText("N/A");
                emailDisplayLabel.setText("N/A");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            usernameLabel.setText("Error");
            emailDisplayLabel.setText("Error");
        }
    }

    private void saveProfileChanges() {
        String newUsername = nameField.getText();
        String newEmail = emailField.getText();
        String newPassword = passwordField.getText();

        // Validate at least one field is filled
        if (newUsername.isEmpty() && newEmail.isEmpty() && newPassword.isEmpty()) {
            showAlert("Error", "At least one field must be filled out to update.");
            return;
        }

        // Build dynamic SQL query
        StringBuilder updateSQL = new StringBuilder("UPDATE users SET ");
        List<String> updates = new ArrayList<>();

        if (!newUsername.isEmpty()) updates.add("username = ?");
        if (!newEmail.isEmpty()) updates.add("email = ?");
        if (!newPassword.isEmpty()) updates.add("password = ?");

        updateSQL.append(String.join(", ", updates));
        updateSQL.append(" WHERE user_id = ?");  // Changed to use user_id

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL.toString());
            
            int paramIndex = 1;
            if (!newUsername.isEmpty()) preparedStatement.setString(paramIndex++, newUsername);
            if (!newEmail.isEmpty()) preparedStatement.setString(paramIndex++, newEmail);
            if (!newPassword.isEmpty()) preparedStatement.setString(paramIndex++, newPassword);
            
            // Set user_id from session as last parameter
            preparedStatement.setInt(paramIndex, UserSession.getInstance().getUserId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Update session if username changed
                if (!newUsername.isEmpty()) {
                    UserSession.getInstance().startSession(
                        UserSession.getInstance().getUserId(),
                        newUsername,
                        UserSession.getInstance().isAdmin()
                    );
                }
                
                // Refresh displayed data
                loadUserData();
                showAlert("Success", "Profile updated successfully!");
                
                // Clear password field
                passwordField.clear();
            } else {
                showAlert("Error", "Failed to update profile.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not update profile: " + e.getMessage());
        }
    }

    // Method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Replace with your actual database connection details
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/lifesync";
        String user = "root";
        String password = "45683968";
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
