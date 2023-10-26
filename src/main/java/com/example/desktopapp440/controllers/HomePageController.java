package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.example.desktopapp440.objects.Users;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


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
    private Button logOutButton;
    @FXML
    private Button addItemButton;
    @FXML
    private TextField searchField;
    private boolean ableToSearch;
    private Users User;

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
