package lifesync.ui_main;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import databasePackage.FinanceDatabase;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

public class FinanceTrackerView extends Application {

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
    private static final Color COLOR_INCOME = Color.web("#B7E1CD");    // Soft green for income
    private static final Color COLOR_EXPENSE = Color.web("#FFD6D6");   // Soft red for expenses
    private static final Color COLOR_SAVINGS = Color.web("#D0BDF4");   // Soft purple for savings

    private List<Expense> expenseHistory = new ArrayList<>(); // Store expense data with description
    private double totalIncome;
    private double totalExpenses;
    private double savingGoal;
    private Label totalIncomeLabel;	
    private Label totalMoneyLeftLabel;
    private Label savingGoalLabel;
    private Label totalExpensesLabel;
    private PieChart financeChart;
    
    
    @Override
    public void start(Stage primaryStage) {
        // Create the finance table when the application starts
        
    	expenseHistory = new ArrayList<>();
        totalIncome = 0;
        totalExpenses = 0;
        savingGoal = 0;
        
        // Load existing data from database
        loadFinanceData();

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
        primaryStage.setTitle("LifeSync - Finance Tracker");
        primaryStage.setScene(scene);
        
        // Maximize the window but keep the taskbar visible
        primaryStage.setMaximized(true);

        primaryStage.show();
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
    
    
    public void ensureTableExists() {
        FinanceDatabase.ensureTableExists();
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
        Button sleepButton = createSidebarButton("🛏️  Sleep", false);
        Button financeButton = createSidebarButton("💰  Finance", true);
        Button profileButton = createSidebarButton("👤  My Profile", false);
        
        // Set button actions
        homeButton.setOnAction(e -> switchToDashboardView(primaryStage));
        moodButton.setOnAction(e -> switchToMoodTrackerView(primaryStage));
        sleepButton.setOnAction(e -> switchToSleepTrackerView(primaryStage));
        financeButton.setOnAction(e -> new FinanceTrackerView().start(primaryStage));
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
        
        // Header with title and expense history button
        HBox header = createHeader(primaryStage);
        
        // Finance tracking content
        VBox financeContent = createFinanceContent();
        
        // Add all sections to main content
        mainContent.getChildren().addAll(header, financeContent);
        
        return mainContent;
    }
    
    private HBox createHeader(Stage primaryStage) {
        // Page title
        Text pageTitle = new Text("Finance Tracker");
        pageTitle.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 32));
        pageTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Today's date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        Text dateText = new Text(today.format(formatter));
        dateText.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
        dateText.setFill(COLOR_TEXT_SECONDARY);
        
        VBox titleBox = new VBox(5, pageTitle, dateText);
        
        // Expense history button
        Button expenseHistoryButton = createActionButton("View Expense History");
        expenseHistoryButton.setOnAction(e -> showExpenses());
        
