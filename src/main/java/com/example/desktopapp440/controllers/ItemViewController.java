package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
import com.example.desktopapp440.objects.Users;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class ItemViewController {


    @FXML
    private Label itemTitleLabel,CategoryLabel,DescriptionLabel,PriceLabel;

    @FXML
    private ListView<?> ReviewListView;

    private Users user;

    private String Username,Category,Description,Title;

    private int ItemId;

    private double Price;

    private Date datePosted;


    public void initialiseItemViewController(Users users ,int itemId) {
        user = users;
        ItemId = itemId;

        try{
           Connection dbConnection = new UsersDatabase().getDatabaseConnection();
           final String query = "select * from items where ItemId = ?";
           PreparedStatement statement = dbConnection.prepareStatement(query);
           statement.setString(1,String.valueOf(ItemId));
            ResultSet resultSet = statement.executeQuery();

            Username = resultSet.getString(2);
            Title = resultSet.getString(3);
            Category = resultSet.getString(4);
            Price = resultSet.getDouble(5);
            datePosted = resultSet.getDate(6);
        }
        catch (SQLException e){
        }

        populateLabels();
        populateReviewList();
    }

    public void populateLabels(){
        itemTitleLabel.setText(Title);
        CategoryLabel.setText(Category);
        PriceLabel.setText(String.valueOf(Price));
        DescriptionLabel.setText(Description);
    }

    public void populateReviewList(){

    }
}
