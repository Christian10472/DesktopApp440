package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
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

public class SignUpController {

    private boolean verified;
    private Stage stage;
    private Scene scene;
    @FXML
    private Label firstNameText,
            usernameText,
            emailText,
            passwordText;
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
    private Button signUpButton;
    @FXML
    private Button logInButton;

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
        verified = true;
        //Checking if any parameters are null
        isUserPropmptEmpty();
        doPasswordsMatch();
        //checking if parameters are valid to database
        if (verified) {
            doesUserExist();
            if (verified) {
                addUser();
                onSuccessSignUpButtonClick(event);
            }

        }

    }

    public void isUserPropmptEmpty() {
        firstNameText.setText("");
        usernameText.setText("");
        emailText.setText("");
        passwordText.setText("");
        if ((isTextFieldEmpty(firstNameField)) || (isTextFieldEmpty(lastNameField))) {
            firstNameText.setText("*Please enter a valid name");
        }
        if (isTextFieldEmpty(usernameField)) {
            usernameText.setText("*Please enter a valid Username");
        }
        if (isTextFieldEmpty(emailField)) {
            emailText.setText("*Please enter a valid email address");
        }
        if (isTextFieldEmpty(passwordField)) {
            passwordText.setText("*Please enter a valid email password");
        }
    }

    public boolean isTextFieldEmpty(TextField x) {
        if (x.getText().isEmpty()) {
            verified = false;
            return true;
        }
        return false;
    }

    public void doesUserExist() {
        try {
            Connection dBConnection =
                    new UsersDatabase().getDatabaseConnection();

            String SQL = "SELECT * FROM Users WHERE Username = ?;";
            PreparedStatement verifyUser = dBConnection.prepareStatement(SQL);
            verifyUser.setString(1, usernameField.getText());
            ResultSet usersResultSet = verifyUser.executeQuery();

            if (usersResultSet.next()) {
                usernameText.setText("*Username is already taken");
                verified = false;
            }

            SQL = "SELECT * FROM Users WHERE Email = ?;";
            verifyUser = dBConnection.prepareStatement(SQL);
            verifyUser.setString(1, emailField.getText());
            usersResultSet = verifyUser.executeQuery();
            if (usersResultSet.next()) {
                emailText.setText("*Email is already in use");
                verified = false;
            }
        } catch (Exception e) {
            System.out.println("Error connecting to database");
            System.out.println(e.getMessage());
            verified = false;
        }
    }

    public void doPasswordsMatch() {
        if (!(passwordField.getText()).equals(confirmPasswordField.getText())) {
            passwordText.setText("*Passwords do not match");
            verified = false;
        }
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
