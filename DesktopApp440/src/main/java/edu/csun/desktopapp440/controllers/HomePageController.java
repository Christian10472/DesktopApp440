package edu.csun.desktopapp440.controllers;

import edu.csun.desktopapp440.database.UsersDatabase;
import edu.csun.desktopapp440.objects.Items;
import edu.csun.desktopapp440.objects.Reviews;
import edu.csun.desktopapp440.objects.Users;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    @FXML
    private Label statusLabel;

    private Users User;
    private ArrayList<Items> items = new ArrayList<>();
    private ArrayList<Reviews> itemReviews = new ArrayList<>();

    public void initialiseHomepage(Users user) {
       User = user;
    }

    public void onLogOutButtonClick(ActionEvent event) {
        MainController.returnToLoginPage(event, getClass());
    }

    public void onAddItemButtonClick(ActionEvent event){
        try{
            URL addITemURL = getClass().getResource("/templates/AddItem.fxml");
            if (addITemURL == null){
                throw new NullPointerException("Missing resources on: AddItem.fxml");
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

    @FXML
    public void onInitializeDatabaseButtonClick(ActionEvent event) {

        final String checkIfItemExists = "SELECT count(*)\n" +
                "FROM information_schema.tables\n" +
                "WHERE table_schema = ?\n" +
                "AND table_name = ?;";
        final String reInitializeDatabase = "CALL ReinitializeItemsTable";
        final String initializeDatabase = "CALL InitializeItemsTable";


        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement checkIfItemExistsStatement =
                    dbConnection.prepareStatement(checkIfItemExists);
            checkIfItemExistsStatement.setString(1, "comp440database");
            checkIfItemExistsStatement.setString(2, "items");
            ResultSet resultSet = checkIfItemExistsStatement.executeQuery();
            resultSet.next();
            if(resultSet.getInt(1) > 0) {
                PreparedStatement reInitializeDatabaseStatement =
                        dbConnection.prepareStatement(reInitializeDatabase);
                reInitializeDatabaseStatement.execute();
                statusLabel.setText("Items table has been reinitialized");
            } else {
                PreparedStatement initializeDatabaseStatement =
                        dbConnection.prepareStatement(initializeDatabase);
                initializeDatabaseStatement.execute();
                statusLabel.setText("Items table has been initialized");
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("SQL Error: %s", e.getMessage()));
        }
    }


    public void onSearchClick(){
        if(getUserSearch()){
            searchUserRequestedItem();
        }
    }


    //Getting whether the user inputted a prompt into the search field
    public boolean getUserSearch() {
        return !searchField.getText().isEmpty();
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
            int i = 0;
            while (rs.next()){
                //Add items into an object for easy access to review page
                items.add(new Items(
                        rs.getInt("ItemId"),
                        rs.getString("Username"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Category"),
                        rs.getDouble("Price"),
                        rs.getDate("DatePosted")
                ));
                //Only show id, title, Category, and Price on Search Table
                String newRow = String.format("%-5s%-20s%-25s%-8s",
                        items.get(i).getItemId(),
                        items.get(i).getTitle(),
                        items.get(i).getCategory(),
                        items.get(i).getPrice());
                userSearchListView.getItems().addAll(newRow);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Error getting search query from database: %s",e.getMessage()));
        }
    }

    //Method to get Reviews of item to be sent as a list for item View Controller to read
    //Reduces calls on database when accessing add review scene
    public void getReviews(int row){
        int selectedItemId= items.get(row).getItemId();
        try{
            Connection dBConnection = new UsersDatabase().getDatabaseConnection();
            String query = "SELECT * FROM Reviews where ItemId = ?";
            PreparedStatement ps = dBConnection.prepareStatement(query);
            ps.setInt(1, selectedItemId);
            ResultSet rs = ps.executeQuery();

            //Get all reviews and display them on Search Window
            while (rs.next()){
                itemReviews.add(new Reviews(
                        rs.getString("Reviewer"),
                        rs.getString("Quality"),
                        rs.getString("Review"),
                        rs.getDate("DatePosted")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Error getting reviews: %s", e.getMessage()));
        }
    }

    //Override for when user clicks on an Item within the searchView
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        userSearchListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                //Get the row index to find which index in items array user chose
                int selectedRowIndex = userSearchListView.getSelectionModel().getSelectedIndex();

                //Try to load the Item_View FXML
                try {
                    URL viewItemURL = getClass().getResource("/templates/ItemView.fxml");
                    if (viewItemURL == null) {
                        throw new NullPointerException("Missing resources on: ItemView.fxml");
                    }
                    FXMLLoader loader = new FXMLLoader(viewItemURL);
                    Stage stage = (Stage) userSearchListView.getScene().getWindow();
                    stage.setScene(new Scene(loader.load()));
                    ItemViewController controller = loader.getController();

                    //get Reviews for the item that was selected to pass to Item View Controller
                    getReviews(selectedRowIndex);

                    //passing User who is logged in + the selected Items ID + the items reviews
                    controller.initialiseItemViewController(User, items.get(selectedRowIndex), itemReviews);
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Error loading Item View.fxml: %s", e.getMessage()));
                }
            }
        });
    }

}
