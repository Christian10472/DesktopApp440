/**
 * Mathew Nuval
 * Anthony Plasencia
 * Christian Perez
 * Professor Ebrahimi
 * Comp 440
 * 2023 November 3
 */

package edu.csun.desktopapp440.controllers;

import edu.csun.desktopapp440.database.UsersDatabase;
import edu.csun.desktopapp440.objects.Items;
import edu.csun.desktopapp440.objects.Reviews;
import edu.csun.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;

public class ItemViewController {

    @FXML
    private Label itemTitleLabel,CategoryLabel,DescriptionLabel,PriceLabel,SameUserLabel;

    @FXML
    private TextArea ReviewTextArea;

    private Users user;

    private String Category,Description,Title,username;

    private Items items;
    private ArrayList<Reviews> reviews;

    private double Price;



    public void initialiseItemViewController(Users users , Items items, ArrayList<Reviews> reviews){
        this.reviews = reviews;
        user = users;
        this.items= items;

        username = items.getUsername();
        Title = items.getTitle();
        Category = items.getCategory();
        Description = items.getDescription();
        Price = items.getPrice();

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
        if(reviews.isEmpty()){
            ReviewTextArea.setText("No reviews yet");
            return;
        }
        StringBuilder review = new StringBuilder();
        ReviewTextArea.setWrapText(true);
        ReviewTextArea.setEditable(false);
        for (Reviews value : reviews) {
            review.append("User: ").append(value.getReviewer()).append("\n")
                    .append("Date Posted: ").append(value.getDatePosted()).append("\n")
                    .append("Quality: ").append(value.getQuality()).append("\n")
                    .append("Review:  ").append(value.getReview()).append("\n")
                    .append("==================================").append("\n");
        }

        ReviewTextArea.setText(review.toString());
    }

    public void onBackClick(ActionEvent event) throws IOException {
        try{
            URL addITemURL = getClass().getResource("/templates/HomePage.fxml");
            if (addITemURL == null){
                throw new NullPointerException("Missing resources on: HomePage.fxml");
            }
            FXMLLoader loader = new FXMLLoader(addITemURL);
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
        if(user.getUsername().equals(username)){
            SameUserLabel.setText("You cannot review your own item");
        }
        else
            try {
            URL addITemURL = getClass().getResource("/templates/AddReview.fxml");
            if (addITemURL == null) {
                throw new NullPointerException("Missing resources on: AddReview.fxml");
            }
            FXMLLoader loader = new FXMLLoader(addITemURL);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            AddReviewController controller = loader.getController();
            controller.initialiseAddReviewController(user, items,reviews);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error loading AddReview.fxml: %s", e.getMessage()));
        }
    }
}
