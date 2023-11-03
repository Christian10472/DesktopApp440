/**
 * Mathew Nuval
 * Anthony Plasencia
 * Christian Perez
 * Professor Ebrahimi
 * Comp 440
 * 2023 November 3
 */

package edu.csun.desktopapp440.controllers;

import edu.csun.desktopapp440.database.UsersDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class SignUpController {
    /**
     * Log variable
     */
    private static final Logger log;

    /**
     * Logger for debugging
     */
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(SignUpController.class.getName());
    }


    private boolean verified;
    private Stage stage;
    private Scene scene;
    @FXML
    private Label firstNameLabel,
            lastNameLabel,
            usernameLabel,
            emailLabel,
            passwordLabel;
    @FXML
    private TextField firstNameField,
            lastNameField,
            usernameField,
            emailField,
            passwordField,
            confirmPasswordField;
    @FXML
    private CheckBox termsCheckBox;
    @FXML
    private Button signUpButton,
            logInButton;
    @FXML
    protected void onTermsCheckBoxClick() {
        if (termsCheckBox.isSelected()) {
            signUpButton.setDisable(false);
        } else {
            signUpButton.setDisable(true);
        }
    }

    @FXML
    protected void onLogInPageButtonClick(ActionEvent event) throws IOException {
        URL logInUrl = getClass().getResource("/templates/LogIn.fxml");
        if (logInUrl == null) {
            throw new RuntimeException("Missing login resource url");
        }
        Parent root = FXMLLoader.load(logInUrl);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onSignUpButtonClick(ActionEvent event) {
        //Verify user inputs for sign up
        if (verifyNewUser()) {
            addUser();
            onSuccessSignUpButtonClick(event);
        }
    }

    public boolean verifyNewUser() {
        boolean results = true;
        if (isUserPromptEmpty()) {
            results = false;
        }
        if (!doPasswordsMatch()) {
            passwordLabel.setText("* Password do not match");
            results = false;
        }
        if (doesUserExist()) {
            results = false;
        }
        return results;
    }

    public boolean isUserPromptEmpty() {
        boolean results = false;
        HashMap<TextField, Label> userFields = new HashMap<>();
        userFields.put(firstNameField, firstNameLabel);
        userFields.put(lastNameField, lastNameLabel);
        userFields.put(usernameField, usernameLabel);
        userFields.put(emailField, emailLabel);
        userFields.put(passwordField, passwordLabel);
        HashMap<Label, String> userLabels = new HashMap<>();
        userLabels.put(firstNameLabel, "First Name");
        userLabels.put(lastNameLabel, "Last Name");
        userLabels.put(usernameLabel, "Username");
        userLabels.put(emailLabel, "Email");
        userLabels.put(passwordLabel, "Password");

        for(TextField userField : userFields.keySet()) {
            results = isTextFieldEmpty(userField);
            if (results) {
                userFields.get(
                        userField).setText(
                                String.format(
                                        "* Please enter %s",
                                        userLabels.get(userFields.get(userField))));
            }
        }
        return results;
    }

    public boolean isTextFieldEmpty(TextField x) {
        return x.getText().isEmpty();
    }

    public boolean doesUserExist() {
        try {
            Connection dBConnection =
                    new UsersDatabase().getDatabaseConnection();

            final String checkUsername = "SELECT * FROM Users WHERE Username = ? OR Email = ?";
            PreparedStatement verifyUser = dBConnection.prepareStatement(checkUsername);
            verifyUser.setString(1, usernameField.getText());
            verifyUser.setString(2, emailField.getText());
            ResultSet usersResultSet = verifyUser.executeQuery();
            if (usersResultSet.next()) {
                if (usersResultSet.getString(
                        "Username").equalsIgnoreCase(
                        usernameField.getText())) {
                    usernameLabel.setText("* Username is already taken");
                }
                if (usersResultSet.getString(
                        "Email").equalsIgnoreCase(
                        emailField.getText())) {
                    emailLabel.setText("* Email is already in use");
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Error connecting to database: %s",e.getMessage()));
        }
        return false;
    }

    public boolean doPasswordsMatch() {
        return passwordField.getText().equals(confirmPasswordField.getText());
    }

    public void addUser() {
        try {
            Connection dBConnection = new UsersDatabase().getDatabaseConnection();
            String sql = "INSERT INTO " +
                    "Users(Username, Password, FirstName, LastName, Email) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement newUserValues = dBConnection.prepareStatement(sql);
            newUserValues.setString(1, usernameField.getText());
            newUserValues.setString(2, passwordField.getText());
            newUserValues.setString(3, firstNameField.getText());
            newUserValues.setString(4, lastNameField.getText());
            newUserValues.setString(5, emailField.getText());
            newUserValues.executeUpdate();

            dBConnection.close();

        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format(
                            "Error inserting into table: %s",
                            e.getMessage()));
        }
    }

    public void onSuccessSignUpButtonClick(ActionEvent event) {
        try {
            URL resourseUrl = getClass().getResource("/templates/SignUpSuccess.fxml");
            if (resourseUrl == null) {
                throw new NullPointerException("Missing resources on: SignUpSuccess.fxml");
            }
            Parent root = FXMLLoader.load(resourseUrl);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Error loading resources: %s",e.getMessage()));
        }
    }
}
