package lifesync.ui_main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import databasePackage.moodDatabase;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MoodTrackerView extends Application {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lifesync";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "45683968";

    // Soft purple pastel color palette
    private static final Color COLOR_BG_START = Color.web("#F3EEFF");  // Very light lavender
    private static final Color COLOR_BG_MID = Color.web("#E9DFFF");    // Light lavender
    private static final Color COLOR_BG_END = Color.web("#E0D3F5");    // Slightly deeper lavender
    
    private static final Color COLOR_ACCENT = Color.web("#D0BDF4");    // Soft lavender
    private static final Color COLOR_TEXT_PRIMARY = Color.web("#4A4063"); // Dark purple-gray
    private static final Color COLOR_TEXT_SECONDARY = Color.web("#8E82A6"); // Medium purple-gray
    
    private static final Color COLOR_BUTTON_PRIMARY = Color.web("#D8D0F0"); // Light lavender
    private static final Color COLOR_BUTTON_SECONDARY = Color.web("#E2DCF7"); // Lighter lavender
    private static final Color COLOR_BUTTON_ACCENT = Color.web("#C5B6E3"); // Slightly darker lavender
    
    private static final Color COLOR_PANEL_BG = Color.web("#FFFFFF");  // White panel background
    private static final Color COLOR_SIDEBAR_BG = Color.web("#F3EEFF", 0.9); // Semi-transparent light lavender
    
    // Mood-specific colors
    private static final Color COLOR_HAPPY = Color.web("#A5D6A7", 0.8);   // Soft green
    private static final Color COLOR_SAD = Color.web("#90CAF9", 0.8);     // Soft blue
    private static final Color COLOR_NEUTRAL = Color.web("#E0E0E0", 0.8); // Soft gray
    private static final Color COLOR_ANGRY = Color.web("#FFAB91", 0.8);   // Soft orange
    private static final Color COLOR_RELAXED = Color.web("#CE93D8", 0.8); // Soft purple

    private String selectedMood;
    private Map<String, LocalDate> recordedMoods;
    private Label selectedMoodLabel;
    private Button selectedMoodButton = null;

    private Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    public void start(Stage primaryStage) {
    	ensureTableExists();
        recordedMoods = new HashMap<>(); // Initialize the map
        selectedMood = "";
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
        primaryStage.setTitle("LifeSync - Mood Tracker");
        primaryStage.setScene(scene);
        
        // Maximize the window but keep the taskbar visible
        primaryStage.setMaximized(true);

        primaryStage.show();
        
        // Ensure database table exists
        ensureTableExists();
    }

    
    private void switchToDashboardView(Stage primaryStage) {
        // Clear the existing content
        BorderPane root = (BorderPane) ((StackPane) primaryStage.getScene().getRoot()).getChildren().get(1);
        
        // Create the DashboardView content
        DashboardView dashboardView = new DashboardView();
        
        // Create a new BorderPane for the dashboard
        BorderPane dashboardRoot = new BorderPane();
        dashboardRoot.setPrefSize(1000, 700);
        
        // Create background
        StackPane backgroundPane = createBackground();
        
        // Create sidebar and main content from DashboardView
        VBox sidebar = dashboardView.createSidebar(primaryStage);
        VBox mainContent = dashboardView.createMainContent(primaryStage);
        
        // Set up the layout
        dashboardRoot.setLeft(sidebar);
        dashboardRoot.setCenter(mainContent);
        
        // Replace the content in the scene
        StackPane sceneRoot = new StackPane(backgroundPane, dashboardRoot);
        primaryStage.getScene().setRoot(sceneRoot);
        
        // Ensure table exists - now this will work
    }
    
    
    
    
 // In MoodTrackerView.java
    private void switchToFinanceTrackerView(Stage primaryStage) {
        // Get the root StackPane from the current scene
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        
        // Clear the existing content
        root.getChildren().clear();
        
        // Create the FinanceTrackerView content
        FinanceTrackerView financeTrackerView = new FinanceTrackerView();
        
        // Create background
        StackPane backgroundPane = financeTrackerView.createBackground();
        
        // Create main content
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);
        
        // Create sidebar and main content from FinanceTrackerView
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
    
    
 // In MoodTrackerView.java
    private void switchToSleepTrackerView(Stage primaryStage) {
        // Get the root StackPane from the current scene
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        
        // Clear the existing content
        root.getChildren().clear();
        
        // Create the SleepTrackerView content
        SleepTrackerView sleepTrackerView = new SleepTrackerView();
        
        // Create background
        StackPane backgroundPane = sleepTrackerView.createBackground();
        
        // Create main content
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);
        
        // Create sidebar and main content from SleepTrackerView
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
    
    
    private void switchToMyProfileView(Stage primaryStage) {
        // Get the root StackPane from the current scene
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        
        // Clear the existing content
        root.getChildren().clear();
        
        // Create the MyProfileView content
        MyProfileView profileView = new MyProfileView();
        
        // Create background
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
    
    public StackPane createBackground() {
        StackPane background = new StackPane();
        
        // Create a gradient background
        LinearGradient enhancedGradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, COLOR_BG_START),
            new Stop(0.5, COLOR_BG_MID),
            new Stop(1, COLOR_BG_END)
        );
        background.setBackground(new javafx.scene.layout.Background(
            new javafx.scene.layout.BackgroundFill(enhancedGradient, null, null)));
        
        // Add decorative circles
        addBackgroundShapes(background);
        
        return background;
    }
    
    private void addBackgroundShapes(StackPane root) {
        // Add soft, blurred circles in the background
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
        
        // Add a few larger, more transparent shapes for depth
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
    
    public VBox createSidebar(Stage primaryStage) {
        // Sidebar with soft purple styling
        VBox sidebar = new VBox(25);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(220);
        sidebar.setAlignment(Pos.TOP_CENTER);
        
        // Add a glass-like effect to sidebar
        sidebar.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_SIDEBAR_BG, new javafx.scene.layout.CornerRadii(0, 20, 20, 0, false), null)));
        sidebar.setEffect(new DropShadow(10, Color.web("#00000020")));
        
        // App title/logo
        Text appTitle = new Text("LifeSync");
        appTitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        appTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Subtitle
        Text appSubtitle = new Text("Your Life, Organized");
        appSubtitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        appSubtitle.setFill(COLOR_TEXT_SECONDARY);
        
        VBox titleBox = new VBox(5, appTitle, appSubtitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 20, 0));
        
        // Navigation buttons
        Button homeButton = createSidebarButton("🏠  Home", false);
        Button moodButton = createSidebarButton("😊  Mood", true);
        Button sleepButton = createSidebarButton("🛏️  Sleep", false);
        Button financeButton = createSidebarButton("💰  Finance", false);
        Button profileButton = createSidebarButton("👤  My Profile", false);
        
        // Set button actions
        homeButton.setOnAction(e -> switchToDashboardView(primaryStage));
        moodButton.setOnAction(e -> new MoodTrackerView().start(primaryStage));
        sleepButton.setOnAction(e -> switchToSleepTrackerView(primaryStage));
        financeButton.setOnAction(e -> switchToFinanceTrackerView(primaryStage));
        profileButton.setOnAction(e -> switchToMyProfileView(primaryStage));
        
        
        // Add spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Profile button at bottom
        
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
        // Main content area with white background and rounded corners
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(20), null)));
        mainContent.setEffect(new DropShadow(15, Color.web("#00000015")));
        BorderPane.setMargin(mainContent, new Insets(20));
        
        // Header with title and past moods button
        HBox header = createHeader(primaryStage);
        
        // Mood selection section
        VBox moodSelectionSection = createMoodSelectionSection();
        
        // Add all sections to main content
        mainContent.getChildren().addAll(header, moodSelectionSection);
        
        return mainContent;
    }
    
    private HBox createHeader(Stage primaryStage) {
        // Page title
        Text pageTitle = new Text("Mood Tracker");
        pageTitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 32));
        pageTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Today's date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        Text dateText = new Text(today.format(formatter));
        dateText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        dateText.setFill(COLOR_TEXT_SECONDARY);
        
        VBox titleBox = new VBox(5, pageTitle, dateText);
        
        // Past moods button
        Button pastMoodsButton = createActionButton("View Past Moods");
        pastMoodsButton.setOnAction(e -> showPastMoods(primaryStage));
        
        // Create header layout
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleBox, spacer, pastMoodsButton);
        
        return header;
    }
    
    private VBox createMoodSelectionSection() {
        // Section title
        Text sectionTitle = new Text("How are you feeling today?");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 22));
        sectionTitle.setFill(COLOR_TEXT_PRIMARY);
        sectionTitle.setTextAlignment(TextAlignment.CENTER);
        
        // Mood buttons
        VBox moodButtons = createMoodButtons();
        
        // Selected mood display
        selectedMoodLabel = new Label("Select a mood to record");
        selectedMoodLabel.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 16));
        selectedMoodLabel.setTextFill(COLOR_TEXT_SECONDARY);
        selectedMoodLabel.setAlignment(Pos.CENTER);
        
        // Save mood button
        Button saveMoodButton = createActionButton("Save Mood");
        saveMoodButton.setOnAction(e -> saveMood());
        saveMoodButton.setPrefWidth(180);
        
        // Create section layout
        VBox section = new VBox(25);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20, 0, 30, 0));
        section.getChildren().addAll(sectionTitle, moodButtons, selectedMoodLabel, saveMoodButton);
        
        return section;
    }
    
    private VBox createMoodButtons() {
        VBox moodButtonsContainer = new VBox(20);
        moodButtonsContainer.setAlignment(Pos.CENTER);
        
        // Create mood buttons with emojis and labels
        Button happyButton = createMoodButton("😊  Happy", COLOR_HAPPY);
        Button sadButton = createMoodButton("😔  Sad", COLOR_SAD);
        Button neutralButton = createMoodButton("😐  Neutral", COLOR_NEUTRAL);
        Button angryButton = createMoodButton("😡  Angry", COLOR_ANGRY);
        Button relaxedButton = createMoodButton("😌  Relaxed", COLOR_RELAXED);
        
        // Set actions for mood buttons
        happyButton.setOnAction(e -> selectMood("Happy", happyButton));
        sadButton.setOnAction(e -> selectMood("Sad", sadButton));
        neutralButton.setOnAction(e -> selectMood("Neutral", neutralButton));
        angryButton.setOnAction(e -> selectMood("Angry", angryButton));
        relaxedButton.setOnAction(e -> selectMood("Relaxed", relaxedButton));
        
        // Organize buttons in rows
        HBox firstRow = new HBox(20, happyButton, sadButton);
        firstRow.setAlignment(Pos.CENTER);
        
        HBox secondRow = new HBox(20, neutralButton, angryButton);
        secondRow.setAlignment(Pos.CENTER);
        
        HBox thirdRow = new HBox(20, relaxedButton);
        thirdRow.setAlignment(Pos.CENTER);
        
        // Add rows to container
        moodButtonsContainer.getChildren().addAll(firstRow, secondRow, thirdRow);
        
        return moodButtonsContainer;
    }
    
    private Button createMoodButton(String text, Color baseColor) {
        Button button = new Button(text);
        
        // Set base style
        button.setStyle(
            "-fx-background-color: " + toRgbaString(baseColor) + "; " +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: normal; " +
            "-fx-padding: 15px 25px; " +
            "-fx-background-radius: 15px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
        );
        
        button.setPrefWidth(160);
        
        // Add hover effect
        Color hoverColor = baseColor.darker();
        
        button.setOnMouseEntered(e -> {
            if (button != selectedMoodButton) {
                button.setStyle(
                    "-fx-background-color: " + toRgbaString(hoverColor) + "; " +
                    "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16px; " +
                    "-fx-font-weight: normal; " +
                    "-fx-padding: 15px 25px; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 6, 0, 0, 2);"
                );
            }
        });
        
        button.setOnMouseExited(e -> {
            if (button != selectedMoodButton) {
                button.setStyle(
                    "-fx-background-color: " + toRgbaString(baseColor) + "; " +
                    "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16px; " +
                    "-fx-font-weight: normal; " +
                    "-fx-padding: 15px 25px; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
                );
            }
        });
        
        // Add scale animation
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        
        button.setOnMouseEntered(e -> {
            if (button != selectedMoodButton) {
                button.setStyle(
                    "-fx-background-color: " + toRgbaString(hoverColor) + "; " +
                    "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16px; " +
                    "-fx-font-weight: normal; " +
                    "-fx-padding: 15px 25px; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 6, 0, 0, 2);"
                );
                scaleIn.play();
            }
        });
        
        button.setOnMouseExited(e -> {
            if (button != selectedMoodButton) {
                button.setStyle(
                    "-fx-background-color: " + toRgbaString(baseColor) + "; " +
                    "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16px; " +
                    "-fx-font-weight: normal; " +
                    "-fx-padding: 15px 25px; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
                );
                scaleOut.play();
            }
        });
        
        return button;
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
            
            // Add hover effect
            button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + toRgbaString(COLOR_BUTTON_SECONDARY) + "; " +
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
        
        button.setPrefWidth(180);
        
        return button;
    }
    
    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + "; " +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10px 15px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + toRgbaString(COLOR_BUTTON_ACCENT) + "; " +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10px 15px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 6, 0, 0, 2);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + "; " +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10px 15px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
        ));
        
        return button;
    }
    
    private void selectMood(String mood, Button button) {
        // Reset previously selected button if any
        if (selectedMoodButton != null) {
            // Get the original color based on the mood
            Color originalColor = getMoodColor(selectedMoodButton.getText().trim().split("\\s+")[1]);
            
            selectedMoodButton.setStyle(
                "-fx-background-color: " + toRgbaString(originalColor) + "; " +
                "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: normal; " +
                "-fx-padding: 15px 25px; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
            );
        }
        
        // Set the new selected mood and button
        selectedMood = mood;
        selectedMoodButton = button;
        
        // Get the color for the selected mood
        Color moodColor = getMoodColor(mood);
        Color darkerMoodColor = moodColor.darker();
        
        // Update the button style to show it's selected
        button.setStyle(
            "-fx-background-color: " + toRgbaString(darkerMoodColor) + "; " +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15px 25px; " +
            "-fx-background-radius: 15px; " +
            "-fx-border-color: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 15px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 6, 0, 0, 2);"
        );
        
        // Update the selected mood label
        selectedMoodLabel.setText("You selected: " + selectedMood);
        
        // Add a scale animation for feedback
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);
        scaleUp.play();
    }
    
    private Color getMoodColor(String mood) {
        switch (mood) {
            case "Happy": return COLOR_HAPPY;
            case "Sad": return COLOR_SAD;
            case "Neutral": return COLOR_NEUTRAL;
            case "Angry": return COLOR_ANGRY;
            case "Relaxed": return COLOR_RELAXED;
            default: return COLOR_BUTTON_PRIMARY;
        }
    }
    
    private void saveMood() {
        if (!selectedMood.isEmpty()) {
            boolean success = moodDatabase.saveMood(selectedMood);
            
            if (success) {
                // Show success message
                selectedMoodLabel.setText("Mood saved: " + selectedMood);
                selectedMoodLabel.setTextFill(COLOR_HAPPY.darker());
                
                // Add a fade animation for feedback
                FadeTransition fade = new FadeTransition(Duration.millis(300), selectedMoodLabel);
                fade.setFromValue(0.5);
                fade.setToValue(1.0);
                fade.setCycleCount(2);
                fade.setAutoReverse(true);
                fade.play();
            } else {
                selectedMoodLabel.setText("Failed to save mood");
                selectedMoodLabel.setTextFill(COLOR_ANGRY.darker());
            }
        } else {
            // No mood selected
            selectedMoodLabel.setText("Please select a mood first");
            selectedMoodLabel.setTextFill(COLOR_TEXT_SECONDARY);
        }
    }
    
    public void ensureTableExists() {
        moodDatabase.ensureTableExists();
    }
    
 // Update the showPastMoods method to use the additional fields from moodDatabase
    private void showPastMoods(Stage primaryStage) {
        // Create a new stage for past moods
        Stage pastMoodsStage = new Stage();
        
        // Create the main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, null, null)));
        
        // Title
        Text title = new Text("Your Mood History");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 24));
        title.setFill(COLOR_TEXT_PRIMARY);
        
        // Container for past moods
        VBox moodsContainer = new VBox(10);
        moodsContainer.setPadding(new Insets(15));
        moodsContainer.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_BG_START, new javafx.scene.layout.CornerRadii(10), null)));
        
        // Retrieve moods from database
        List<Map<String, Object>> moodHistory = moodDatabase.getMoodHistory();
        
        if (!moodHistory.isEmpty()) {
            for (Map<String, Object> moodRecord : moodHistory) {
                String moodDetail = (String) moodRecord.get("moodDetails");
                LocalDate moodDate = (LocalDate) moodRecord.get("recordedDate");
                Integer moodLevel = (Integer) moodRecord.get("moodLevel");
                String notes = (String) moodRecord.get("notes");
                
                // Format the date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
                String formattedDate = moodDate.format(formatter);
                
                // Create a styled mood item
                HBox moodItem = new HBox(15);
                moodItem.setPadding(new Insets(12, 15, 12, 15));
                moodItem.setAlignment(Pos.CENTER_LEFT);
                
                // Set background color based on mood level - make it moderately subtle
                Color moodColor = getMoodColorByLevel(moodLevel);
                // Make the background color moderately subtle
                Color bgColor = moodColor.deriveColor(0, 0.8, 1.0, 0.3); // Increased opacity to 30%
                Color borderColor = moodColor.deriveColor(0, 0.9, 0.9, 0.5); // Increased opacity to 50%
                
                moodItem.setStyle(
                    "-fx-background-color: " + toRgbaString(bgColor) + "; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: " + toRgbaString(borderColor) + "; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-width: 1px;"
                );
                
                // Emoji based on mood
                Text emoji = new Text(getMoodEmoji(moodDetail));
                emoji.setFont(Font.font("Segoe UI", 20));
                
                // Mood name
                Label moodLabel = new Label(moodDetail);
                moodLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
                moodLabel.setTextFill(COLOR_TEXT_PRIMARY);
                
                // Add spacer
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                // Date
                Label dateLabel = new Label(formattedDate);
                dateLabel.setFont(Font.font("Segoe UI", 12));
                dateLabel.setTextFill(COLOR_TEXT_SECONDARY);
                
                moodItem.getChildren().addAll(emoji, moodLabel, spacer, dateLabel);
                moodsContainer.getChildren().add(moodItem);
                
                // Add notes if available
                if (notes != null && !notes.trim().isEmpty() && !notes.equals("Recorded via MoodTracker")) {
                    Label notesLabel = new Label("Note: " + notes);
                    notesLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
                    notesLabel.setTextFill(COLOR_TEXT_SECONDARY);
                    notesLabel.setPadding(new Insets(0, 0, 0, 40));
                    moodsContainer.getChildren().add(notesLabel);
                }
            }
        } else {
            // No moods found
            Label noMoodsLabel = new Label("No moods recorded yet");
            noMoodsLabel.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
            noMoodsLabel.setTextFill(COLOR_TEXT_SECONDARY);
            noMoodsLabel.setPadding(new Insets(20));
            noMoodsLabel.setAlignment(Pos.CENTER);
            
            moodsContainer.getChildren().add(noMoodsLabel);
        }
        
        // Create a scroll pane for the moods
        ScrollPane scrollPane = new ScrollPane(moodsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent;"
        );
        
        // Close button
        Button closeButton = createActionButton("Close");
        closeButton.setOnAction(e -> pastMoodsStage.close());
        
        // Add all elements to the root
        root.getChildren().addAll(title, scrollPane, closeButton);
        
        // Create and show the scene
        Scene scene = new Scene(root, 500, 500);
        pastMoodsStage.setTitle("Mood History");
        pastMoodsStage.setScene(scene);
        pastMoodsStage.show();
    }

    // Helper method to get color based on mood level
    private Color getMoodColorByLevel(Integer level) {
        if (level == null) {
            return COLOR_NEUTRAL.deriveColor(0, 0.85, 0.95, 1); // Slightly muted neutral
        }
        
        switch (level) {
            case 5: return COLOR_HAPPY.deriveColor(0, 0.8, 0.9, 1); // Slightly muted happy
            case 4: return COLOR_HAPPY.deriveColor(0, 0.7, 0.85, 1); // Moderately muted happy
            case 3: return COLOR_NEUTRAL.deriveColor(0, 0.8, 0.9, 1); // Slightly muted neutral
            case 2: return COLOR_SAD.deriveColor(0, 0.8, 0.9, 1); // Slightly muted sad
            case 1: return COLOR_ANGRY.deriveColor(0, 0.8, 0.9, 1); // Slightly muted angry
            default: return COLOR_NEUTRAL.deriveColor(0, 0.8, 0.9, 1); // Slightly muted neutral
        }
    }
    
    private String getMoodEmoji(String mood) {
        switch (mood) {
            case "Happy": return "😊";
            case "Sad": return "😔";
            case "Neutral": return "😐";
            case "Angry": return "😡";
            case "Relaxed": return "😌";
            default: return "❓";
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