package com.example.desktopapp440.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class HomePageController {


    com.example.desktopapp440.objects.Users Users;
    @FXML
    private Label UsernameLabel;
    @FXML
    private Label PasswordLabel;
    @FXML
    private Label FirstNameLabel;
    @FXML
    private Label LastNameLabel;
    @FXML
    private Label EmailLabel;
    @FXML
    private Button logOutButton;

    public void initialiseHomepage(com.example.desktopapp440.objects.Users users) {
        Users = users;
        UsernameLabel.setText(Users.getUsername());
        PasswordLabel.setText(Users.getPassword());
        FirstNameLabel.setText(Users.getFirstName());
        LastNameLabel.setText(Users.getLastName());
        EmailLabel.setText(Users.getEmail());
    }


    public void onLogOutButtonClick(ActionEvent event) {
        try {
            URL logInUrl = getClass().getResource("/templates/LogIn.fxml");
            if (logInUrl == null) {
                throw new NullPointerException("Missing resources on: LogIn.fxml");
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(logInUrl)));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            "Error loading LogIn.fxml: %s",
                            e.getMessage()));
        }
    }
}
