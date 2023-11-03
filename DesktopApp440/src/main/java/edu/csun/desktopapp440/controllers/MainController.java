/**
 * Mathew Nuval
 * Anthony Plasencia
 * Christian Perez
 * Professor Ebrahimi
 * Comp 440
 * 2023 November 3
 */

package edu.csun.desktopapp440.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainController {
    public static void returnToLoginPage(ActionEvent event, Class<?> classObject) {
        try {
            URL logInUrl = classObject.getResource("/templates/LogIn.fxml");
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
