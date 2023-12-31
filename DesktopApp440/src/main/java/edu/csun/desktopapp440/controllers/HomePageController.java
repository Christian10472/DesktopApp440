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
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
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
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;


public class HomePageController implements Initializable {

    /**
     * Log variable
     */
    private static final Logger log;

    /**
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
    private TextField searchField,FirstCategory,SecondCategory;
    @FXML
    private ChoiceBox<String> User1;
    @FXML
    private ChoiceBox<String> User2;
    @FXML
    private Label statusLabel;
    @FXML
    private Button SearchButton,LogOutButton;
    private DatePicker Dates;


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
            "Users who never posted a Excellent item",
            "Users who never posted a poor review",
            "Only Poor Item Reviews",
            "No Poor Item Reviews",
            "List a user pair (A, B) such that they always gave each other excellent reviews"};
    private boolean clickable = true;

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
        clearListView();
        switch (searchChoiceBox.getValue()) {
            case "Category":
               if (getUserSearch()) {
                   searchUserRequestedItem();
               }
               break;
            case "2 items Same Day Different Categories":
                if(twoItemsSameDayInput())
                    queryTwoItemsSameDay();
                break;
            case "All the items posted by user X, such that all the comments are Excellent or good":
                if(userXAllExcellentOrGoodInput())
                    queryUserXAllExcellentOrGood();
                break;
            case "Most Posts On Certain Day":
                if(getMostPostedItemsInput())
                    getMostPostedItems();
                break;
            case "Favorites From User X and User Y":
                if(favoriteFromUserXAndYInput())
                    queryFavoriteFromUserXAndY();
                break;

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
            populateWithItems(rs);

        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Error getting search query from database: %s",e.getMessage()));
        }
    }


    //Populate Chart with Items (Only done if user can click on item to go to review GUI)
    public void populateWithItems(ResultSet rs) throws SQLException {
        //Variable to allow table to be clicked
        clickable = true;

        //Get all items and display them on Search Window
        int i = 0;
        if (!items.isEmpty()) {
            items.clear();
        }
        while (rs.next()) {
            //Add items into an object for easy access to review page
            items.add(new Items(
                    rs.getInt("ItemId"),
                    rs.getString("Username"),
                    rs.getString("Title"),
                    rs.getString("Category"),
                    rs.getString("Description"),
                    rs.getDouble("Price"),
                    rs.getDate("DatePosted")
            ));
            // Only show id, title, Category, and Price on the Search Table
            int id = items.get(i).getItemId();
            String title = truncateString(items.get(i).getTitle(), 18);
            String category = truncateString(items.get(i).getCategory(), 30);
            double price = items.get(i).getPrice();
            String username = truncateString(items.get(i).getUsername(), 15);
            String description = truncateString(items.get(i).getDescription(), 30);

            String newRow = String.format("%-5s %-18s %-30s %-8s %-15s %-30s",
                    id, title, category, price, username, description);
            userSearchListView.getItems().addAll(newRow);
            i++;
        }
    }

    // Truncate a string to a specified length
    private String truncateString(String input, int maxLength) {
        if (input.length() > maxLength) {
            return input.substring(0, maxLength - 4) + "...";
        } else {
            return input;
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
                if (!clickable) {
                    return;
                }
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

    /**
     * Work on Phase 3 of the project
     */

