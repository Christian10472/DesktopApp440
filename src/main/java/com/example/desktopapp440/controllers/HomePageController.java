package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.example.desktopapp440.objects.Users;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class HomePageController {

    /*
     * Log variable
     */
    private static final Logger log;

    /*
     * Logger for debugging
     */
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(HomePageController.class.getName());
    }

    @FXML
    private Button logOutButton;
    @FXML
    private Button addItemButton;
    @FXML
    private TextField searchField;
    private boolean ableToSearch;
    private Users User;

    public void initialiseHomepage(Users user) {
       User = user;
    }

    public void onLogOutButtonClick(ActionEvent event) {
        MainController.returnToLoginPage(event, getClass());
    }

    public void onAddItemButtonClick(ActionEvent event){
        try{
            URL addITemURL = getClass().getResource("/templates/Add_Item.fxml");
            if (addITemURL == null){
                throw new NullPointerException("Missing resources on: Add_Item.fxml");
            }
            FXMLLoader loader = new FXMLLoader(addITemURL);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            AddItemController controller = loader.getController();
            controller.initializeAddItemController(User);
            stage.show();
        } catch (IOException e) {
        throw new RuntimeException(String.format("Error loading HomePage.fxml: %s", e.getMessage()));
        }
    }


    public void onSearchClick(){
        getUserSearch();
        if(ableToSearch == true){
            searchUserRequestedItem();
        }
    }


    //Getting whether the user inputted a prompt into the search field
    public void getUserSearch() {
        if (searchField.getText().isEmpty()){
            ableToSearch = true;
        }
    }

    //Searching the database for the Users requested item;
    public void searchUserRequestedItem(){
        String[] itemID = new String[]{};
        String[] itemTitle = new String[]{};
        String[] itemDescription = new String[]{};
        String[] itemCategory = new String[]{};
        String[] itemPrice = new String[]{};

        try{
            Connection dBConnection = new UsersDatabase().getDatabaseConnection();
            final String findItemSearchCategory = "SELECT * FROM Users WHERE Category = ?;";
            PreparedStatement newUserValues = dBConnection.prepareStatement(findItemSearchCategory);

        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Error connecting to database: %s",e.getMessage()));
        }
    }
}
