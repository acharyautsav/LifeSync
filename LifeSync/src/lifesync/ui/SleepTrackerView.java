package lifesync.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class SleepTrackerView extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(900, 600);

        // Sidebar Gradient with a soft tone
        LinearGradient sidebarGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#F7D1D7")), new Stop(1, Color.web("#736DED")));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #F7D1D7, #736DED);");

        // Sidebar with smooth gradient and rounded corners
        VBox sidebar = new VBox(30);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom right, #F7D1D7, #736DED); " +
                "-fx-background-radius: 20px;");
        sidebar.setPrefWidth(250);
        sidebar.setAlignment(Pos.CENTER);

        Button homeButton = createSidebarButton("🏠 Home");
        Button moodButton = createSidebarButton("😊 Mood");
        Button sleepButton = createSidebarButton("🛏 Sleep");
        Button financeButton = createSidebarButton("💰 Finance");

        homeButton.setOnAction(e -> new DashboardView().start(primaryStage));
        moodButton.setOnAction(e -> new MoodTrackerView().start(primaryStage));
        sleepButton.setOnAction(e -> new SleepTrackerView().start(primaryStage));
        financeButton.setOnAction(e -> new FinanceTrackerView().start(primaryStage));

        sidebar.getChildren().addAll(homeButton, moodButton, sleepButton, financeButton);

        // Main Content area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: #EADEF9; -fx-background-radius: 20px;"); //rounding

        Button profileButton = createProfileButton();
        Button trackSleepButton = createHeaderButton("Track your sleep hours");

        profileButton.setOnAction(e -> new MyProfileView().start(primaryStage));
        trackSleepButton.setOnAction(e -> trackSleepHours(primaryStage));

        HBox topButtons = new HBox(20, profileButton, trackSleepButton);
        topButtons.setAlignment(Pos.CENTER);

        // Sleep Tracker Section
        Label sleepLabel = new Label("Track Your Sleep");
        sleepLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:#FCFCFC;");

        // Sleep Input Section
        Label sleepTimeLabel = new Label("Enter Sleep Time (HH:MM AM/PM): ");
        TextField sleepTimeInput = new TextField();
        sleepTimeInput.setPromptText("Enter sleep time");
        sleepTimeInput.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10px; -fx-border-color: #D0D0D0; -fx-padding: 10px;");

        Label wakeupTimeLabel = new Label("Enter Wakeup Time (HH:MM AM/PM): ");
        TextField wakeupTimeInput = new TextField();
        wakeupTimeInput.setPromptText("Enter wakeup time");
        wakeupTimeInput.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10px; -fx-border-color: #D0D0D0; -fx-padding: 10px;");

        // Add button to pick current time
        Button pickSleepTimeButton = new Button("Pick Sleep Time");
        Button pickWakeupTimeButton = new Button("Pick Wakeup Time");

        pickSleepTimeButton.setOnAction(e -> showTimePicker(primaryStage, sleepTimeInput));
        pickWakeupTimeButton.setOnAction(e -> showTimePicker(primaryStage, wakeupTimeInput));
        
        pickSleepTimeButton.setStyle("-fx-background-color: #B8A0D5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; " +
                "-fx-background-radius: 10px;");
        pickWakeupTimeButton.setStyle("-fx-background-color: #B8A0D5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; " +
                "-fx-background-radius: 10px;");

        Label totalSleepLabel = new Label("Total Sleep Duration: ");
        Label sleepDurationDisplay = new Label("00 hours 00 minutes 00 seconds");
        sleepDurationDisplay.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label warningLabel = new Label("");
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        Button logSleepButton = new Button("Log Sleep");
        logSleepButton.setStyle("-fx-background-color: #B8A0D5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; " +
                "-fx-background-radius: 10px;");
        logSleepButton.setOnAction(e -> {
            String sleepTimeStr = sleepTimeInput.getText();
            String wakeupTimeStr = wakeupTimeInput.getText();

            logSleepDuration(sleepTimeStr, wakeupTimeStr, totalSleepLabel, warningLabel);
        });

        VBox sleepBox = new VBox(10, sleepLabel, sleepTimeLabel, sleepTimeInput, pickSleepTimeButton,
                wakeupTimeLabel, wakeupTimeInput, pickWakeupTimeButton, totalSleepLabel, sleepDurationDisplay,
                warningLabel, logSleepButton);
        sleepBox.setPadding(new Insets(15));
        sleepBox.setStyle("-fx-background-color: #9E6FDF; -fx-padding: 15px; -fx-border-radius: 10px; -fx-border-color: white;");
        sleepBox.setMinWidth(300);

        mainContent.getChildren().addAll(topButtons, sleepBox);

        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root);
        primaryStage.setTitle("LifeSync - Sleep Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showTimePicker(Stage primaryStage, TextField timeInput) {
        // Create a new time picker dialog
        Dialog<LocalTime> timeDialog = new Dialog<>();
        timeDialog.setTitle("Pick Time");
        timeDialog.setHeaderText("Select a time");

        // Add OK and Cancel buttons
        ButtonType pickButton = new ButtonType("Pick", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        timeDialog.getDialogPane().getButtonTypes().addAll(pickButton, cancelButton);

        // Layout for the time picker
        HBox timePickerBox = new HBox(10);
        timePickerBox.setAlignment(Pos.CENTER);

        // Spinner for selecting hours (1-12 for 12-hour format)
        Spinner<Integer> hourSpinner = new Spinner<>(1, 12, 12);
        hourSpinner.setEditable(true);

        // Spinner for selecting minutes (0-59)
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        minuteSpinner.setEditable(true);

        // Spinner for AM/PM selection
        Spinner<String> amPmSpinner = new Spinner<>();
        SpinnerValueFactory<String> amPmFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList("AM", "PM"));
        amPmSpinner.setValueFactory(amPmFactory);
        amPmSpinner.setEditable(false); // AM/PM does not need to be typed manually

        // Add components to the layout
        timePickerBox.getChildren().addAll(
            new Label("Hour:"), hourSpinner,
            new Label("Minute:"), minuteSpinner,
            new Label("AM/PM:"), amPmSpinner
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


    private void trackSleepHours(Stage primaryStage) {
        // Show the tracked sleep time, wake-up time, and the date
        Alert sleepInfoAlert = new Alert(Alert.AlertType.INFORMATION);
        sleepInfoAlert.setTitle("Sleep Details");

        // Current Date
        LocalDate currentDate = LocalDate.now();

        // Set up the message
        String message = "Date: " + currentDate.toString() + "\n";
        message += "Sleep Time: 10:30 PM\n";  // Replace with actual input data
        message += "Wakeup Time: 06:30 AM\n";  // Replace with actual input data
        message += "Total Sleep Duration: 8 hours 00 minutes";  // Replace with actual calculated value

        sleepInfoAlert.setHeaderText("Your Sleep Details");
        sleepInfoAlert.setContentText(message);

        // Show the alert
        sleepInfoAlert.showAndWait();
    }

    private void logSleepDuration(String sleepTimeStr, String wakeupTimeStr, Label sleepDurationLabel, Label warningLabel) {
        try {
            // Parse sleep and wakeup time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime sleepTime = LocalTime.parse(sleepTimeStr, formatter);
            LocalTime wakeupTime = LocalTime.parse(wakeupTimeStr, formatter);

            // Calculate the total sleep duration
            java.time.Duration duration = java.time.Duration.between(sleepTime, wakeupTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.getSeconds()% 60;

            // Display total sleep duration
            sleepDurationLabel.setText(String.format("Total Sleep Duration: %02d hours %02d minutes %02d seconds", hours, minutes, seconds));

            // Warning for less than 8 hours
            if (hours < 8) {
                warningLabel.setText("Warning: Sleep is less than 8 hours!");
            } else {
                warningLabel.setText("");
            }
        } catch (Exception e) {
            sleepDurationLabel.setText("Invalid time format");
            warningLabel.setText("");
        }
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;");
        button.setPrefWidth(120);
        addButtonHoverEffect(button);
        return button;
    }

    private Button createProfileButton() {
        Button profileButton = new Button("👤 Profile");
        profileButton.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;");
        profileButton.setPrefWidth(220);
        addButtonHoverEffect(profileButton);
        return profileButton;
    }

    private Button createHeaderButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;");
        addButtonHoverEffect(button);
        return button;
    }

    private void addButtonHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
            button.setStyle("-fx-background-color: #7A4E9F; -fx-font-size: 16px; -fx-font-weight: bold; " +
                    "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;");
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
            button.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 16px; -fx-font-weight: bold; " +
                    "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
