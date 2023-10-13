package com.example.desktopapp440.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SignUpSuccessController {

    @FXML
    private Button logInButton;

    @FXML
    protected void onLogInButtonClick(ActionEvent event) {
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
