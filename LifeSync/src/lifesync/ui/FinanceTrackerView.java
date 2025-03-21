package lifesync.ui;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FinanceTrackerView extends Application {

    private Map<String, String[]> expenseHistory; // Store expense data with description
    private double totalIncome;
    private double totalExpenses;
    private double savingGoal;
    private Label totalIncomeLabel;
    private Label totalMoneyLeftLabel;
    private Label savingGoalLabel;

    @Override
    public void start(Stage primaryStage) {
        expenseHistory = new HashMap<>();
        totalIncome = 0;
        totalExpenses = 0;
        savingGoal = 0;

        BorderPane root = new BorderPane();
        root.setPrefSize(900, 600);

        VBox sidebar = createSidebar(primaryStage);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: #EADEF9; -fx-background-radius: 20px;");

        Button profileButton = createHeaderButton("\uD83D\uDC64 Profile");
        Button trackExpensesButton = createHeaderButton("📊 Track Your Expenses");

        // Add hover effect to the buttons
        addButtonHoverEffect(profileButton);
        addButtonHoverEffect(trackExpensesButton);

        HBox topButtons = new HBox(20, profileButton, trackExpensesButton);
        topButtons.setAlignment(Pos.CENTER);

        trackExpensesButton.setOnAction(e -> {
            showExpenses();
        });

        HBox incomeBox = createInputBox("Enter Income", "Save Income", (amount) -> {
            totalIncome += amount;
            updateLabels();
        });

        HBox expenseBox = createExpenseInputBox();

        HBox savingGoalBox = createInputBox("Enter Saving Goal", "Set Saving Goal", (amount) -> {
            savingGoal = amount;
            savingGoalLabel.setText("Saving Goal: $" + savingGoal);
            checkSavingGoal();
        });

        totalIncomeLabel = new Label("Total Income: $0.00");
        totalMoneyLeftLabel = new Label("Money Left: $0.00");
        savingGoalLabel = new Label("Saving Goal: $0.00");

        VBox financeInfoBox = new VBox(10, incomeBox, expenseBox, savingGoalBox, totalIncomeLabel, totalMoneyLeftLabel, savingGoalLabel);
        financeInfoBox.setAlignment(Pos.CENTER);
        financeInfoBox.setPadding(new Insets(10));

        VBox centeredLayout = new VBox(100, topButtons, financeInfoBox);
        centeredLayout.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(centeredLayout);
        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root);
        primaryStage.setTitle("LifeSync - Finance Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showExpenses() {
        StringBuilder expensesText = new StringBuilder("Your Expenses:\n");
        for (String date : expenseHistory.keySet()) {
            String[] expenseData = expenseHistory.get(date);
            expensesText.append("Date: ").append(date).append(" - $")
                    .append(expenseData[0]).append(" - Description: ").append(expenseData[1]).append("\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Your Expenses");
        alert.setHeaderText(null);
        alert.setContentText(expensesText.toString());
        alert.showAndWait();
    }

    private VBox createSidebar(Stage primaryStage) {
        VBox sidebar = new VBox(30);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom right, #F7D1D7, #736DED); -fx-background-radius: 20px;");
        sidebar.setPrefWidth(250);
        sidebar.setAlignment(Pos.CENTER);

        Button homeButton = createSidebarButton("\uD83C\uDFE0 Home");
        Button moodButton = createSidebarButton("\uD83D\uDE0A Mood");
        Button sleepButton = createSidebarButton("\uD83D\uDECF Sleep");
        Button financeButton = createSidebarButton("\uD83D\uDCB0 Finance");

        homeButton.setOnAction(e -> new DashboardView().start(primaryStage));
        moodButton.setOnAction(e -> new MoodTrackerView().start(primaryStage));
        sleepButton.setOnAction(e -> new SleepTrackerView().start(primaryStage));
        financeButton.setOnAction(e -> new FinanceTrackerView().start(primaryStage));

        // Add hover effects to sidebar buttons
        addButtonHoverEffect(homeButton);
        addButtonHoverEffect(moodButton);
        addButtonHoverEffect(sleepButton);
        addButtonHoverEffect(financeButton);

        sidebar.getChildren().addAll(homeButton, moodButton, sleepButton, financeButton);
        return sidebar;
    }

    private HBox createInputBox(String prompt, String buttonText, AmountHandler handler) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMinWidth(250);
        Button button = createHeaderButton(buttonText);
        
        // Add hover effect to the input box button
        addButtonHoverEffect(button);
        
        button.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(field.getText());
                handler.handle(amount);
                field.clear();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });
        box.getChildren().addAll(field, button);
        return box;
    }

    private HBox createExpenseInputBox() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter Expense Amount");
        amountField.setMinWidth(150);

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter Expense Description");
        descriptionField.setMinWidth(150);

        Button saveButton = createHeaderButton("Save Expense");
        
        // Add hover effect to the save button
        addButtonHoverEffect(saveButton);
        
        saveButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText().isEmpty() ? "No description" : descriptionField.getText();
                String currentDate = getCurrentDate();
                totalExpenses += amount;
                expenseHistory.put(currentDate, new String[]{String.valueOf(amount), description});
                totalIncome -= amount;
                amountField.clear();
                descriptionField.clear();
                updateLabels();
                checkMoneyLeft();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });

        box.getChildren().addAll(amountField, descriptionField, saveButton);
        return box;
    }

    private void updateLabels() {
        totalIncomeLabel.setText("Total Income: $" + totalIncome);
        totalMoneyLeftLabel.setText("Money Left: $" + (totalIncome - totalExpenses));
    }

    private void checkSavingGoal() {
        if ((totalIncome - totalExpenses) < savingGoal) {
            showAlert("Saving Goal Alert", "You have not reached your saving goal!");
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

    private Button createSidebarButton(String text) {
        return createStyledButton(text, 120);
    }

    private Button createHeaderButton(String text) {
        return createStyledButton(text, 220);
    }

    private Button createStyledButton(String text, int width) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;");
        button.setPrefWidth(width);
        return button;
    }

    private void addButtonHoverEffect(Button button) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #7A50C4; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;"));

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

    @FunctionalInterface
    interface AmountHandler {
        void handle(double amount);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
