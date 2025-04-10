package lifesync.ui_main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import databasePackage.DbConnection;
import databasePackage.UserSession;
import databasePackage.sendAndImportPendingList;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

public class DashboardView extends Application {

    private VBox pendingTasksContainer;
    private Map<String, LocalDate> completedTasks;
    private DbConnection dbConnection;
    
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

    @Override
    public void start(Stage primaryStage) {
        completedTasks = new HashMap<>();
        
        // Initialize database connection
        dbConnection = DbConnection.getInstance();

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
        primaryStage.setTitle("LifeSync - Dashboard");
        primaryStage.setScene(scene);
        
        // Maximize the window but keep the taskbar visible
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    
    private void switchToSleepTrackerView(Stage primaryStage) {
        // Clear the existing content
        BorderPane root = (BorderPane) ((StackPane) primaryStage.getScene().getRoot()).getChildren().get(1);
        
        // Create the SleepTrackerView content
        SleepTrackerView sleepTrackerView = new SleepTrackerView();
        
        // Create a new BorderPane for the sleep tracker
        BorderPane sleepTrackerRoot = new BorderPane();
        sleepTrackerRoot.setPrefSize(1000, 700);
        
        // Create background
        StackPane backgroundPane = sleepTrackerView.createBackground();
        
        // Create sidebar and main content from SleepTrackerView
        VBox sidebar = sleepTrackerView.createSidebar(primaryStage);
        VBox mainContent = sleepTrackerView.createMainContent(primaryStage);
        
        // Set up the layout
        sleepTrackerRoot.setLeft(sidebar);
        sleepTrackerRoot.setCenter(mainContent);
        
        // Replace the content in the scene
        StackPane sceneRoot = new StackPane(backgroundPane, sleepTrackerRoot);
        primaryStage.getScene().setRoot(sceneRoot);
        
        // Ensure table exists
        sleepTrackerView.ensureTableExists();
    }
    
    
    private void switchToMoodTrackerView(Stage primaryStage) {
        // Clear the existing content
        BorderPane root = (BorderPane) ((StackPane) primaryStage.getScene().getRoot()).getChildren().get(1);
        
        // Create the MoodTrackerView content
        MoodTrackerView moodTrackerView = new MoodTrackerView();
        
        // Create a new BorderPane for the mood tracker
        BorderPane moodTrackerRoot = new BorderPane();
        moodTrackerRoot.setPrefSize(1000, 700);
        
        // Create background
        StackPane backgroundPane = createBackground();
        
        // Create sidebar and main content from MoodTrackerView
        VBox sidebar = moodTrackerView.createSidebar(primaryStage);
        VBox mainContent = moodTrackerView.createMainContent(primaryStage);
        
        // Set up the layout
        moodTrackerRoot.setLeft(sidebar);
        moodTrackerRoot.setCenter(mainContent);
        
        // Replace the content in the scene
        StackPane sceneRoot = new StackPane(backgroundPane, moodTrackerRoot);
        primaryStage.getScene().setRoot(sceneRoot);
        
        // Ensure table exists - now this will work
        moodTrackerView.ensureTableExists();
    }
    
    
    private void switchToFinanceTrackerView(Stage primaryStage) {
        // Clear the existing content
        BorderPane root = (BorderPane) ((StackPane) primaryStage.getScene().getRoot()).getChildren().get(1);
        
        // Create the FinanceTrackerView content
        FinanceTrackerView financeTrackerView = new FinanceTrackerView();
        
        // Create a new BorderPane for the finance tracker
        BorderPane financeTrackerRoot = new BorderPane();
        financeTrackerRoot.setPrefSize(1000, 700);
        
        // Create background
        StackPane backgroundPane = financeTrackerView.createBackground();
        
        // Create sidebar and main content from FinanceTrackerView
        VBox sidebar = financeTrackerView.createSidebar(primaryStage);
        VBox mainContent = financeTrackerView.createMainContent(primaryStage);
        
        // Set up the layout
        financeTrackerRoot.setLeft(sidebar);
        financeTrackerRoot.setCenter(mainContent);
        
        // Replace the content in the scene
        StackPane sceneRoot = new StackPane(backgroundPane, financeTrackerRoot);
        primaryStage.getScene().setRoot(sceneRoot);
        
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
    
    public void createDashboardContent(StackPane root, Stage primaryStage) {
        completedTasks = new HashMap<>();

        // Initialize database connection if not already done
        if (dbConnection == null) {
            dbConnection = DbConnection.getInstance();
        }

        // Create a background with gradient and shapes
        StackPane backgroundPane = createBackground();
        
        // Main container
        BorderPane contentRoot = new BorderPane();
        contentRoot.setPrefSize(1000, 700);

        // Sidebar with soft purple styling
        VBox sidebar = createSidebar(primaryStage);
        
        // Main content area
        VBox mainContent = createMainContent(primaryStage);
        
        // Add components to contentRoot
        contentRoot.setLeft(sidebar);
        contentRoot.setCenter(mainContent);
        
        // Add the background and content to the provided root
        root.getChildren().addAll(backgroundPane, contentRoot);

        // Update the title
        primaryStage.setTitle("LifeSync - Dashboard");
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
        Button homeButton = createSidebarButton("🏠  Home", true);
        Button moodButton = createSidebarButton("😊  Mood", false);
        Button sleepButton = createSidebarButton("🛏️  Sleep", false);
        Button financeButton = createSidebarButton("💰  Finance", false);
        Button profileButton = createSidebarButton("👤  My Profile", false);
        
        
        // Set button actions
        homeButton.setOnAction(e -> new DashboardView().start(primaryStage));
        moodButton.setOnAction(e -> switchToMoodTrackerView(primaryStage));
        sleepButton.setOnAction(e -> switchToSleepTrackerView(primaryStage));
        financeButton.setOnAction(e -> switchToFinanceTrackerView(primaryStage));
        profileButton.setOnAction(e -> switchToMyProfileView(primaryStage));
        // Add spacer
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
        // Main content area with white background and rounded corners
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(20), null)));
        mainContent.setEffect(new DropShadow(15, Color.web("#00000015")));
        BorderPane.setMargin(mainContent, new Insets(20));
        
