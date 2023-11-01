package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
import com.example.desktopapp440.objects.Items;
import com.example.desktopapp440.objects.Reviews;
import com.example.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddReviewController implements Initializable {


    @FXML
    private ChoiceBox<String> conditionChoiceBox;



    @FXML
    private TextArea reviewTextArea;

    private Users user;
    private Items item;
    private ArrayList<Reviews> reviews;
    private final String[] condition = {"excellent", "good", "fair", "poor"};

    public void initialiseAddReviewController(Users users , Items item, ArrayList<Reviews> reviews) {
        this.reviews = reviews;
        user = users;
        this.item = item;
    }

    //Populating checklist with the condition values
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        conditionChoiceBox.getItems().addAll(condition);
    }

    public void onAddReviewButtonClick(ActionEvent event) throws IOException {
        if(checkIfThreeOrMoreReviews()){
           addReview();
           goBack(event);
        }
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
                    //Prompt for to many reviews in one day aka get a fucking life
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
            ps.setInt(1, item.getItemId());
            ps.setString(2, user.getUsername());
            ps.setString(3, conditionChoiceBox.getValue());
            ps.setString(4,  reviewTextArea.getText());
            ps.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            ps.executeUpdate();

            //Adds to Review Object
            reviews.add(new Reviews(user.getUsername(),conditionChoiceBox.getValue(),reviewTextArea.getText(),java.sql.Date.valueOf(LocalDate.now())));

            dBConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format(
                            "Error inserting into table: %s",
                            e.getMessage()));
        }
    }

    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        goBack(actionEvent);
    }

    public void goBack(ActionEvent event) throws IOException{
        try{
            URL addITemURL = getClass().getResource("/templates/Item_View.fxml");
            if (addITemURL == null){
                throw new NullPointerException("Missing resources on: Item_View.fxml");
            }
            FXMLLoader loader = new FXMLLoader(addITemURL);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            ItemViewController controller = loader.getController();
            controller.initialiseItemViewController(user,item,reviews);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error loading HomePage.fxml: %s", e.getMessage()));
        }
    }
}
