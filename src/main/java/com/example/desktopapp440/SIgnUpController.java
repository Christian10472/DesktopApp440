package com.example.desktopapp440;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SIgnUpController {
    private boolean allGood;
    @FXML
    private Label FirstText, UsernameText, EmailText, PasswordText;
    @FXML
    private TextField FirstUser, LastUser, UsernameUser, EmailUser, PasswordUser;
    @FXML
    private CheckBox TermsCheck;

    @FXML
    protected void onSignUpClick() {
        allGood = true;
        //Checking if any parameters are null
        if((isNull(FirstUser) == true) || (isNull(LastUser) == true)){
            FirstText.setText("*Please enter a valid name");
        }
        if(isNull(UsernameUser) == true){
            UsernameText.setText("*Please enter a valid Username");
        }
        if(isNull(EmailUser) == true){
            EmailText.setText("*Please enter a valid email address");
        }
        if(isNull(PasswordUser) == true){
            PasswordText.setText("*Please enter a valid email password");
        }

        //checking if parameters are valid to database


    }

    public boolean isNull(TextField x){
        if (x.getText().isEmpty()){
            allGood = false;
            return true;
        }
        return false;
    }

}
