package lifesync.ui;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MyProfileView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Profile");

        // Labels for user details
        Label nameLabel = new Label("Name: ");
        Label usernameLabel = new Label("Username: ");
        Label emailLabel = new Label("Email: ");

        Label nameValue = new Label("[User's Name]"); // Fetch from database
        Label usernameValue = new Label("[User's Username]"); // Fetch from database
        Label emailValue = new Label("[User's Email]"); // Fetch from database

        // Grid for user details
        GridPane profileGrid = new GridPane();
        profileGrid.setPadding(new Insets(20));
        profileGrid.setHgap(10);
        profileGrid.setVgap(10);
        profileGrid.add(nameLabel, 0, 0);
        profileGrid.add(nameValue, 1, 0);
        profileGrid.add(usernameLabel, 0, 1);
        profileGrid.add(usernameValue, 1, 1);
        profileGrid.add(emailLabel, 0, 2);
        profileGrid.add(emailValue, 1, 2);

        // Change Password Section
        Label changePassLabel = new Label("Change Password");
        PasswordField oldPassField = new PasswordField();
        oldPassField.setPromptText("Old Password");
        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("New Password");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm New Password");
        Button submitChangeButton = createStyledButton("Submit");

        // Handle password change
        submitChangeButton.setOnAction(e -> {
            String oldPassword = oldPassField.getText();
            String newPassword = newPassField.getText();
            String confirmPassword = confirmPassField.getText();

            // Check if all fields are filled
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Please fill all the fields.", Alert.AlertType.WARNING);
                return;
            }

            // Check if new password and confirm password match
            if (!newPassword.equals(confirmPassword)) {
                showAlert("Your passwords do not match.", Alert.AlertType.ERROR);
                return;
            }

            // Check if the old password is correct (this is just a mock; implement your database logic)
            if (!oldPassword.equals("[CorrectOldPassword]")) { // Replace with actual validation logic
                showAlert("Wrong old password.", Alert.AlertType.ERROR);
                return;
            }

            // Success message (this should include actual password update logic in your system)
            showAlert("Password changed successfully!", Alert.AlertType.INFORMATION);
        });

        // Hide password change fields initially
        VBox passBox = new VBox(10, oldPassField, newPassField, confirmPassField, submitChangeButton);
        passBox.setAlignment(Pos.CENTER);
        passBox.setPadding(new Insets(20));
        passBox.setVisible(false); // Initially hidden

        // Button to reveal change password fields
        Button revealChangePassButton = createStyledButton("Change Password");
        revealChangePassButton.setOnAction(e -> {
            passBox.setVisible(true); // Show password change fields
        });

        // Log Out Button
        Button logOutButton = createStyledButton("Log Out");
        logOutButton.setOnAction(e -> new LoginView().start(primaryStage));

        // Back Button
        Button backButton = createStyledButton("Back to Home");
        backButton.setOnAction(e -> {
            // TODO: Navigate back to Dashboard
            System.out.println("Back to Dashboard logic here");
        });

        // Top section with "Your Profile" and buttons (Back to Home & Log Out)
        Label profileTitle = new Label("Your Profile");
        profileTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox topButtonsBox = new HBox(20, backButton, logOutButton);
        topButtonsBox.setAlignment(Pos.CENTER);
        topButtonsBox.setPadding(new Insets(10));
        
        backButton.setOnAction(e -> new DashboardView().start(primaryStage));

        VBox mainLayout = new VBox(20, profileTitle, topButtonsBox, profileGrid, revealChangePassButton, passBox);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout, 400, 500);  // Increased height to fit everything comfortably
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to show alert messages
    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Password Change");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Create styled buttons to match the style provided
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #736DED; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: 12px; " +  // Reduced font size
            "-fx-padding: 8px 16px; " +  // Reduced padding for smaller button size
            "-fx-border-radius: 8px; " + // Rounded corners
            "-fx-border-color: #98A8FF; " +
            "-fx-border-width: 2px;"
        );

        // Allow buttons to expand or shrink depending on the available space
        HBox.setHgrow(button, Priority.ALWAYS);

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #98A8FF; " + // Darker color on hover
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +  // Maintain smaller font size on hover
            "-fx-padding: 8px 16px; " +  // Maintain reduced padding
            "-fx-border-radius: 8px; " +
            "-fx-border-color: #F1F0F9; " +
            "-fx-border-width: 2px;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #736DED; " + // Reset to original color
            "-fx-text-fill: black; " +
            "-fx-font-size: 12px; " +  // Maintain smaller font size on hover reset
            "-fx-padding: 8px 16px; " +  // Maintain reduced padding
            "-fx-border-radius: 8px; " +
            "-fx-border-color: #98A8FF; " +
            "-fx-border-width: 2px;"
        ));

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