        // Create header layout
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleBox, spacer, expenseHistoryButton);
        
        return header;
    }
    
    private VBox createFinanceContent() {
        // Create two panels side by side
        HBox contentLayout = new HBox(20);
        contentLayout.setAlignment(Pos.TOP_CENTER);
        
        // Left panel - Finance inputs
        VBox inputPanel = createInputPanel();
        
        // Right panel - Finance summary
        VBox summaryPanel = createSummaryPanel();
        
        contentLayout.getChildren().addAll(inputPanel, summaryPanel);
        
        return new VBox(contentLayout);
    }
    
    private VBox createInputPanel() {
        // Panel title
        Text panelTitle = new Text("Track Your Finances");
        panelTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 22));
        panelTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Income input
        Label incomeLabel = new Label("Add Income");
        incomeLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 16));
        incomeLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        HBox incomeBox = createInputBox("Enter Income Amount", "Add Income", (amount) -> {
            totalIncome += amount;
            updateLabels();
            saveFinanceData();
        });
        
        // Expense input
        Label expenseLabel = new Label("Add Expense");
        expenseLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 16));
        expenseLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        HBox expenseBox = createExpenseInputBox();
        
        // Saving goal input
        Label savingGoalLabel = new Label("Set Saving Goal");
        savingGoalLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 16));
        savingGoalLabel.setTextFill(COLOR_TEXT_PRIMARY);
        
        HBox savingGoalBox = createInputBox("Enter Saving Goal Amount", "Set Goal", (amount) -> {
            savingGoal = amount;
            this.savingGoalLabel.setText(String.format("$%.2f", savingGoal));
            saveFinanceData();
        });
        
        // Create the panel
        VBox panel = new VBox(15, 
            panelTitle,
            incomeLabel, incomeBox,
            expenseLabel, expenseBox,
            savingGoalLabel, savingGoalBox
        );
        panel.setPadding(new Insets(25));
        panel.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(15), null)));
        panel.setEffect(new DropShadow(8, Color.web("#00000010")));
        panel.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
            COLOR_ACCENT, javafx.scene.layout.BorderStrokeStyle.SOLID, 
            new javafx.scene.layout.CornerRadii(15), new javafx.scene.layout.BorderWidths(1))));
        panel.setPrefWidth(400);
        
        return panel;
    }
    
 // Modify the createSummaryPanel() method to store the label reference
    private VBox createSummaryPanel() {
        // Panel title
        Text panelTitle = new Text("Financial Summary");
        panelTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 22));
        panelTitle.setFill(COLOR_TEXT_PRIMARY);
        
        // Create summary labels with styled containers
        VBox incomeContainer = createSummaryContainer("Income", COLOR_INCOME);
        totalIncomeLabel = new Label(String.format("$%.2f", totalIncome));
        totalIncomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        totalIncomeLabel.setTextFill(COLOR_TEXT_PRIMARY);
        incomeContainer.getChildren().add(totalIncomeLabel);
        
        VBox expenseContainer = createSummaryContainer("Expenses", COLOR_EXPENSE);
        totalExpensesLabel = new Label(String.format("$%.2f", totalExpenses));
        totalExpensesLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        totalExpensesLabel.setTextFill(COLOR_TEXT_PRIMARY);
        expenseContainer.getChildren().add(totalExpensesLabel);
        
        VBox balanceContainer = createSummaryContainer("Balance", COLOR_SUCCESS);
        totalMoneyLeftLabel = new Label(String.format("$%.2f", totalIncome - totalExpenses));
        totalMoneyLeftLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        totalMoneyLeftLabel.setTextFill(COLOR_TEXT_PRIMARY);
        balanceContainer.getChildren().add(totalMoneyLeftLabel);
        
        VBox savingsContainer = createSummaryContainer("Saving Goal", COLOR_SAVINGS);
        this.savingGoalLabel = new Label(String.format("$%.2f", savingGoal));
        this.savingGoalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        this.savingGoalLabel.setTextFill(COLOR_TEXT_PRIMARY);
        savingsContainer.getChildren().add(this.savingGoalLabel);
        
        // Create a simple pie chart for visualization
        financeChart = createFinancePieChart();
        
        // Create the panel
        VBox panel = new VBox(15, 
            panelTitle,
            new HBox(10, incomeContainer, expenseContainer),
            new HBox(10, balanceContainer, savingsContainer),
            financeChart
        );
        panel.setPadding(new Insets(25));
        panel.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            COLOR_PANEL_BG, new javafx.scene.layout.CornerRadii(15), null)));
        panel.setEffect(new DropShadow(8, Color.web("#00000010")));
        panel.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(
            COLOR_ACCENT, javafx.scene.layout.BorderStrokeStyle.SOLID, 
            new javafx.scene.layout.CornerRadii(15), new javafx.scene.layout.BorderWidths(1))));
        panel.setPrefWidth(400);
        
        return panel;
    }
    
    private VBox createSummaryContainer(String title, Color color) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        titleLabel.setTextFill(COLOR_TEXT_SECONDARY);
        
        VBox container = new VBox(5, titleLabel);
        container.setPadding(new Insets(15));
        container.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
            color.deriveColor(0, 1, 1, 0.2), new javafx.scene.layout.CornerRadii(10), null)));
        container.setAlignment(Pos.CENTER);
        container.setPrefWidth(180);
        
        return container;
    }
    
    private PieChart createFinancePieChart() {
        // Create pie chart data
        PieChart.Data incomeData = new PieChart.Data("Income", totalIncome);
        PieChart.Data expenseData = new PieChart.Data("Expenses", totalExpenses);
        PieChart.Data savingsData = new PieChart.Data("Savings", totalIncome - totalExpenses);
        
        PieChart chart = new PieChart();
        chart.getData().addAll(incomeData, expenseData, savingsData);
        chart.setTitle("Financial Breakdown");
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        chart.setPrefHeight(200);
        
        // Style the chart
        chart.setStyle("-fx-font-family: 'Segoe UI';");
        
        return chart;
    }
    
    private HBox createInputBox(String prompt, String buttonText, AmountHandler handler) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
            "-fx-background-color: #F8F7FC; " +
            "-fx-border-color: #E2DCF7; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 10px; " +
            "-fx-font-size: 14px;"
        );
        HBox.setHgrow(field, Priority.ALWAYS);
        
        Button button = createActionButton(buttonText);
        
        button.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(field.getText());
                handler.handle(amount);
                field.clear();
                
                // Add animation for feedback
                FadeTransition fade = new FadeTransition(Duration.millis(300), button);
                fade.setFromValue(0.7);
                fade.setToValue(1.0);
                fade.setCycleCount(2);
                fade.setAutoReverse(true);
                fade.play();
                
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });
        
        box.getChildren().addAll(field, button);
        return box;
    }
    
    private HBox createExpenseInputBox() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter Expense Amount");
        amountField.setStyle(
            "-fx-background-color: #F8F7FC; " +
            "-fx-border-color: #E2DCF7; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 10px; " +
            "-fx-font-size: 14px;"
        );
        
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter Expense Description");
        descriptionField.setStyle(
            "-fx-background-color: #F8F7FC; " +
            "-fx-border-color: #E2DCF7; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 10px; " +
            "-fx-font-size: 14px;"
        );
        
        HBox.setHgrow(amountField, Priority.ALWAYS);
        HBox.setHgrow(descriptionField, Priority.ALWAYS);
        
        Button saveButton = createActionButton("Save Expense");
        
        saveButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText().isEmpty() ? "No description" : descriptionField.getText();
                LocalDate currentDate = LocalDate.now();

                // Save to database immediately
                saveExpenseToDatabase(amount, description, currentDate);

                // Check goal and clear fields
                checkMoneyLeft();
                amountField.clear();
                descriptionField.clear();

                // Feedback animation
                FadeTransition fade = new FadeTransition(Duration.millis(300), saveButton);
                fade.setFromValue(0.7);
                fade.setToValue(1.0);
                fade.play();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });
        
        VBox fieldsBox = new VBox(10, amountField, descriptionField);
        
        box.getChildren().addAll(fieldsBox, saveButton);
        return box;
    }
    
    
    private void saveExpenseToDatabase(double amount, String description, LocalDate date) {
        try {
            // Debug output
            System.out.println("Attempting to save expense: $" + amount + " - " + description + " - " + date);
            
            // Call the correct method with the right parameters
            boolean success = FinanceDatabase.saveExpense(amount, description, date);
            
            if (success) {
                System.out.println("✓ Expense saved successfully to database");
                
                // Update local data
                expenseHistory.add(new Expense(date, amount, description));
                totalExpenses += amount;
                
                // Update UI
                saveFinanceData();
                updateLabels();
            } else {
                System.out.println("❌ Failed to save expense to database");
                showAlert("Database Error", "Failed to save expense to database. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error in saveExpenseToDatabase: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
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
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + toRgbaString(COLOR_BUTTON_ACCENT) + "; " +
                "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px 15px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 6, 0, 0, 2);"
            );
            
            // Add scale animation
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), button);
            scaleIn.setToX(1.05);
            scaleIn.setToY(1.05);
            scaleIn.play();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + toRgbaString(COLOR_BUTTON_PRIMARY) + "; " +
                "-fx-text-fill: " + toRgbaString(COLOR_TEXT_PRIMARY) + "; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px 15px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);"
            );
            
            // Add scale animation
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), button);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            scaleOut.play();
        });
        
        return button;
    }
    
 // Modify the updateLabels() method to update the expense label
    private void updateLabels() {
        totalIncomeLabel.setText(String.format("$%.2f", totalIncome));
        totalExpensesLabel.setText(String.format("$%.2f", totalExpenses));
        totalMoneyLeftLabel.setText(String.format("$%.2f", (totalIncome - totalExpenses)));
        savingGoalLabel.setText(String.format("$%.2f", savingGoal));

        // Update pie chart
        financeChart.getData().clear();
        financeChart.getData().addAll(
            new PieChart.Data("Income", totalIncome),
            new PieChart.Data("Expenses", totalExpenses),
            new PieChart.Data("Balance", totalIncome - totalExpenses)
        );
    }
    
    private void checkSavingGoal() {
        if ((totalIncome - totalExpenses) < savingGoal) {
            showAlert("Saving Goal Alert", "You have not reached your saving goal yet. Keep going!");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving Goal Reached");
            alert.setHeaderText(null);
            alert.setContentText("Congratulations! You've reached your saving goal!");
            alert.showAndWait();
        }
    }
    
    private void checkMoneyLeft() {
        if ((totalIncome - totalExpenses) < savingGoal) {
            showAlert("Low Money Left Alert", "Your remaining money is lower than your saving goal!");
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    
    private void loadFinanceData() {
        // Load summary data
        Map<String, Object> summary = FinanceDatabase.getLatestFinanceSummary();
        totalIncome = (double) summary.get("totalIncome");
        totalExpenses = (double) summary.get("totalExpenses");
        savingGoal = (double) summary.get("savingGoal");
        
        // Load expenses
        List<Map<String, Object>> expensesList = FinanceDatabase.getAllExpenses();
        expenseHistory.clear();
        for (Map<String, Object> expense : expensesList) {
            LocalDate date = (LocalDate) expense.get("date");
            double amount = (double) expense.get("amount");
            String desc = (String) expense.get("description");
            expenseHistory.add(new Expense(date, amount, desc));
        }
        
        // Recalculate total expenses from loaded data
        totalExpenses = expenseHistory.stream().mapToDouble(Expense::getAmount).sum();
        
        updateLabels();
    }
    
    private void saveFinanceData() {
        FinanceDatabase.saveFinanceSummary(totalIncome, totalExpenses, savingGoal);
    }
    
    private List<Expense> retrieveExpenses() {
        List<Expense> expenses = new ArrayList<>();
        
        try {
            System.out.println("Retrieving expenses from database...");
            List<Map<String, Object>> expensesList = FinanceDatabase.getAllExpenses();
            System.out.println("Retrieved " + expensesList.size() + " expenses from database");
            
            for (Map<String, Object> expenseData : expensesList) {
                try {
                    LocalDate date = (LocalDate) expenseData.get("date");
                    double amount = (double) expenseData.get("amount");
                    String description = (String) expenseData.get("description");
                    
                    System.out.println("Processing expense: " + date + " - $" + amount + " - " + description);
                    expenses.add(new Expense(date, amount, description));
                } catch (Exception e) {
                    System.err.println("❌ Error processing expense data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error retrieving expenses: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Returning " + expenses.size() + " expenses");
        return expenses;
    }
    
    private void showExpenses() {
        try {
            // Create a new stage for expense history
            Stage expenseHistoryStage = new Stage();
            
            // Create the main container
            VBox root = new VBox(20);
            root.setPadding(new Insets(30));
            root.setAlignment(Pos.TOP_CENTER);
            root.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                COLOR_PANEL_BG, null, null)));
            
            // Title
            Text title = new Text("Your Expense History");
            title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 24));
            title.setFill(COLOR_TEXT_PRIMARY);
            
            // Container for expenses
            VBox expensesContainer = new VBox(10);
            expensesContainer.setPadding(new Insets(15));
            expensesContainer.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                COLOR_BG_START, new javafx.scene.layout.CornerRadii(10), null)));
            
            // Retrieve expenses
            System.out.println("Retrieving expenses for display...");
            List<Expense> expenses = retrieveExpenses();
            System.out.println("Retrieved " + expenses.size() + " expenses for display");
            
            if (expenses.isEmpty()) {
                Label noExpensesLabel = new Label("No expenses recorded yet");
                noExpensesLabel.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 14));
                noExpensesLabel.setTextFill(COLOR_TEXT_SECONDARY);
                noExpensesLabel.setPadding(new Insets(20));
                noExpensesLabel.setAlignment(Pos.CENTER);
                
                expensesContainer.getChildren().add(noExpensesLabel);
            } else {
                // Add expense items
                for (Expense expense : expenses) {
                    HBox expenseItem = new HBox(15);
                    expenseItem.setPadding(new Insets(15));
                    expenseItem.setAlignment(Pos.CENTER_LEFT);
                    
                    // Format the date
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                    String formattedDate = expense.getExpenseDate().format(formatter);
                    
                    // Set background color
                    expenseItem.setStyle(
                        "-fx-background-color: " + toRgbaString(COLOR_EXPENSE.deriveColor(0, 1, 1, 0.2)) + "; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: " + toRgbaString(COLOR_EXPENSE.deriveColor(0, 1, 1, 0.4)) + "; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1px;"
                    );
                    
                    // Date
                    Label dateLabel = new Label(formattedDate);
                    dateLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
                    dateLabel.setTextFill(COLOR_TEXT_PRIMARY);
                    dateLabel.setMinWidth(120);
                    
                    // Amount
                    Label amountLabel = new Label(String.format("$%.2f", expense.getAmount()));
                    amountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                    amountLabel.setTextFill(COLOR_TEXT_PRIMARY);
                    amountLabel.setMinWidth(100);
                    
                    // Description
                    Label descriptionLabel = new Label(expense.getDescription());
                    descriptionLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
                    descriptionLabel.setTextFill(COLOR_TEXT_SECONDARY);
                    HBox.setHgrow(descriptionLabel, Priority.ALWAYS);
                    
                    expenseItem.getChildren().addAll(dateLabel, amountLabel, descriptionLabel);
                    expensesContainer.getChildren().add(expenseItem);
                }
            }
            
            // Create a scroll pane for the expenses
            ScrollPane scrollPane = new ScrollPane(expensesContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(400);
            scrollPane.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent;"
            );
            
            // Close button
            Button closeButton = createActionButton("Close");
            closeButton.setOnAction(e -> expenseHistoryStage.close());
            
            // Add all elements to the root
            root.getChildren().addAll(title, scrollPane, closeButton);
            
            // Create and show the scene
            Scene scene = new Scene(root, 500, 500);
            expenseHistoryStage.setTitle("Expense History");
            expenseHistoryStage.setScene(scene);
            expenseHistoryStage.show();
        } catch (Exception e) {
            System.err.println("❌ Error showing expenses: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An error occurred while showing expenses: " + e.getMessage());
        }
    }
    
    // Expense class to represent expense data
    public static class Expense { // Mark as static if it's an inner class
        private final LocalDate expenseDate;
        private final double amount;
        private final String description;

        public Expense(LocalDate expenseDate, double amount, String description) {
            this.expenseDate = expenseDate;
            this.amount = amount;
            this.description = description;
        }

        // Getters
        public LocalDate getExpenseDate() { return expenseDate; }
        public double getAmount() { return amount; }
        public String getDescription() { return description; }

        @Override
        public String toString() { // Corrected with 'public'
            return String.format("Date: %s - $%.2f - Description: %s", 
                expenseDate, amount, description);
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
    
    @FunctionalInterface
    interface AmountHandler {
        void handle(double amount);
    }
    
    @Override
    public void stop() {
        saveFinanceData();
    }

    public static void main(String[] args) {
        launch(args);
    }
}