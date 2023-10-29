package com.example.desktopapp440.controllers;

import com.example.desktopapp440.objects.Users;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class AddReviewController {

    @FXML
    private Button addReviewButton,
            backButton;

    @FXML
    private ChoiceBox<?> conditionChoiceBox;

    @FXML
    private Label itemTitleLabel;

    @FXML
    private TextArea reviewTextArea;

    private Users user;
    private int ItemId;

    public void initialiseAddReviewController(Users users , int itemId) {
        user = users;
        ItemId = itemId;
    }



}
