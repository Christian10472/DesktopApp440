package com.example.desktopapp440;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SIgnUpController {
    private boolean allGood;
    @FXML
    private Label FirstText, LastText, UsernameText, EmailText, PasswordText;

    @FXML
    protected void onSignUpClick() {
        ifNull(FirstText);
    }

    public void ifNull(Label x){

    }

}
