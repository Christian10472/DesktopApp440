package edu.csun.desktopapp440.controllers;

import edu.csun.desktopapp440.database.UsersDatabase;
import edu.csun.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class LogInController {

    /**
     * Log variable
     */
    private static final Logger log;

    /**
     * Logger for debugging
     */
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(LogInController.class.getName());
    }

    private Users users;
    @FXML
    public Button logInButton,
            signUpButton;
    @FXML
    public TextField userNameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label errorLabel;

    @FXML
    public void userLogInEvent(Event event) {
        try {
            if (validateInput()) {
                URL homePageUrl = getClass().getResource("/templates/HomePage.fxml");
                if (homePageUrl == null) {
                    throw new NullPointerException("Missing resources on: HomePage.fxmll");
                }
            }
            if (!validateInput()) {
                log.warning("Unable to validate inputs");
                errorLabel.setText("*Incorrect Username or Password*");
                return;
            }
            URL homePageUrl = getClass().getResource("/templates/HomePage.fxml");
            if (homePageUrl == null) {
                throw new NullPointerException("Missing resources on: HomePage.fxml");
            }
            FXMLLoader loader = new FXMLLoader(homePageUrl);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            HomePageController controller = loader.getController();
            controller.initialiseHomepage(users);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            "Error loading HomePage.fxml: %s",
                            e.getMessage()));
        }
    }

    @FXML
    public void onSignUpButtonClick(ActionEvent event) {
        try {
            URL signUpUrl = getClass().getResource("/templates/SignUp.fxml");
            if (signUpUrl == null) {
                throw new NullPointerException("Missing resources on: SignUp.fxml");
            }
            Parent root = FXMLLoader.load(signUpUrl);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            "Error loading SignUp.fxml: %s",
                            e.getMessage()));
        }
    }

    private boolean validateInput() {
        Connection dBConnection;
        try {
            dBConnection = new UsersDatabase().getDatabaseConnection();

            ResultSet resultSet;
            final String verifyUserCredentials = "select * from users where Username = ? and Password = ?";
            PreparedStatement verifyLogin = dBConnection.prepareStatement(verifyUserCredentials) ;

            String usernameInput = userNameField.getText();
            String passwordInput = passwordField.getText();

            verifyLogin.setString(1, usernameInput);
            verifyLogin.setString(2, passwordInput);
            resultSet = verifyLogin.executeQuery();

            if (resultSet.next()) {
                if( (usernameInput.equals(resultSet.getString("Username"))) && passwordInput.equals(resultSet.getString("Password"))) {

                    users = new Users();
                    users.setUsername(resultSet.getString(("Username")));
                    users.setPassword(resultSet.getString("Password"));
                    users.setFirstName(resultSet.getString("FirstName"));
                    users.setLastName(resultSet.getString("LastName"));
                    users.setEmail(resultSet.getString("Email"));
                    return true;
                }
            }
            dBConnection.close();

        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("SQL Error: %s", e.getMessage()));
        }

        return false;
    }

}