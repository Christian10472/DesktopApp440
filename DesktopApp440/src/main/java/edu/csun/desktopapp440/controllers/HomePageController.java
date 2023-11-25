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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
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
    private HBox homePageHBox;
    @FXML
    private ChoiceBox<String> searchChoiceBox;
    @FXML
    private ListView<String> userSearchListView;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button SearchButton,LogOutButton;

    private Users User;
    private final ArrayList<Items> items = new ArrayList<>();
    private final ArrayList<Reviews> itemReviews = new ArrayList<>();
    private final String[] SearchOptions =
            {"Category",
            "Highest Price in Each Category",
            "2 items Same Day Different Categories",
            "All the items posted by user X, such that all the comments are Excellent or good",
            "Most Posts On Certain Day",
            "Favorites From User X and User Y",
            "users who never posted a Excellent review",
            "users who never posted a poor review",
            "Only Poor Item Reviews",
            "No Poor Item Reviews",};

    public void initialiseHomepage(Users user) {
        User = user;

        searchChoiceBox.getItems().addAll(SearchOptions);
        searchChoiceBox.setValue(SearchOptions[0]);

        homePageHBox.setSpacing(10);
        homePageHBox.getChildren().addAll(searchChoiceBox,searchField,SearchButton,LogOutButton);

        searchChoiceBox.setOnAction(this::updateSearchField);
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

        final String checkIfTableExists = "SELECT count(*)\n" +
                "FROM information_schema.tables\n" +
                "WHERE table_schema = ?\n" +
                "AND table_name = ?;";

        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement checkIfTableExistsStatement =
                    dbConnection.prepareStatement(checkIfTableExists);
            checkIfTableExistsStatement.setString(1, "comp440database");
            checkIfTableExistsStatement.setString(2, "reviews");
            ResultSet resultSet = checkIfTableExistsStatement.executeQuery();
            resultSet.next();
            if(resultSet.getInt(1) <= 0) {
                if(verifyAndInitializeReviewTables(dbConnection)) {
                    initializeOrReinitializeTables(dbConnection, resultSet);
                }
            } else {
                initializeOrReinitializeTables(dbConnection, resultSet);
            }



        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("SQL Error: %s", e.getMessage()));
        }
    }

    public boolean verifyAndInitializeReviewTables(Connection dbConnection) {
        final String checkIfItemExists = "SELECT count(*)\n" +
                "FROM information_schema.tables\n" +
                "WHERE table_schema = ?\n" +
                "AND table_name = ?;";
        final String initializeReviewTables = "CALL InitializeReviewsTable";
        try {
            PreparedStatement checkIfTableExistsStatement =
                    dbConnection.prepareStatement(checkIfItemExists);
            checkIfTableExistsStatement.setString(1, "comp440database");
            checkIfTableExistsStatement.setString(2, "items");
            ResultSet itemsResultSet = checkIfTableExistsStatement.executeQuery();
            itemsResultSet.next();
            if (itemsResultSet.getInt(1) > 0) {
                PreparedStatement initializeDatabaseStatement =
                        dbConnection.prepareStatement(initializeReviewTables);
                initializeDatabaseStatement.execute();
                statusLabel.setText("Review table has been initialized");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("SQL Error: %s", e.getMessage()));
        }
        return true;
    }
    public void initializeOrReinitializeTables(Connection dbConnection, ResultSet resultSet) {
        final String reInitializeTables = "CALL ReinitializeTables";
        final String initializeTables = "CALL InitializeTables";
        try {
            if (resultSet.getInt(1) > 0) {
                PreparedStatement reInitializeDatabaseStatement =
                        dbConnection.prepareStatement(reInitializeTables);
                reInitializeDatabaseStatement.execute();
                statusLabel.setText("Database has been re-initialized");
            } else {
                PreparedStatement initializeDatabaseStatement =
                        dbConnection.prepareStatement(initializeTables);
                initializeDatabaseStatement.execute();
                statusLabel.setText("Database has been initialized");
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
            StringBuilder query =
                    new StringBuilder(
                            "SELECT * FROM items WHERE");
            String[] searchArray = searchField.getText().split(",");
            for(int i = 0; i < searchArray.length; i++) {
                if(i == 0) {
                    query.append(" Category LIKE ?");
                } else {
                    query.append(" OR Category LIKE ?");
                }
            }

            Connection dBConnection =
                    new UsersDatabase().getDatabaseConnection();
            PreparedStatement ps =
                    dBConnection.prepareStatement(query.toString());
            int index = 1;
            for (String searchItem : searchArray) {
                String preparedSearchItem = "%" + searchItem.trim() + "%";
                ps.setString(index++, preparedSearchItem);
            }
            ResultSet rs = ps.executeQuery();

            //Get all items and display them on Search Window
            int i = 0;
            if (!items.isEmpty()) {
                items.clear();
            }
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
            String query = "SELECT * FROM Reviews WHERE ItemId = ?";
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

    public void onSearchEvent(Event event) {
        if(event.getClass() == KeyEvent.class) {
            if(((KeyEvent) event).getCode() != KeyCode.ENTER) {
                return;
            }
        }
        onSearchClick();
    }

    private void updateSearchField(ActionEvent event) {
        clearHbox();
        clearListView();
        switch (searchChoiceBox.getValue()) {
            case "Category":
                homePageHBox.setSpacing(10);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        searchField,
                        SearchButton,
                        LogOutButton);

                searchField.setPromptText("Enter Category");
                break;
            case "Highest Price in Each Category":
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);

                GetHighestPriceInEachCategory();
                break;
            case "2 items Same Day Different Categories":
                homePageHBox.setSpacing(10);
                TextField FirstCategory= new TextField();
                TextField SecondCategory= new TextField();
                TextField Date= new TextField();

                FirstCategory.setPromptText("Category 1");
                FirstCategory.setPrefWidth(100);
                SecondCategory.setPromptText("Category 2");
                SecondCategory.setPrefWidth(100);
                Date.setPromptText("Date");
                Date.setPrefWidth(100);

                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        FirstCategory,
                        SecondCategory,
                        Date,
                        SearchButton,
                        LogOutButton);

                break;
            case "All the items posted by user X, such that all the comments are Excellent or good":
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        searchField,
                        LogOutButton);
                break;
            case "Most Posts On Certain Day":
                homePageHBox.setSpacing(25);
                TextField Day= new TextField();
                Day.setPrefWidth(100);
                Day.setPromptText("YYYY-MM-DD");
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        Day,
                        SearchButton,
                        LogOutButton);
                //CHeck if date is valid
                break;
            case "Favorites From User X and User Y":
                homePageHBox.setSpacing(10);
                TextField User1= new TextField();
                TextField User2= new TextField();
                User1.setPromptText("User 1");
                User1.setPrefWidth(100);
                User2.setPromptText("User 2");
                User2.setPrefWidth(100);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        User1,
                        User2,
                        SearchButton,
                        LogOutButton);
                break;
            case "users who never posted a Excellent review":
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                NeverPostedExcellentReview();
                break;
            case "users who never posted a poor review":
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                NeverPostedPoorReview();
                break;
            case "Only Poor Item Reviews":
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                OnlyPostedPoorItems();
                break;
            case "No Poor Item Reviews":
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                NoPoorItemReviews();
                break;
        }
    }

    private void NoPoorItemReviews() {
    }

    private void OnlyPostedPoorItems() {
    }

    private void NeverPostedPoorReview() {
    }

    private void NeverPostedExcellentReview() {
    }

    private void GetHighestPriceInEachCategory() {
        try {
            Connection connection = new UsersDatabase().getDatabaseConnection();
            String query = "Select Category, Max(Price) From items Group By Category";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet re = ps.executeQuery();
            //populate the list view with the results

        }
        catch (SQLException e){
            String.format("SQL Error: %s", e.getMessage());
        }
        //populate the list view with the results

    }

    public void clearHbox() {
        homePageHBox.getChildren().clear();
    }

    public void clearListView(){
        userSearchListView.getItems().clear();
    }
}
