package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
import com.example.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddReviewController implements Initializable {

    @FXML
    private Button addReviewButton,
            backButton;

    @FXML
    private ChoiceBox<String> conditionChoiceBox;

    @FXML
    private Label itemTitleLabel;

    @FXML
    private TextArea reviewTextArea;

    private Users user;
    private int itemId;
    private String[] condition = {"excellent", "good", "fair", "poor"};

    public void initialiseAddReviewController(Users users , int ItemId) {
        user = users;
        ItemId = itemId;
    }

    //Populating checklist with the condition values
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        conditionChoiceBox.getItems().addAll(condition);
    }

    public void onAddReviewButtonClick(){
        if(checkIfThreeOrMoreReviews() && CheckIfSellerIsReviewer()){
           addReview();
        }
    }

    private boolean CheckIfSellerIsReviewer() {
        try{
            Connection dbConnection = new UsersDatabase().getDatabaseConnection();
            final String query = ("Select * from Items where ItemId = ?");
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setInt(1,itemId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                if (user.getUsername().equals(resultSet.getString("Username")));
                //prompt for cant review cuz you made the post stop trying to bot your shit
                return false;
            }
        }
        catch (SQLException e)
        {

        }
        return true;
    }

    public boolean checkIfThreeOrMoreReviews(){
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.atStartOfDay().toLocalDate();
        LocalDate endDate = currentDate.plusDays(1).atStartOfDay().toLocalDate();

        Connection dbConnection = new UsersDatabase().getDatabaseConnection();
        final String query = "SELECT COUNT(*) FROM Reviews WHERE Reviewer = ? AND date BETWEEN ? AND ?";
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(3, java.sql.Date.valueOf(endDate));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getInt(1) >= 3) {
                    //Promt for to many reviews in one day aka get a fucking life
                    return false;
                }
            }
        } catch (SQLException e) {
            String.format("SQL Error: %s", e.getMessage());
        }
        return true;
    }


    private void addReview() {
        try {
            Connection dBConnection = new UsersDatabase().getDatabaseConnection();
            String sql = "INSERT INTO " +
                    "Reviews(ItemId,Reviewer,Quality,Review,Date_Posted) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement ps = dBConnection.prepareStatement(sql);
            ps.setInt(2, itemId);
            ps.setString(2, user.getUsername());
            ps.setString(3, conditionChoiceBox.getValue());
            ps.setString(4,  reviewTextArea.getText());
            ps.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            ps.executeUpdate();

            dBConnection.close();

        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format(
                            "Error inserting into table: %s",
                            e.getMessage()));
        }
    }


}