    //Update Search Field when DropDown Menu Item is selected
    private void updateSearchField(ActionEvent event) {
        clearHbox();
        clearListView();
        switch (searchChoiceBox.getValue()) {
            case "Category" -> {
                homePageHBox.setSpacing(10);
                searchField.setText(null);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        searchField,
                        SearchButton,
                        LogOutButton);
                searchField.setPromptText("Enter Category");
            }
            case "Highest Price in Each Category" -> {
                homePageHBox.setSpacing(375);
                searchField.setText(null);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                GetHighestPriceInEachCategory();
            }
            case "2 items Same Day Different Categories" -> {
                homePageHBox.setSpacing(10);
                FirstCategory = new TextField();
                SecondCategory = new TextField();
                Dates = new DatePicker();
                FirstCategory.setPromptText("Category 1");
                FirstCategory.setPrefWidth(100);
                SecondCategory.setPromptText("Category 2");
                SecondCategory.setPrefWidth(100);
                Dates.setPromptText("Date");
                Dates.setPrefWidth(100);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        FirstCategory,
                        SecondCategory,
                        Dates,
                        SearchButton,
                        LogOutButton);
            }
            case "All the items posted by user X, such that all the comments are Excellent or good" -> {
                Region spacer1 = new Region();
                spacer1.setPrefWidth(10);
                Region spacer2 = new Region();
                spacer2.setPrefWidth(40);
                searchField.setText(null);
                searchField.setPromptText("Enter Username");
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        spacer1,
                        searchField,
                        SearchButton,
                        LogOutButton);
            }
            case "Most Posts On Certain Day" -> {
                homePageHBox.setSpacing(25);
                Dates = new DatePicker();
                Dates.setPromptText("Enter a Date");
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        Dates,
                        SearchButton,
                        LogOutButton);
            }
            case "Favorites From User X and User Y" -> {
                homePageHBox.setSpacing(10);
                initializeUserDropdown();
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        User1,
                        User2,
                        SearchButton,
                        LogOutButton);
            }
            case "Users who never posted a Excellent item" -> {
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                NeverPostedExcellentItem();
            }
            case "Users who never posted a poor review" -> {
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                NeverPostedPoorReview();
            }
            case "Only Poor Item Reviews" -> {
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                OnlyPostedPoorReviews();
            }
            case "No Poor Item Reviews" -> {
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                NoPoorItemReviews();
            }
            case "List a user pair (A, B) such that they always gave each other excellent reviews" -> {
                homePageHBox.setSpacing(375);
                homePageHBox.getChildren().addAll(
                        searchChoiceBox,
                        LogOutButton);
                UsersWhoGaveEachOtherExcellent();
            }
        }
    }

    private void initializeUserDropdown() {
        User1 = new ChoiceBox<String>();
        User2 = new ChoiceBox<String>();
        ObservableList<String> usernameList = getUsernamesFromDatabase();
        User1.setItems(usernameList);
        User1.setPrefWidth(100);
        User2.setItems(usernameList);
        User2.setPrefWidth(100);
    }

    private ObservableList<String> getUsernamesFromDatabase() {
        String query = "SELECT Username FROM users";
        List<String> usernameList = new ArrayList<>();
        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement checkIfTableExistsStatement =
                    dbConnection.prepareStatement(query);
            ResultSet resultSet = checkIfTableExistsStatement.executeQuery();
            resultSet.next();
            while(resultSet.next()) {
                usernameList.add(resultSet.getString(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableList(usernameList);
    }



    //Requirement for #1 Gets the highest priced item in each category
    private void GetHighestPriceInEachCategory() {
        try {
            Connection connection = new UsersDatabase().getDatabaseConnection();
            String query = "SELECT * FROM items WHERE (Category, Price) IN (SELECT Category, MAX(Price) AS MaxPrice FROM items GROUP BY Category)";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            populateWithItems(rs);

        }
        catch (SQLException e){
            String.format("SQL Error: %s", e.getMessage());
        }
    }

    //Requirement for #2 checks if the input is populated
    private boolean twoItemsSameDayInput() {
        if(FirstCategory.getText().isEmpty()){
            FirstCategory.setText(null);
            FirstCategory.setPromptText("Invalid Input");
            return false;
        }

        if(SecondCategory.getText().isEmpty()){
            SecondCategory.setText(null);
            SecondCategory.setPromptText("Invalid Input");
            return false;
        }

        if(Dates.getValue()==null){
            Dates.setPromptText("Pick A Date");
            return false;
        }
        return true;
    }

    //Requirement for #2 runs the query gets the users who posted into 2 different categories x and y on the same day z
    private void queryTwoItemsSameDay() {
        clickable = false;
        try{
            Connection connection = new UsersDatabase().getDatabaseConnection();
            String categoriesList = FirstCategory.getText() + "," + SecondCategory.getText();
            String[] categoriesArray = categoriesList.split(",");
            StringBuilder sqlQuery = new StringBuilder("SELECT Username FROM items WHERE DatePosted = ? AND (");

            for (int i = 0; i < categoriesArray.length; i++) {
                sqlQuery.append("Category LIKE ?");
                if (i < categoriesArray.length - 1) {
                    sqlQuery.append(" OR ");
                }
            }
            sqlQuery.append(") GROUP BY Username, DatePosted HAVING COUNT(DISTINCT Category) >= ")
                    .append(categoriesArray.length)
                    .append(" ORDER BY Username");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery.toString());
          preparedStatement.setString(1, String.valueOf(Date.valueOf(Dates.getValue())));

            for (int i = 0; i < categoriesArray.length; i++) {
                preparedStatement.setString(i + 2, "%" + categoriesArray[i].trim() + "%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userSearchListView.getItems().addAll(resultSet.getString(1));
            }
        }
        catch (SQLException e){
            String.format("SQL Error: %s", e.getMessage());
        }
    }

    //Requirement for #3 checks to see if the input is valid
    private boolean userXAllExcellentOrGoodInput() {
        if (searchField.getText().isEmpty()) {
            searchField.setText(null);
            searchField.setPromptText("Invalid Input");
            return false;
        }
        return true;
    }

    //Requirement for #3 runs the query and gets all users who have items with only excellent or good reviews
    private void queryUserXAllExcellentOrGood() {
        clickable = true;
        try {
            Connection connection = new UsersDatabase().getDatabaseConnection();
            String query = "SELECT i.ItemId, i.Title ,i.Category ,i.Price,i.Username, i.Description " +
                    "FROM items i INNER JOIN reviews r " +
                    "ON i.ItemId = r.ItemId WHERE i.Username = ? " +
                    "GROUP BY i.ItemId, i.Title, i.Username " +
                    "HAVING COUNT(CASE WHEN r.Quality NOT IN ('Excellent', 'Good') THEN 1 END) = 0";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, searchField.getText());
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No items found for the specified user or criteria.");
            } else {
                //Get all items and display them on Search Window
                while (rs.next()) {
                    String id = String.valueOf(rs.getInt("ItemId"));
                    String title = String.valueOf(rs.getString("Title"));
                    String category = String.valueOf(rs.getString("Category"));
                    String price = String.valueOf(rs.getDouble("Price"));
                    String username = String.valueOf(rs.getString("Username"));
                    String description = String.valueOf(rs.getString("Description"));
                    String newRow = String.format("%-5s %-18s %-30s %-8s %-15s %-30s",
                            id, title, category, price, username, description);
                    userSearchListView.getItems().addAll(newRow);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception here
        }
    }

 //Method for Number 4 Requirement checks if input is valid
    private boolean getMostPostedItemsInput(){
        if(Dates.getValue()==null){
            Dates.setPromptText("Pick A Date");
            return false;
        }
        return true;
    }

    //Method for Number 4 Requirement to search database
    private void getMostPostedItems(){
        clickable = false;
        System.out.println(clickable);
        try {
            Connection connection = new UsersDatabase().getDatabaseConnection();
            String query = "SELECT Username, DatePosted, COUNT(ItemId) AS NumberOfItemsPosted " +
                    "FROM items " +
                    "WHERE DatePosted = ? " +
                    "GROUP BY Username, DatePosted " +
                    "ORDER BY NumberOfItemsPosted DESC ";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(Dates.getValue()));
            ResultSet rs = ps.executeQuery();

            int max = 0;
            while (rs.next()) {
                //Set value to first count
                if (rs.isFirst()) {
                    max = rs.getInt("NumberOfItemsPosted");
                }
                //Check if count is growing smaller. Done so ties are printed
                if(rs.getInt("NumberOfItemsPosted") < max){
                    break;
                }
                //Print Users, Date, and count of items
                String newRow = String.format("%-20s%-15s Items Posted:%-4d",
                        rs.getString("Username"),
                        rs.getDate("DatePosted"),
                        rs.getInt("NumberOfItemsPosted"));
                System.out.println(newRow);
                userSearchListView.getItems().addAll(newRow);
            }


        }
        catch (SQLException e){
            String.format("SQL Error: %s", e.getMessage());
        }
    }

    private void dropTwoFavoriteTable() {
        String initializeQuery =
                "DROP TABLE twofavoriteusers";
        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement prepareQuery =
                    dbConnection.prepareStatement(initializeQuery);
            prepareQuery.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void initializeTwoFavorites() {
        dropTwoFavoriteTable();
        String initializeQuery =
                "CREATE TABLE TwoFavoriteUsers AS (SELECT fav1.Username AS Username1, fav2.Username AS Username2, fav1.FavoriteUser " +
                "FROM favorites AS fav1 " +
                "INNER JOIN favorites AS fav2 " +
                "ON fav1.Username > fav2.Username " +
                "AND fav1.FavoriteUser = fav2.FavoriteUser)";
        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement prepareQuery =
                    dbConnection.prepareStatement(initializeQuery);
            prepareQuery.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Requirement #5 runs the query
    private void queryFavoriteFromUserXAndY(){
        initializeTwoFavorites();
        String query = "SELECT * FROM users WHERE Username LIKE (SELECT FavoriteUser " +
                "FROM twofavoriteusers " +
                "WHERE Username1 LIKE ? OR Username1 LIKE ? AND Username2 LIKE ? OR Username2 LIKE ?)";
        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement prepareQuery =
                    dbConnection.prepareStatement(query);
            prepareQuery.setString(1, User1.getValue());
            prepareQuery.setString(2, User2.getValue());
            prepareQuery.setString(3, User1.getValue());
            prepareQuery.setString(4, User2.getValue());
            ResultSet resultSet = prepareQuery.executeQuery();
            while (resultSet.next()) {
                userSearchListView.getItems().addAll(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Requirement for #5 checks the input
    private boolean favoriteFromUserXAndYInput(){
        return true;
    }

    //Requirement for #6 runs the query no need to check input
    private void NeverPostedExcellentItem() {
        clickable = false;
        String query = "SELECT * FROM Users WHERE Username NOT IN (SELECT DISTINCT Username " +
                "FROM items " +
                "WHERE Username " +
                "IN (SELECT Owner " +
                "FROM reviews " +
                "WHERE Quality = 'excellent' " +
                "GROUP BY (Owner) " +
                "HAVING COUNT(Quality) >= 3))";
        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement prepareQuery =
                    dbConnection.prepareStatement(query);
            ResultSet resultSet = prepareQuery.executeQuery();
            while (resultSet.next()) {
                userSearchListView.getItems().addAll(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Requirement for #7
    private void NeverPostedPoorReview() {
        clickable = false;
        try{
            Connection connection = new UsersDatabase().getDatabaseConnection();
            String query = "SELECT * FROM Users WHERE Username NOT IN (SELECT Reviewer " +
                    "FROM reviews " +
                    "WHERE Quality = 'poor' " +
                    "GROUP BY (Reviewer) " +
                    "HAVING COUNT(Quality) > 0)";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                userSearchListView.getItems().addAll(rs.getString(1));
            }
        }
        catch (SQLException e){
            String.format("SQL Error: %s", e.getMessage());
        }
    }

    //Requirement #8
    private void OnlyPostedPoorReviews() {
        clickable = false;
        try {
            Connection connection = new UsersDatabase().getDatabaseConnection();
            String query = "SELECT DISTINCT reviewer " +
                    "FROM reviews review1 " +
                    "WHERE NOT EXISTS(" +
                    "SELECT * " +
                    "FROM reviews review2 " +
                    "WHERE review1.reviewer = review2.reviewer " +
                    "AND review2.Quality != 'poor')" +
                    "AND review1.Quality = 'poor'";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                userSearchListView.getItems().addAll(rs.getString("Reviewer"));
            }
        }
        catch (SQLException e){
            String.format("SQL Error: %s", e.getMessage());
        }
    }

    //Requirement for #9
    private void NoPoorItemReviews() {
        clickable = false;
        String query = "SELECT * FROM Users WHERE Username NOT IN (SELECT DISTINCT Username " +
                "FROM items " +
                "WHERE Username " +
                "IN (SELECT Owner " +
                "FROM reviews " +
                "WHERE Quality = 'poor' " +
                "GROUP BY (Owner) " +
                "HAVING COUNT(Quality) > 0) " +
                ")";
        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement prepareQuery =
                    dbConnection.prepareStatement(query);
            ResultSet resultSet = prepareQuery.executeQuery();
            while (resultSet.next()) {
                userSearchListView.getItems().addAll(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Methopd for Number10
    private void UsersWhoGaveEachOtherExcellent(){
        try {
            clickable = false;
            Connection connection = new UsersDatabase().getDatabaseConnection();
            String query = "SELECT A.username AS user_A, B.username AS user_B\n" +
                    "FROM users A\n" +
                    "CROSS JOIN users B\n" +
                    "WHERE A.username < B.username\n" +
                    "AND EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM items i1\n" +
                    "    WHERE i1.username = A.username\n" +
                    ")\n" +
                    "AND EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM items i2\n" +
                    "    WHERE i2.username = B.username\n" +
                    ")\n" +
                    "AND NOT EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM items i\n" +
                    "    WHERE i.username = A.username\n" +
                    "    AND NOT EXISTS (\n" +
                    "        SELECT 1\n" +
                    "        FROM reviews r\n" +
                    "        WHERE r.itemId = i.itemId\n" +
                    "        AND r.reviewer = B.username\n" +
                    "        AND r.quality = 'excellent'\n" +
                    "    )\n" +
                    ")\n" +
                    "AND EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM reviews r\n" +
                    "    WHERE r.reviewer = B.username\n" +
                    "    AND r.quality = 'excellent'\n" +
                    ")\n" +
                    "AND NOT EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM items i\n" +
                    "    WHERE i.username = B.username\n" +
                    "    AND NOT EXISTS (\n" +
                    "        SELECT 1\n" +
                    "        FROM reviews r\n" +
                    "        WHERE r.itemId = i.itemId\n" +
                    "        AND r.reviewer = A.username\n" +
                    "        AND r.quality = 'excellent'\n" +
                    "    )\n" +
                    ")\n" +
                    "AND EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM reviews r\n" +
                    "    WHERE r.reviewer = A.username\n" +
                    "    AND r.quality = 'excellent'\n" +
                    ");";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("user_A") + " " + rs.getString("user_B"));
                String newRow = String.format("%-20s%-15s",
                        rs.getString("user_A"),
                        rs.getString("user_B"));
                userSearchListView.getItems().addAll(newRow);
            }
        }
        catch (SQLException e){
            String.format("SQL Error: %s", e.getMessage());
        }
    }


    public void clearHbox() {
        homePageHBox.getChildren().clear();
    }

    public void clearListView(){
        userSearchListView.getItems().clear();
    }
}
