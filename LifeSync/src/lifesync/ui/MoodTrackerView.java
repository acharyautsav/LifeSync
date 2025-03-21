package lifesync.ui;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MoodTrackerView extends Application {

    private Map<String, LocalDate> recordedMoods;
    private Label selectedMoodLabel;
    private String selectedMood;
    
    @Override
    public void start(Stage primaryStage) {	
        recordedMoods = new HashMap<>();
        selectedMood = "";

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

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: #EADEF9; -fx-background-radius: 20px;"); //rounding

        // Top Buttons (Profile & Past Moods)
        Button profileButton = createProfileButton();
        Button pastMoodsButton = createHeaderButton("View Past Moods");

        profileButton.setOnAction(e -> new MyProfileView().start(primaryStage));
        pastMoodsButton.setOnAction(e -> showPastMoods(primaryStage));
        
        HBox topButtons = new HBox(20, profileButton, pastMoodsButton);
        topButtons.setAlignment(Pos.CENTER);

        // Mood Buttons Section (Vertical Layout)
        VBox moodButtons = createMoodButtons();
        

        selectedMoodLabel = new Label("No mood selected");
        selectedMoodLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6E6E6E;");

        Button saveMoodButton = new Button("Save Mood");
        saveMoodButton.setStyle("-fx-background-color: #B8A0D5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; " +
                "-fx-background-radius: 10px;");
        saveMoodButton.setOnAction(e -> saveMood());

        VBox moodSection = new VBox(20, moodButtons, selectedMoodLabel, saveMoodButton);
        moodSection.setPadding(new Insets(100));
        moodSection.setAlignment(Pos.CENTER);

        mainContent.getChildren().addAll(topButtons, moodSection);
        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root);
        primaryStage.setTitle("LifeSync - Mood Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveMood() {
        if (!selectedMood.isEmpty()) {
            recordedMoods.put(selectedMood, LocalDate.now());
            selectedMoodLabel.setText("Mood saved: " + selectedMood + " on " + LocalDate.now());
        } else {
            selectedMoodLabel.setText("No mood selected to save!");
        }
    }

    private VBox createMoodButtons() {
        VBox moodButtons = new VBox(20);
        moodButtons.setAlignment(Pos.CENTER);

        // Create the buttons
        Button happyButton = new Button("😊 Happy");
        Button sadButton = new Button("😔 Sad");
        Button neutralButton = new Button("😐 Neutral");
        Button angryButton = new Button("😡 Angry");
        Button relaxedButton = new Button("😌 Relaxed");

        // Style the buttons
        styleMoodButton(happyButton);
        styleMoodButton(sadButton);
        styleMoodButton(neutralButton);
        styleMoodButton(angryButton);
        styleMoodButton(relaxedButton);

        // Set the action for each button
        happyButton.setOnAction(e -> selectMood("Happy"));
        sadButton.setOnAction(e -> selectMood("Sad"));
        neutralButton.setOnAction(e -> selectMood("Neutral"));
        angryButton.setOnAction(e -> selectMood("Angry"));
        relaxedButton.setOnAction(e -> selectMood("Relaxed"));

        // Organize buttons in rows
        HBox firstRow = new HBox(20, happyButton, sadButton);
        firstRow.setAlignment(Pos.CENTER);

        HBox secondRow = new HBox(20, neutralButton, angryButton);
        secondRow.setAlignment(Pos.CENTER);

        HBox thirdRow = new HBox(20, relaxedButton);
        thirdRow.setAlignment(Pos.CENTER);

        // Add rows to VBox
        moodButtons.getChildren().addAll(firstRow, secondRow, thirdRow);

        return moodButtons;
    }

    private void selectMood(String mood) {
        selectedMood = mood;
        selectedMoodLabel.setText("Selected mood: " + selectedMood);
    }

    private void styleMoodButton(Button button) {
        button.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;");
        button.setPrefWidth(140);
        addButtonHoverEffect(button);
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
        button.setPrefWidth(220);
        addButtonHoverEffect(button);
        return button;
    }

    private void showPastMoods(Stage primaryStage) {
        Stage pastMoodsStage = new Stage();
        pastMoodsStage.setTitle("Past Moods");

        VBox pastMoodsContainer = new VBox(10);
        pastMoodsContainer.setPadding(new Insets(15));
        pastMoodsContainer.setStyle("-fx-background-color: #DDE6FF; -fx-background-radius: 20px;");

        for (Map.Entry<String, LocalDate> entry : recordedMoods.entrySet()) {
            Label pastMoodLabel = new Label(entry.getKey() + " on " + entry.getValue());
            pastMoodLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #6E6E6E;");
            pastMoodsContainer.getChildren().add(pastMoodLabel);
        }

        Scene pastMoodsScene = new Scene(pastMoodsContainer, 400, 400);
        pastMoodsStage.setScene(pastMoodsScene);
        pastMoodsStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
