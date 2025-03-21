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

public class DashboardView extends Application {

    private VBox pendingTasksContainer;
    private Map<String, LocalDate> completedTasks;

    @Override
    public void start(Stage primaryStage) {
    	
        completedTasks = new HashMap<>();

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
        Button profileButton = createProfileButton();
        Button completedTasksButton = createHeaderButton("Your Completed Tasks");

        profileButton.setOnAction(e -> new MyProfileView().start(primaryStage));
        completedTasksButton.setOnAction(e -> showCompletedTasks(primaryStage));

        HBox topButtons = new HBox(20, profileButton, completedTasksButton);
        topButtons.setAlignment(Pos.CENTER);

        // Task Input Section
        Label taskListLabel = new Label("Create your list");
        taskListLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:#FCFCFC;");

        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter a task");
        taskInput.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10px; -fx-border-color: #D0D0D0; -fx-padding: 10px;");

        Button addTaskButton = new Button("Add Task");
        addTaskButton.setStyle("-fx-background-color: #B8A0D5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; " +
                "-fx-background-radius: 10px;");

        pendingTasksContainer = new VBox(10);

        addTaskButton.setOnAction(e -> addTask(taskInput.getText()));

        VBox taskListBox = new VBox(10, taskListLabel, taskInput, addTaskButton);
        taskListBox.setPadding(new Insets(15));
        taskListBox.setStyle("-fx-background-color: #9E6FDF; -fx-padding: 15px; -fx-border-radius: 10px; -fx-border-color: white;");
        taskListBox.setMinWidth(300);

        // Pending Tasks Section
        Label pendingTasksLabel = new Label("Your Pending Tasks");
        pendingTasksLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FCFCFC;");

        VBox pendingTasksBox = new VBox(10, pendingTasksLabel, pendingTasksContainer);
        pendingTasksBox.setPadding(new Insets(15));
        pendingTasksBox.setStyle("-fx-background-color:  #9E6FDF; -fx-padding: 15px; -fx-border-radius: 10px; -fx-border-color: white");
        pendingTasksBox.setMinWidth(300);

        HBox taskSections = new HBox(20, taskListBox, pendingTasksBox);
        taskSections.setAlignment(Pos.CENTER);

        mainContent.getChildren().addAll(topButtons, taskSections);
        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root);
        primaryStage.setTitle("LifeSync - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addTask(String task) {
        if (!task.isEmpty()) {
            CheckBox pendingCheckBox = new CheckBox(task);
            pendingCheckBox.setOnAction(e -> handleTaskCompletion(pendingCheckBox));
            pendingTasksContainer.getChildren().add(pendingCheckBox);
        }
    }

    private void handleTaskCompletion(CheckBox taskCheckBox) {
        if (taskCheckBox.isSelected()) {
            completedTasks.put(taskCheckBox.getText(), LocalDate.now());
        } else {
            completedTasks.remove(taskCheckBox.getText());
        }
    }

    private void showCompletedTasks(Stage primaryStage) {
        Stage completedTasksStage = new Stage();
        completedTasksStage.setTitle("Completed Tasks");

        VBox completedTasksContainer = new VBox(10);
        completedTasksContainer.setPadding(new Insets(15));
        completedTasksContainer.setStyle("-fx-background-color: #DDE6FF; -fx-background-radius: 20px;");

        for (Map.Entry<String, LocalDate> entry : completedTasks.entrySet()) {
            Label taskLabel = new Label(entry.getKey() + " - Completed on: " + entry.getValue());
            taskLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: normal; -fx-text-fill: #6E6E6E;");
            completedTasksContainer.getChildren().add(taskLabel);
        }

        Scene scene = new Scene(completedTasksContainer, 400, 300);
        completedTasksStage.setScene(scene);
        completedTasksStage.show();
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

    public static void main(String[] args) {
        launch(args);
    }
}
