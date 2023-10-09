package com.example.desktopapp440;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class HomePageController {



    UserGetterNSetter Users;
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

   public void initialiseHomepage(UserGetterNSetter userGetterNSetter){
    Users = userGetterNSetter;
    UsernameLabel.setText(Users.getUsername());
    PasswordLabel.setText(Users.getPassword());
    FirstNameLabel.setText(Users.getFirstName());
    LastNameLabel.setText(Users.getLastName());
    EmailLabel.setText(Users.getEmail());
   }


}