        // Header with title and completed tasks button
        HBox header = createHeader(primaryStage);
        
        // Task management section
        HBox taskSection = createTaskSection();
        
        // Add all sections to main content
        mainContent.getChildren().addAll(header, taskSection);
        
        return mainContent;
    }
    
    private HBox createHeader(Stage primaryStage) {
        // Dashboard title
        Text dashboardTitle = new Text("Dashboard");
        dashboardTitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 32));
        dashboardTitle.setFill(COLOR_TEXT_PRIMARY);
        
     // Today's date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        Text dateText = new Text(today.format(formatter));
        dateText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        dateText.setFill(COLOR_TEXT_SECONDARY);
        
        VBox titleBox = new VBox(5, dashboardTitle, dateText);
        
        // Completed tasks button
        Button completedTasksButton = createActionButton("View Completed Tasks");
        completedTasksButton.setOnAction(e -> showCompletedTasks(primaryStage));
        
        // Create header layout
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleBox, spacer, completedTasksButton);
        
        return header;
    }
    
    private HBox createTaskSection() {
        // Task creation panel
        VBox taskCreationPanel = createTaskCreationPanel();
        
        // Pending tasks panel
        VBox pendingTasksPanel = createPendingTasksPanel();
        
        // Create task section layout
        HBox taskSection = new HBox(25);
        taskSection.getChildren().addAll(taskCreationPanel, pendingTasksPanel);
        HBox.setHgrow(taskCreationPanel, Priority.ALWAYS);
        HBox.setHgrow(pendingTasksPanel, Priority.ALWAYS);
        
        return taskSection;
    }
    
    private VBox createTaskCreationPanel() {
        // Panel title
        Text panelTitle = new Text("Create New Task");
        panelTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        panelTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Task input field
        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter a task");
        taskInput.setStyle(
            "-fx-background-color: #F8F7FC; " +
            "-fx-border-color: #E2DCF7; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 12px; " +
            "-fx-font-size: 14px;"
        );
        
        // Add task button
        Button addTaskButton = createActionButton("Add Task");
        addTaskButton.setOnAction(e -> addTask(taskInput.getText()));
        addTaskButton.setPrefWidth(150);
        
        // Task creation instructions
        Text instructionsText = new Text("Add tasks to your daily list to stay organized and productive.");
        instructionsText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        instructionsText.setFill(COLOR_TEXT_SECONDARY);
        instructionsText.setWrappingWidth(300);
        
        // Create panel layout
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(25));
        panel.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(15), null)));
        panel.setEffect(new DropShadow(8, Color.web("#00000010")));
        panel.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
            COLOR_ACCENT, javafx.scene.layout.BorderStrokeStyle.SOLID, 
            new javafx.scene.layout.CornerRadii(15), new javafx.scene.layout.BorderWidths(1))));
        
        // Add elements to panel
        panel.getChildren().addAll(panelTitle, taskInput, addTaskButton, instructionsText);
        
        return panel;
    }
    
    private VBox createPendingTasksPanel() {
        // Panel title
        Text panelTitle = new Text("Your Pending Tasks");
        panelTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        panelTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Create pending tasks container
        pendingTasksContainer = new VBox(12);
        pendingTasksContainer.setPadding(new Insets(5, 0, 5, 0));
        
        // Load tasks from database for the current user
        List<String> pendingTasks = sendAndImportPendingList.getPendingTasks();
        for (String taskDescription : pendingTasks) {
            addTaskToUI(taskDescription);
        }
        
        // Create scrollable container for tasks
        ScrollPane scrollPane = new ScrollPane(pendingTasksContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-padding: 0;"
        );
        
        // Create panel layout
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(25));
        panel.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(15), null)));
        panel.setEffect(new DropShadow(8, Color.web("#00000010")));
        panel.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
            COLOR_ACCENT, javafx.scene.layout.BorderStrokeStyle.SOLID, 
            new javafx.scene.layout.CornerRadii(15), new javafx.scene.layout.BorderWidths(1))));
        
        // Add elements to panel
        panel.getChildren().addAll(panelTitle, scrollPane);
        
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
    
    private void addTask(String task) {
        if (!task.isEmpty()) {
            sendAndImportPendingList.addTask(task); // Save to database
            addTaskToUI(task);
        }
    }

    // Add task to UI with improved styling
    private void addTaskToUI(String task) {
        HBox taskItem = new HBox(10);
        taskItem.setAlignment(Pos.CENTER_LEFT);
        taskItem.setPadding(new Insets(8, 10, 8, 10));
        taskItem.setStyle(
            "-fx-background-color: #F8F7FC; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: #E2DCF7; " +
            "-fx-border-radius: 8px;"
        );
        
        CheckBox taskCheckBox = new CheckBox();
        taskCheckBox.setStyle(
            "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + ";" +
            "-fx-font-size: 14px;"
        );
        
        Label taskLabel = new Label(task);
        taskLabel.setFont(Font.font("Segoe UI", 14));
        taskLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        taskItem.getChildren().addAll(taskCheckBox, taskLabel);
        
        // Add animation for new tasks
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), taskItem);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        // Handle task completion
        taskCheckBox.setOnAction(e -> {
            if (taskCheckBox.isSelected()) {
                // Visual feedback when checked
                taskLabel.setTextFill(COLOR_TEXT_SECONDARY);
                taskLabel.setStyle("-fx-strikethrough: true;");
                
                // Handle completion in database
                handleTaskCompletion(task);
                
                // Animate task completion
                FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), taskItem);
                fadeOut.setDelay(Duration.millis(500));
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(event -> pendingTasksContainer.getChildren().remove(taskItem));
                fadeOut.play();
            }
        });
        
        pendingTasksContainer.getChildren().add(taskItem);
    }

    private void handleTaskCompletion(String taskName) {
        // Ensure completedTasks is initialized
        if (completedTasks == null) {
            completedTasks = new HashMap<>();
        }
        
        // Add to completed tasks in the local map
        completedTasks.put(taskName, LocalDate.now());

        // Ensure database connection is initialized
        if (dbConnection == null) {
            dbConnection = DbConnection.getInstance();
        }

        // Get current user ID from session
        int userId = UserSession.getInstance().getUserId();
        
        if (userId == -1) {
            System.err.println("❌ No user logged in. Cannot mark task as completed.");
            return;
        }

        // Insert into the database using DbConnection
        try (Connection conn = dbConnection.getConnection()) {
            // SQL query to insert the completed task into the completed_task table
            String insertQuery = "INSERT INTO completed_tasks (user_id, task_description, completion_date) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setInt(1, userId);  // Set user ID from session
                pstmt.setString(2, taskName);  // Set task name
                pstmt.setDate(3, Date.valueOf(LocalDate.now()));  // Set current date
                pstmt.executeUpdate();  // Execute the insert query
                System.out.println("✓ Task '" + taskName + "' marked as completed for user ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error recording completed task: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove from the pending list (update the database accordingly)
        sendAndImportPendingList.removeTask(taskName); // Call the method to remove from pending list
    }

    public void showCompletedTasks(Stage primaryStage) {
        // Ensure database connection is initialized
        if (dbConnection == null) {
            dbConnection = DbConnection.getInstance();
        }
        
        // Get current user ID from session
        int userId = UserSession.getInstance().getUserId();
        
        if (userId == -1) {
            System.err.println("❌ No user logged in. Cannot show completed tasks.");
            return;
        }
        
        // Create a new stage for completed tasks
        Stage completedTasksStage = new Stage();
        
        // Create the main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, null, null)));
        
        // Title
        Text title = new Text("Completed Tasks");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 24));
        title.setFill(COLOR_TEXT_PRIMARY);
        
        // Container for completed tasks
        VBox tasksContainer = new VBox(10);
        tasksContainer.setPadding(new Insets(15));
        tasksContainer.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_BG_START, new javafx.scene.layout.CornerRadii(10), null)));
        
        // Retrieve completed tasks from the database and add them to the container
        try (Connection conn = dbConnection.getConnection()) {
            // Query to get completed tasks for the current user
            String selectQuery = "SELECT task_description, completion_date FROM completed_tasks WHERE user_id = ? ORDER BY completion_date DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String completedTask = rs.getString("task_description");
                        LocalDate completedDate = rs.getDate("completion_date").toLocalDate();
                        
                        // Create a styled task item
                        HBox taskItem = new HBox(15);
                        taskItem.setPadding(new Insets(10, 15, 10, 15));
                        taskItem.setStyle(
                            "-fx-background-color: white; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-border-color: #E2DCF7; " +
                            "-fx-border-radius: 8px;"
                        );
                        
                        // Task name
                        Label taskLabel = new Label(completedTask);
                        taskLabel.setFont(Font.font("Segoe UI", 14));
                        taskLabel.setTextFill(COLOR_TEXT_PRIMARY);
                        
                        // Completion date
                        Label dateLabel = new Label(completedDate.toString());
                        dateLabel.setFont(Font.font("Segoe UI", 12));
                        dateLabel.setTextFill(COLOR_TEXT_SECONDARY);
                        
                        // Add spacer
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        
                        taskItem.getChildren().addAll(taskLabel, spacer, dateLabel);
                        tasksContainer.getChildren().add(taskItem);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving completed tasks: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Create a scroll pane for the tasks
        ScrollPane scrollPane = new ScrollPane(tasksContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent;"
        );
        
        // Close button
        Button closeButton = createActionButton("Close");
        closeButton.setOnAction(e -> completedTasksStage.close());
        
        // Add all elements to the root
        root.getChildren().addAll(title, scrollPane, closeButton);
        
        // Create and show the scene
        Scene scene = new Scene(root, 500, 500);
        completedTasksStage.setTitle("Completed Tasks");
        completedTasksStage.setScene(scene);
        completedTasksStage.show();
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