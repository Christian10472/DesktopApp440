package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
import com.example.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
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

public class LogInController {
    private String SelectStatement =
            "select * from users where Username = ? and Password = ?";
    private Users users;


    @FXML
    public Button logInButton;
    @FXML
    public  Button signUpButton;
    @FXML
    public TextField userNameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label errorLabel;


    @FXML
    public void onLogInButtonClick(ActionEvent event) {
        try {
            if (validateInput()) {
                URL homePageUrl = getClass().getResource("/templates/HomePage.fxml");
                if (homePageUrl == null) {
                    throw new NullPointerException("Missing resources on: HomePage.fxmll");
                }
                FXMLLoader loader = new FXMLLoader(homePageUrl);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                HomePageController controller = loader.getController();
                controller.initialiseHomepage(users);
                stage.show();
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            "Error loading HomePage.fxml: %s",
                            e.getMessage()));
        }
        errorLabel.setText("*Incorrect Username or Password*");
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
        Connection dBConnection = null;
        try {
            dBConnection = new UsersDatabase().getDatabaseConnection();

            ResultSet resultSet;
            PreparedStatement verifyLogin = dBConnection.prepareStatement(SelectStatement) ;

            String UsernameInput = userNameField.getText();
            String PasswordInput = passwordField.getText();

            verifyLogin.setString(1, UsernameInput);
            verifyLogin.setString(2, PasswordInput);
            resultSet = verifyLogin.executeQuery();

            if (resultSet.next()) {
                if( (UsernameInput.equals(resultSet.getString("Username"))) && PasswordInput.equals(resultSet.getString("Password"))) {

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