package edu.csun.desktopapp440.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.logging.Logger;

public class SignUpSuccessController {

    /**
     * Log variable
     */
    private static final Logger log;

    /**
     * Logger for debugging
     */
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(SignUpSuccessController.class.getName());
    }

    @FXML
    private Button logInButton;

    @FXML
    protected void onLogInButtonClick(ActionEvent event) {
        MainController.returnToLoginPage(event, getClass());
    }
}
