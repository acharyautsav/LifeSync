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
import javafx.util.Duration;
import javafx.animation.ScaleTransition;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DashboardViewAdmin extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the main layout for the admin dashboard
        BorderPane root = new BorderPane();
        root.setPrefSize(900, 600);

        // Sidebar Gradient with a soft tone
        LinearGradient sidebarGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#F7D1D7")), new Stop(1, Color.web("#736DED")));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #F7D1D7, #736DED);");

        // Sidebar with smooth gradient and rounded corners
        VBox sidebar = new VBox(30); // Increased spacing for better alignment
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom right, #F7D1D7, #736DED); " +
                "-fx-background-radius: 20px;");
        sidebar.setPrefWidth(200);
        sidebar.setAlignment(Pos.CENTER);

        Button viewUsersButton = createSidebarButton("👥 View Users");
        Button deleteUserButton = createSidebarButton("🗑 Delete User");
        Button changePasswordButton = createSidebarButton("🔑 Manage User Info");

        // Set actions for the buttons
        viewUsersButton.setOnAction(e -> showUserList());
        deleteUserButton.setOnAction(e -> deleteUser());
        changePasswordButton.setOnAction(e -> changeUserInfo());

        sidebar.getChildren().addAll(viewUsersButton, deleteUserButton, changePasswordButton);

        // Main Content area (view users, delete user, change password)
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: #EADEF9; -fx-background-radius: 20px;");

        // Create Scene
        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root);
        primaryStage.setTitle("LifeSync - Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #9E6FDF; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 20px; -fx-background-radius: 20px; -fx-text-fill: white;");
        button.setPrefWidth(120);
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

    private void showUserList() {
        // Create a new window to show a list of users (view users information)
        Stage userListStage = new Stage();
        userListStage.setTitle("User List");

        VBox userListContainer = new VBox(10);
        userListContainer.setPadding(new Insets(15));
        userListContainer.setStyle("-fx-background-color: #DDE6FF; -fx-background-radius: 20px;");

        // This is just a sample list of users. In a real application, fetch this data from a database.
        String[] users = {"User1: user1@example.com", "User2: user2@example.com", "User3: user3@example.com"};

        for (String user : users) {
            Label userLabel = new Label(user);
            userListContainer.getChildren().add(userLabel);
        }

        Scene scene = new Scene(userListContainer, 400, 300);
        userListStage.setScene(scene);
        userListStage.show();
    }

    private void deleteUser() {
        // Create a window for deleting a user
        Stage deleteUserStage = new Stage();
        deleteUserStage.setTitle("Delete User");

        VBox deleteUserContainer = new VBox(10);
        deleteUserContainer.setPadding(new Insets(15));
        deleteUserContainer.setStyle("-fx-background-color: #DDE6FF; -fx-background-radius: 20px;");

        TextField userEmailInput = new TextField();
        userEmailInput.setPromptText("Enter user email to delete");

        Button deleteButton = new Button("Delete User");
        deleteButton.setOnAction(e -> {
            // In a real application, delete the user from the database here
            String email = userEmailInput.getText();
            if (!email.isEmpty()) {
                // Code to delete user goes here
                System.out.println("User with email " + email + " deleted.");
            }
        });

        deleteUserContainer.getChildren().addAll(userEmailInput, deleteButton);

        Scene scene = new Scene(deleteUserContainer, 400, 200);
        deleteUserStage.setScene(scene);
        deleteUserStage.show();
    }

    private void changeUserInfo() {
        // Create a window to change user information (username, email, password)
        Stage changeUserInfoStage = new Stage();
        changeUserInfoStage.setTitle("Manage User Information");

        VBox changeUserInfoContainer = new VBox(10);
        changeUserInfoContainer.setPadding(new Insets(15));
        changeUserInfoContainer.setStyle("-fx-background-color: #DDE6FF; -fx-background-radius: 20px;");

        TextField userEmailInput = new TextField();
        userEmailInput.setPromptText("Enter user email");

        TextField newUsernameInput = new TextField();
        newUsernameInput.setPromptText("Enter new username");

        TextField newEmailInput = new TextField();
        newEmailInput.setPromptText("Enter new email");

        PasswordField newPasswordInput = new PasswordField();
        newPasswordInput.setPromptText("Enter new password");

        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.setOnAction(e -> {
            String email = userEmailInput.getText();
            String newUsername = newUsernameInput.getText();
            String newEmail = newEmailInput.getText();
            String newPassword = newPasswordInput.getText();

            if (!email.isEmpty() && !newUsername.isEmpty() && !newEmail.isEmpty() && !newPassword.isEmpty()) {
                // Hash the new password
                String hashedPassword = hashPassword(newPassword);

                // Code to save changes to the database goes here (username, email, password)
                System.out.println("User info changed: " + "Username: " + newUsername + ", Email: " + newEmail + ", Password: " + hashedPassword);

                // Show success message
                showSuccessPopup();
            }
        });

        changeUserInfoContainer.getChildren().addAll(userEmailInput, newUsernameInput, newEmailInput, newPasswordInput, saveChangesButton);

        Scene scene = new Scene(changeUserInfoContainer, 400, 250);
        changeUserInfoStage.setScene(scene);
        changeUserInfoStage.show();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showSuccessPopup() {
        // Create a success message popup
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Changes Saved Successfully!");
        alert.setContentText("The user information has been updated.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
