package edu.csun.desktopapp440.controllers;

import edu.csun.desktopapp440.database.UsersDatabase;
import edu.csun.desktopapp440.objects.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;


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
                String id = rs.getString("ItemId");
                String title = rs.getString("Title");
                String category = rs.getString("Category");
                String description = rs.getString("Description");
                Double price = rs.getDouble("Price");

                String newRow = String.format("%-5s%-20s%-25s%-8s", id, title, category, price);
                System.out.println(newRow);
                userSearchListView.getItems().addAll(newRow);
            }


        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Error getting search query from database: %s",e.getMessage()));
        }
    }
}
