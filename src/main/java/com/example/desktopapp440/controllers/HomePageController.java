package com.example.desktopapp440.controllers;

import com.example.desktopapp440.database.UsersDatabase;
import com.example.desktopapp440.objects.Items;
import com.example.desktopapp440.objects.Reviews;
import com.example.desktopapp440.objects.Users;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;


public class HomePageController implements Initializable {

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
    private ListView<String> userSearchListView;
    @FXML
    private TextField searchField;
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
        if(getUserSearch()){
            searchUserRequestedItem();
        }
    }


    //Getting whether the user inputted a prompt into the search field
    public boolean getUserSearch() {
        if (searchField.getText().isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    //Searching the database for the Users requested item;
    public void searchUserRequestedItem(){
        //Clear the listview for new search
        userSearchListView.getItems().clear();

        try{
            Connection dBConnection = new UsersDatabase().getDatabaseConnection();
            String query = "SELECT * FROM Items WHERE Category Like ?";
            PreparedStatement ps = dBConnection.prepareStatement(query);
            String userSearch = "%" + searchField.getText() + "%";
            ps.setString(1, userSearch);
            ResultSet rs = ps.executeQuery();

            //Get all items and display them on Search Window
            while (rs.next()){
                //Add items into an object for easy access to review page
                items.add(new Items(
                        rs.getInt("ItemId"),
                        rs.getString("Username"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Category"),
                        rs.getDouble("Price"),
                        rs.getDate("Date_Posted")
                ));
                //Only show id, title, Category, and Price on Search Table
                String newRow = String.format("%-5s%-20s%-25s%-8s",
                        items.get(i).getItemId(),
                        items.get(i).getTitle(),
                        items.get(i).getCategory(),
                        items.get(i).getPrice());
                userSearchListView.getItems().addAll(newRow);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Error getting search query from database: %s",e.getMessage()));
        }
    }

    //Override for when user clicks on an Item within the searchView
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        userSearchListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                //Get the Substring Item ID from the searchView
                String selectedItem = userSearchListView.getSelectionModel().getSelectedItem();
                String[] selectedItemSplit = selectedItem.split(" ");
                Integer selectedID = Integer.parseInt(selectedItemSplit[0]);

                //Try to load the Item_View FXML
                try {
                    URL viewItemURL = getClass().getResource("/templates/Item_View.fxml");
                    if (viewItemURL == null) {
                        throw new NullPointerException("Missing resources on: Item_View.fxml");
                    }
                    FXMLLoader loader = new FXMLLoader(viewItemURL);
                    Stage stage = (Stage) userSearchListView.getScene().getWindow();
                    stage.setScene(new Scene(loader.load()));
                    ItemViewController controller = loader.getController();
                    //passing User who is logged in + the selected Items ID
                    controller.initialiseItemViewController(User, selectedID);
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Error loading Item View.fxml: %s", e.getMessage()));
                }
            }
        });
    }

}
