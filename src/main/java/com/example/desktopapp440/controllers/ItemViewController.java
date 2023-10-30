package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
import com.example.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class ItemViewController {

    @FXML
    private Label itemTitleLabel,CategoryLabel,DescriptionLabel,PriceLabel;

    @FXML
    private ListView<String> ReviewListView;

    private Users user;

    private String Category,Description,Title;

    private int ItemId;

    private double Price;



    public void initialiseItemViewController(Users users ,int itemId) {
        user = users;
        ItemId = itemId;

        try{
           Connection dbConnection = new UsersDatabase().getDatabaseConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM Items WHERE ItemID = ?");
            preparedStatement.setInt(1,ItemId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            Title = resultSet.getString("Title");
            Category = resultSet.getString("Category");
            Description = resultSet.getString("Description");
            Price = resultSet.getDouble("Price");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        populateLabels();
        populateReviewList();
    }

    public void populateLabels(){
        itemTitleLabel.setText(Title);
        CategoryLabel.setText(Category);
        PriceLabel.setText(String.valueOf(Price));
        DescriptionLabel.setWrapText(true);
        DescriptionLabel.setText(Description);
    }

    public void populateReviewList(){
        try{
            Connection dbConnection = new UsersDatabase().getDatabaseConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT Review FROM Reviews WHERE ItemID = ?");
            preparedStatement.setInt(1,ItemId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            while(resultSet.next()){
               String review = resultSet.getString("Review");
                ReviewListView.getItems().addAll(review);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onBackClick(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomePage.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            HomePageController controller = loader.getController();
            controller.initialiseHomepage(user);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error loading HomePage.fxml: %s", e.getMessage()));
        }
    }

    public void onAddReviewClick(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddReview.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            AddReviewController controller = loader.getController();
            controller.initialiseAddReviewController(user,ItemId);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error loading AddReview.fxml: %s", e.getMessage()));
        }
    }
}
