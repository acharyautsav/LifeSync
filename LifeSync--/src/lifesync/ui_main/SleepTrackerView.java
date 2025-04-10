package lifesync.ui_main;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import databasePackage.sleepDatabase;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
import javafx.util.Duration;

public class SleepTrackerView extends Application {

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
    
    private static final Color COLOR_WARNING = Color.web("#FFB7B7");   // Soft red for warnings
    private static final Color COLOR_SUCCESS = Color.web("#B7E1CD");   // Soft green for success

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
        primaryStage.setTitle("LifeSync - Sleep Tracker");
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
        
        // Ensure table exists
    }

 // In SleepTrackerView.java
    private void switchToMoodTrackerView(Stage primaryStage) {
        // Get the root StackPane from the current scene
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        
        // Clear the existing content
        root.getChildren().clear();
        
        // Create the MoodTrackerView content
        MoodTrackerView moodTrackerView = new MoodTrackerView();
        
        // Create background
        StackPane backgroundPane = moodTrackerView.createBackground();
        
        // Create main content
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);
        
        // Create sidebar and main content from MoodTrackerView
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
    
    public void ensureTableExists() {
        sleepDatabase.ensureTableExists();
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
        Button moodButton = createSidebarButton("😊  Mood", false);
        Button sleepButton = createSidebarButton("🛏️  Sleep", true);
        Button financeButton = createSidebarButton("💰  Finance", false);
        Button profileButton = createSidebarButton("👤  My Profile", false);
        
        // Set button actions
        homeButton.setOnAction(e -> switchToDashboardView(primaryStage));
        moodButton.setOnAction(e -> switchToMoodTrackerView(primaryStage));
        sleepButton.setOnAction(e -> new SleepTrackerView().start(primaryStage));
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
        
        // Header with title and sleep history button
        HBox header = createHeader(primaryStage);
        
        // Sleep tracking form
        VBox sleepTrackingForm = createSleepTrackingForm();
        
        // Add all sections to main content
        mainContent.getChildren().addAll(header, sleepTrackingForm);
        
        return mainContent;
    }
    
    private HBox createHeader(Stage primaryStage) {
        // Page title
        Text pageTitle = new Text("Sleep Tracker");
        pageTitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 32));
        pageTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Today's date
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        Text dateText = new Text(now.format(formatter));
        dateText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        dateText.setFill(COLOR_TEXT_SECONDARY);
        
        VBox titleBox = new VBox(5, pageTitle, dateText);
        
        // Sleep history button
        Button sleepHistoryButton = createActionButton("View Sleep History");
        sleepHistoryButton.setOnAction(e -> trackSleepHours(primaryStage));
        
        // Create header layout
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleBox, spacer, sleepHistoryButton);
        
        return header;
    }
    
    private VBox createSleepTrackingForm() {
        // Panel title
        Text panelTitle = new Text("Track Your Sleep");
        panelTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 22));
        panelTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Sleep time input
        Label sleepTimeLabel = new Label("When did you go to sleep?");
        sleepTimeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        sleepTimeLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        TextField sleepTimeInput = new TextField();
        sleepTimeInput.setPromptText("e.g., 10:30 PM");
        sleepTimeInput.setStyle(
            "-fx-background-color: #F8F7FC; " +
            "-fx-border-color: #E2DCF7; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 12px; " +
            "-fx-font-size: 14px;"
        );
        
        Button pickSleepTimeButton = createActionButton("Pick Time");
        pickSleepTimeButton.setOnAction(e -> showTimePicker(null, sleepTimeInput));
        
        HBox sleepTimeBox = new HBox(15, sleepTimeInput, pickSleepTimeButton);
        sleepTimeBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sleepTimeInput, Priority.ALWAYS);
        
        // Wake-up time input
        Label wakeupTimeLabel = new Label("When did you wake up?");
        wakeupTimeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        wakeupTimeLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        TextField wakeupTimeInput = new TextField();
        wakeupTimeInput.setPromptText("e.g., 06:45 AM");
        wakeupTimeInput.setStyle(
            "-fx-background-color: #F8F7FC; " +
            "-fx-border-color: #E2DCF7; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 12px; " +
            "-fx-font-size: 14px;"
        );
        
        Button pickWakeupTimeButton = createActionButton("Pick Time");
        pickWakeupTimeButton.setOnAction(e -> showTimePicker(null, wakeupTimeInput));
        
        HBox wakeupTimeBox = new HBox(15, wakeupTimeInput, pickWakeupTimeButton);
        wakeupTimeBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(wakeupTimeInput, Priority.ALWAYS);
        
        // Sleep duration display
        Label totalSleepLabel = new Label("Total Sleep Duration: --h --m");
        totalSleepLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 16));
        totalSleepLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        // Warning label
        Label warningLabel = new Label("");
        warningLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        warningLabel.setTextFill(COLOR_WARNING.darker());
        
        // Log sleep button
        Button logSleepButton = createActionButton("Log Sleep");
        logSleepButton.setPrefWidth(200);
        logSleepButton.setStyle(
            "-fx-background-color: " + toRgbaString(COLOR_ACCENT) + "; " +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
        );
        
        logSleepButton.setOnAction(e -> {
            String sleepTimeStr = sleepTimeInput.getText();
            String wakeupTimeStr = wakeupTimeInput.getText();
            logSleepDuration(sleepTimeStr, wakeupTimeStr, totalSleepLabel, warningLabel);
        });
        
        // Add hover effect to log button
        logSleepButton.setOnMouseEntered(e -> logSleepButton.setStyle(
            "-fx-background-color: " + toRgbaString(COLOR_ACCENT.darker()) + "; " +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 6, 0, 0, 2);"
        ));
        
        logSleepButton.setOnMouseExited(e -> logSleepButton.setStyle(
            "-fx-background-color: " + toRgbaString(COLOR_ACCENT) + "; " +
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: normal; " +
            "-fx-padding: 12px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
        ));
        
        // Sleep tips
        Text tipsTitle = new Text("Sleep Tips");
        tipsTitle.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 16));
        tipsTitle.setFill(COLOR_TEXT_PRIMARY);
        
        VBox tipsBox = new VBox(8);
        tipsBox.setPadding(new Insets(15));
        tipsBox.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_BG_START, new javafx.scene.layout.CornerRadii(10), null)));
        
        String[] tips = {
            "• Aim for 7-9 hours of sleep each night",
            "• Maintain a consistent sleep schedule",
            "• Create a restful environment (cool, dark, quiet)",
            "• Avoid screens 1 hour before bedtime",
            "• Limit caffeine and alcohol before sleep"
        };
        
        for (String tip : tips) {
            Label tipLabel = new Label(tip);
            tipLabel.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
            tipLabel.setTextFill(COLOR_TEXT_SECONDARY);
            tipsBox.getChildren().add(tipLabel);
        }
        
        // Create two columns for the form
        VBox leftColumn = new VBox(15, 
            sleepTimeLabel, 
            sleepTimeBox, 
            wakeupTimeLabel, 
            wakeupTimeBox, 
            totalSleepLabel, 
            warningLabel, 
            logSleepButton
        );
        leftColumn.setPrefWidth(350);
        
        VBox rightColumn = new VBox(15, tipsTitle, tipsBox);
        rightColumn.setPrefWidth(300);
        
        // Create the form layout
        HBox formLayout = new HBox(30, leftColumn, rightColumn);
        formLayout.setAlignment(Pos.TOP_CENTER);
        
        // Create the panel
        VBox panel = new VBox(20, panelTitle, formLayout);
        panel.setPadding(new Insets(25));
        panel.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(15), null)));
        panel.setEffect(new DropShadow(8, Color.web("#00000010")));
        panel.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
            COLOR_ACCENT, javafx.scene.layout.BorderStrokeStyle.SOLID, 
            new javafx.scene.layout.CornerRadii(15), new javafx.scene.layout.BorderWidths(1))));
        
        return panel;
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
    
    private void showTimePicker(Stage primaryStage, TextField timeInput) {
        // Create a new time picker dialog
        Dialog<LocalTime> timeDialog = new Dialog<>();
        timeDialog.setTitle("Pick Time");
        timeDialog.setHeaderText("Select a time");
        
        // Style the dialog
        timeDialog.getDialogPane().setStyle(
            "-fx-background-color: " + toRgbaString(COLOR_PANEL_BG) + ";" +
            "-fx-padding: 20px;"
        );
        
        // Add OK and Cancel buttons
        ButtonType pickButton = new ButtonType("Pick", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        timeDialog.getDialogPane().getButtonTypes().addAll(pickButton, cancelButton);
        
        // Layout for the time picker
        HBox timePickerBox = new HBox(15);
        timePickerBox.setAlignment(Pos.CENTER);
        timePickerBox.setPadding(new Insets(20, 10, 20, 10));
        
        // Spinner for selecting hours (1-12 for 12-hour format)
        Label hourLabel = new Label("Hour:");
        hourLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        hourLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        Spinner<Integer> hourSpinner = new Spinner<>(1, 12, 12);
        hourSpinner.setEditable(true);
        hourSpinner.setPrefWidth(80);
        hourSpinner.getEditor().setStyle(
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 14px;"
        );
        
        // Spinner for selecting minutes (0-59)
        Label minuteLabel = new Label("Minute:");
        minuteLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        minuteLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        minuteSpinner.setEditable(true);
        minuteSpinner.setPrefWidth(80);
        minuteSpinner.getEditor().setStyle(
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 14px;"
        );
        
        // Spinner for AM/PM selection
        Label amPmLabel = new Label("AM/PM:");
        amPmLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        amPmLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        Spinner<String> amPmSpinner = new Spinner<>();
        SpinnerValueFactory<String> amPmFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(
            FXCollections.observableArrayList("AM", "PM")
        );
        amPmSpinner.setValueFactory(amPmFactory);
        amPmSpinner.setEditable(false);
        amPmSpinner.setPrefWidth(80);
        amPmSpinner.getEditor().setStyle(
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 14px;"
        );
        
        // Add components to the layout
        timePickerBox.getChildren().addAll(
            hourLabel, hourSpinner,
            minuteLabel, minuteSpinner,
            amPmLabel, amPmSpinner
        );
        
        timeDialog.getDialogPane().setContent(timePickerBox);
        
        // Handle result conversion
        timeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == pickButton) {
                int hour = hourSpinner.getValue();
                int minute = minuteSpinner.getValue();
                String amPm = amPmSpinner.getValue();
                
                // Convert to 24-hour format
                if ("PM".equals(amPm) && hour != 12) {
                    hour += 12; // Convert PM to 24-hour format
                } else if ("AM".equals(amPm) && hour == 12) {
                    hour = 0; // Convert 12 AM to 00:00
                }
                
                return LocalTime.of(hour, minute);
            }
            return null;
        });
        
        // Show the dialog and handle selected time
        timeDialog.showAndWait().ifPresent(selectedTime -> {
            // Format and display selected time in 12-hour format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            timeInput.setText(selectedTime.format(formatter));
        });
    }
    
    private void logSleepDuration(String sleepTimeStr, String wakeupTimeStr, Label sleepDurationLabel, Label warningLabel) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime sleepTime = LocalTime.parse(sleepTimeStr, formatter);
            LocalTime wakeupTime = LocalTime.parse(wakeupTimeStr, formatter);
            
            // Calculate minutes between times
            long totalMinutes = ChronoUnit.MINUTES.between(sleepTime, wakeupTime);
            
            // Handle midnight crossover
            if (totalMinutes < 0) {
                totalMinutes += 1440; // Add 24 hours worth of minutes
            }
            
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            
            // Determine if sleep is insufficient
            boolean isWarning = totalMinutes < 480; // Less than 8 hours
            
            // Display the calculated hours
            sleepDurationLabel.setText(String.format("Total Sleep Duration: %02dh %02dm", hours, minutes));
            
            // Add animation for feedback
            FadeTransition fade = new FadeTransition(Duration.millis(300), sleepDurationLabel);
            fade.setFromValue(0.5);
            fade.setToValue(1.0);
            fade.setCycleCount(2);
            fade.setAutoReverse(true);
            fade.play();
            
            // Add this block for database integration
            sleepDatabase sleepDataDAO = new sleepDatabase();
            boolean insertSuccess = sleepDataDAO.insertSleepData(
                sleepTime, 
                wakeupTime, 
                totalMinutes, 
                isWarning
            );
            
            // Show warning if sleep is less than 8 hours
            if (isWarning) {
                warningLabel.setTextFill(COLOR_WARNING.darker());
                warningLabel.setText("⚠️ Your sleep duration is less than the recommended 8 hours");
            } else {
                warningLabel.setTextFill(COLOR_SUCCESS.darker());
                warningLabel.setText("✓ Great job! You've had sufficient sleep");
            }
            
            // Optional: Add a database insertion status message
            if (!insertSuccess) {
                warningLabel.setTextFill(Color.RED);
                warningLabel.setText("⚠️ Failed to log sleep data to database");
            }
            
        } catch (Exception e) {
            sleepDurationLabel.setText("Total Sleep Duration: --h --m");
            warningLabel.setTextFill(COLOR_WARNING.darker());
            warningLabel.setText("⚠️ Invalid time format. Please use the time picker.");
        }
    }
    
    private void trackSleepHours(Stage primaryStage) {
        // Create a new stage for sleep history
        Stage sleepHistoryStage = new Stage();
        
        // Create the main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, null, null)));
        
        // Title
        Text title = new Text("Your Sleep History");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 24));
        title.setFill(COLOR_TEXT_PRIMARY);
        
        // Container for sleep records
        VBox recordsContainer = new VBox(10);
        recordsContainer.setPadding(new Insets(15));
        recordsContainer.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_BG_START, new javafx.scene.layout.CornerRadii(10), null)));
        
        // Retrieve sleep data from database
        List<Map<String, Object>> sleepHistory = sleepDatabase.getSleepHistory(10);
        
        if (!sleepHistory.isEmpty()) {
            for (Map<String, Object> sleepRecord : sleepHistory) {
                // Extract data from the record
                LocalDateTime sleepTime = (LocalDateTime) sleepRecord.get("sleepTime");
                LocalDateTime wakeupTime = (LocalDateTime) sleepRecord.get("wakeTime");
                int sleepHours = (int) sleepRecord.get("sleepHours");
                int sleepMinutes = (int) sleepRecord.get("sleepMinutes");
                boolean warningFlag = (boolean) sleepRecord.get("warningFlag");
                LocalDateTime createdAt = (LocalDateTime) sleepRecord.get("createdAt");
                
                // Create a styled sleep record item
                VBox recordItem = new VBox(8);
                recordItem.setPadding(new Insets(15));
                
                // Set background color based on warning flag
                Color bgColor = warningFlag ? COLOR_WARNING.deriveColor(0, 1, 1, 0.2) : COLOR_SUCCESS.deriveColor(0, 1, 1, 0.2);
                recordItem.setStyle(
                    "-fx-background-color: " + toRgbaString(bgColor) + "; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: " + toRgbaString(bgColor.darker()) + "; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-width: 1px;"
                );
                
                // Format the date and times
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                
                // Date
                Text dateText = new Text("Date: " + createdAt.format(dateFormatter));
                dateText.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
                dateText.setFill(COLOR_TEXT_PRIMARY);
                
                // Sleep time
                Text sleepTimeText = new Text("Sleep Time: " + sleepTime.format(timeFormatter));
                sleepTimeText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
                sleepTimeText.setFill(COLOR_TEXT_SECONDARY);
                
                // Wake-up time
                Text wakeupTimeText = new Text("Wake-up Time: " + wakeupTime.format(timeFormatter));
                wakeupTimeText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
                wakeupTimeText.setFill(COLOR_TEXT_SECONDARY);
                
                // Total sleep
                Text totalSleepText = new Text(String.format("Total Sleep: %d hours %d minutes", sleepHours, sleepMinutes));
                totalSleepText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
                totalSleepText.setFill(COLOR_TEXT_SECONDARY);
                
                // Warning if applicable
                if (warningFlag) {
                    Text warningText = new Text("⚠️ Less than recommended 8 hours of sleep");
                    warningText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
                    warningText.setFill(COLOR_WARNING.darker());
                    recordItem.getChildren().addAll(dateText, sleepTimeText, wakeupTimeText, totalSleepText, warningText);
                } else {
                    Text goodSleepText = new Text("✓ Healthy sleep duration");
                    goodSleepText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
                    goodSleepText.setFill(COLOR_SUCCESS.darker());
                    recordItem.getChildren().addAll(dateText, sleepTimeText, wakeupTimeText, totalSleepText, goodSleepText);
                }
                
                recordsContainer.getChildren().add(recordItem);
            }
        } else {
            // No records found
            Label noRecordsLabel = new Label("No sleep records found");
            noRecordsLabel.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
            noRecordsLabel.setTextFill(COLOR_TEXT_SECONDARY);
            noRecordsLabel.setPadding(new Insets(20));
            noRecordsLabel.setAlignment(Pos.CENTER);
            
            recordsContainer.getChildren().add(noRecordsLabel);
        }
        
        // Create a scroll pane for the records
        ScrollPane scrollPane = new ScrollPane(recordsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent;"
        );
        
        // Close button
        Button closeButton = createActionButton("Close");
        closeButton.setOnAction(e -> sleepHistoryStage.close());
        
        // Add all elements to the root
        root.getChildren().addAll(title, scrollPane, closeButton);
        
        // Create and show the scene
        Scene scene = new Scene(root, 500, 500);
        sleepHistoryStage.setTitle("Sleep History");
        sleepHistoryStage.setScene(scene);
        sleepHistoryStage.show();
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