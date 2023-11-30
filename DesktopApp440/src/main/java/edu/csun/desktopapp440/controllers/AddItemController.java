package edu.csun.desktopapp440.controllers;

import edu.csun.desktopapp440.database.UsersDatabase;
import edu.csun.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddItemController {
    private Users user;
    private String title,category, description;
    private double price;

    public void initializeAddItemController(Users users){
        user = users;
    }

    @FXML
    private Label titleOfPageLabel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField itemCatagoryTextField, itemPriceTextField, itemTitleTextField;

    @FXML
    private Label titleLabel, catagoryLabel, priceLabel, descriptionLabel,tooManyItems;


    /**
     * goes from the add item screen to the homepage screen
     */
    public void onBackButtonAction(ActionEvent event) throws IOException {
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


    /**
     * @param event
     * When the add button is clicked
     * does a check if the input is valid and
     * the user has not passed the 3 item daily limit
     */
    public void onAddButtonAction(ActionEvent event) {
        if (checkInput() && canUserAddItem(user.getUsername())) {
            title = itemTitleTextField.getText();
            category = itemCatagoryTextField.getText();
            description = descriptionTextArea.getText();
            price = Double.parseDouble(itemPriceTextField.getText());
            addItem();
            clearInput();
        }
    }

    /**
     * when the item is added the input is cleared and so are the
     * labels if any errors were made
     */
    private void clearInput() {
        itemTitleTextField.clear();
        itemCatagoryTextField.clear();
        descriptionTextArea.clear();
        itemPriceTextField.clear();
        titleLabel.setText("");
        catagoryLabel.setText("");
        priceLabel.setText("");
        descriptionLabel.setText("");
    }


    /**
     * @return if the input the user made is valid
     * promted to fix it if not
     */
    public boolean checkInput() {
        boolean valid=true;


        if (itemTitleTextField.getText().isEmpty()) {
            titleLabel.setText("Please enter a title");
            valid=false;
        }
        if (itemCatagoryTextField.getText().isEmpty()) {
            catagoryLabel.setText("Please enter a Category");
            valid = false;
        }
        if(descriptionTextArea.getText().isEmpty()) {
            descriptionLabel.setText("Please enter a description");
            valid = false;
        }
        try {
            Double.parseDouble(itemPriceTextField.getText());
        } catch (NumberFormatException e) {
            priceLabel.setText("Please enter a valid price");
            valid = false;
        }
        System.out.println("got to end of validation");
        return valid;
    }


    /**
     * @param username of the user adding
     * @return if the user has not passed the 3 item daily limit
     */
    public boolean canUserAddItem(String username) {
        LocalDate currentDate = LocalDate.now();


        Connection dbConnection = new UsersDatabase().getDatabaseConnection();
        final String query = "select count(*) from items where Username = ? and DatePosted = ?";
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setDate(2, java.sql.Date.valueOf(currentDate));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getInt(1) >= 3) {
                    tooManyItems.setText("You have already added 3 items today");
                    dbConnection.close();
                    return false;
                }
                dbConnection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("SQL Error: %s", e.getMessage()));
        }
        return true;



    }

    /**
     * Takes the users input and adds it to the database in the Items table
     */
    public void addItem() {
        Connection dbConnection = new UsersDatabase().getDatabaseConnection();
        final String query = "INSERT INTO items (Username,Title,Category,Description,Price,DatePosted) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, category);
            preparedStatement.setString(4, description);
            preparedStatement.setDouble(5, price);
            preparedStatement.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
            preparedStatement.executeUpdate();
            titleOfPageLabel.setText("Item Added");
            dbConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("SQL Error: %s", e.getMessage()));
        }

    }
}
