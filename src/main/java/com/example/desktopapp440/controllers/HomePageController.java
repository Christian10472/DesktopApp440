package com.example.desktopapp440.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.logging.Logger;

import com.example.desktopapp440.objects.Users;



public class HomePageController {

    /*
     * Log variable
     */
    private static final Logger log;

    /*
     * Logger for debugging
     */
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(HomePageController.class.getName());
    }

    @FXML
    private Label usernameLabel,
            passwordLabel,
            firstNameLabel,
            lastNameLabel,
            emailLabel;
    @FXML
    private Button logOutButton;

    public void initialiseHomepage(Users user) {
        usernameLabel.setText(user.getUsername());
        passwordLabel.setText(user.getPassword());
        firstNameLabel.setText(user.getFirstName());
        lastNameLabel.setText(user.getLastName());
        emailLabel.setText(user.getEmail());
    }


    public void onLogOutButtonClick(ActionEvent event) {
        MainController.returnToLoginPage(event, getClass());
    }
}
