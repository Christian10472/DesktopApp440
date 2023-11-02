package edu.csun.desktopapp440.controllers;

import edu.csun.desktopapp440.database.UsersDatabase;
import edu.csun.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    private Label TitleOfPageLabel;

    @FXML
    private TextArea DescriptionTextArea;

    @FXML
    private TextField ItemCatagoryTextField,ItemPriceTextField,ItemTitleTextFiled;

    @FXML
    private Label TitleLabel, CatagoryLabel, PriceLabel, DescriptionLabel,tooManyItems;


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
            title = ItemTitleTextFiled.getText();
            category = ItemCatagoryTextField.getText();
            description = DescriptionTextArea.getText();
            price = Double.parseDouble(ItemPriceTextField.getText());
            addItem();
            clearInput();
        }
    }

    /**
     * when the item is added the input is cleared and so are the
     * labels if any errors were made
     */
    private void clearInput() {
        ItemTitleTextFiled.clear();
        ItemCatagoryTextField.clear();
        DescriptionTextArea.clear();
        ItemPriceTextField.clear();
        TitleLabel.setText("");
        CatagoryLabel.setText("");
        PriceLabel.setText("");
        DescriptionLabel.setText("");
    }


    /**
     * @return if the input the user made is valid
     * promted to fix it if not
     */
    public boolean checkInput() {
        boolean valid=true;
        if (ItemTitleTextFiled.getText().isEmpty()) {
            TitleLabel.setText("Please enter a title");
            valid=false;
        }
        if (ItemCatagoryTextField.getText().isEmpty()) {
            CatagoryLabel.setText("Please enter a Category");
            valid = false;
        }
        if(DescriptionTextArea.getText().isEmpty()) {
            DescriptionLabel.setText("Please enter a description");
            valid = false;
        }
        try {
            Double.parseDouble(ItemPriceTextField.getText());
        } catch (NumberFormatException e) {
            PriceLabel.setText("Please enter a valid price");
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
        LocalDate startDate = currentDate.atStartOfDay().toLocalDate();
        LocalDate endDate = currentDate.plusDays(1).atStartOfDay().toLocalDate();

        Connection dbConnection = new UsersDatabase().getDatabaseConnection();
        final String query = "SELECT COUNT(*) FROM items WHERE username = ? AND date BETWEEN ? AND ?";
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(3, java.sql.Date.valueOf(endDate));
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
            String.format("SQL Error: %s", e.getMessage());
        }
        return true;



    }

    /**
     * Takes the users input and adds it to the database in the Items table
     */
    public void addItem() {
        Connection dbConnection = new UsersDatabase().getDatabaseConnection();
        final String query = "INSERT INTO items (Username,Title,Category,Description,Price,Date_Posted) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, category);
            preparedStatement.setString(4, description);
            preparedStatement.setDouble(5, price);
            preparedStatement.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
            preparedStatement.executeUpdate();
            TitleOfPageLabel.setText("Item Added");
            dbConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("SQL Error: %s", e.getMessage()));
        }

    }
}
