package com.musicplayer.musicplayer;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

	
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/musicplayer";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static volatile boolean songChanged = false;
    
    private static Stage loginStage;
    
    
    
    public void initialize() {
        // Check if the user is already authenticated (e.g., if the session is still valid)
        if (authenticateUser("previouslyLoggedInUser", "previouslyLoggedInPassword")) {
            openMainPage();
        }
    }

    
    /******************************************LOGIN***********************************************************/
    @FXML
    private void loginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Authenticate user using database
        if (authenticateUser(username, password)) {
            openMainPage();
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // User exists if there's a matching record
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String content) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /*** @param loginStage2 ***************************DEAMON*******************************************************/
    private void startDaemonThread(Stage loginStage2) {
    	Stage l = loginStage2;
        Thread daemonThread = new Thread(() -> {
            while (true) {
                // Check if the song has been changed
                if (songChanged) {
                    // Run the UI update on the JavaFX application thread
                    Platform.runLater(() -> {
                        try {
                            // Load the Main.fxml again
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
                            Scene scene = new Scene(fxmlLoader.load());

                            Stage newStage = new Stage();
                            newStage.getIcons().add(new Image(new File("src/main/java/images/logo.png").toURI().toString()));
                            newStage.setScene(scene);
                            newStage.show();

                            // Close the previous login stage if not null
                            
                                l.close();
                            

                            // Update the login stage reference
                            loginStage = newStage;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    // Reset the flag
                    songChanged = false;
                }

                // Sleep for some time before checking again
                try {
                    Thread.sleep(1000); // Adjust the sleep time as needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Set the thread as daemon
        daemonThread.setDaemon(true);

        // Start the daemon thread
        daemonThread.start();
    }

    
    /************************************************************************************/
    @FXML
    private void signUpButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if the username and password are not empty
        if (!username.isEmpty() && !password.isEmpty()) {
            // Check if the username already exists
            if (usernameExists(username)) {
                showAlert("Sign Up Failed", "Username already exists. Please choose another username.");
            } else {
                // Perform sign-up and authentication
                if (signUpUser(username, password)) {
                    showAlert("Sign Up Successful", "Account created successfully!");
                    openMainPage();
                } else {
                    showAlert("Sign Up Failed", "Error creating the account. Please try again.");
                }
            }
        } else {
            showAlert("Sign Up Failed", "Enter both username and password. Please try again.");
        }
    }


    private boolean signUpUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean usernameExists(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // User exists if there's a matching record
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void openMainPage() {
    	Stage loginStage = (Stage) usernameField.getScene().getWindow();
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
    	
    	
        Scene scene;
		try {
			scene = new Scene(fxmlLoader.load());
			loginStage.getIcons().add(new Image(new File("src/main/java/images/logo.png").toURI().toString()));

			startDaemonThread(loginStage);
	        loginStage.setScene(scene);
	        loginStage.show();
	        
	        
	        
	        loginStage.setOnCloseRequest(windowEvent -> {
	            Platform.exit();
	            System.exit(0);
	        });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       
		
		
	    


        
    }
}
