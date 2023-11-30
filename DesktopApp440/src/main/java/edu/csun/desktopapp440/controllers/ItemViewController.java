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
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemViewController {


    @FXML
    public Button favoriteButton;

    @FXML
    private Label userLabel, itemTitleLabel, categoryLabel, descriptionLabel, priceLabel, SameUserLabel;

    @FXML
    private TextArea reviewTextArea;

    private Users user;

    private String category, description, title, username;

    private Items items;
    private ArrayList<Reviews> reviews;

    private double price;


    public void initialiseItemViewController(Users users, Items items, ArrayList<Reviews> reviews) {
        this.reviews = reviews;
        user = users;
        this.items = items;

        username = items.getUsername();
        title = items.getTitle();
        category = items.getCategory();
        description = items.getDescription();
        price = items.getPrice();

        populateLabels();
        populateReviewList();
    }

    public void populateLabels() {
        if(checkIfFavoriteExist()) {
            favoriteButton.setText("Un-favorite User");
        }
        userLabel.setText(username);
        itemTitleLabel.setText(title);
        categoryLabel.setText(category);
        priceLabel.setText(String.valueOf(price));
        descriptionLabel.setWrapText(true);
        descriptionLabel.setText(description);
    }

    public void populateReviewList() {
        if (reviews.isEmpty()) {
            reviewTextArea.setText("No reviews yet");
            return;
        }
        StringBuilder review = new StringBuilder();
        reviewTextArea.setWrapText(true);
        reviewTextArea.setEditable(false);
        for (Reviews value : reviews) {
            review.append("User: ").append(value.getReviewer()).append("\n")
                    .append("Date Posted: ").append(value.getDatePosted()).append("\n")
                    .append("Quality: ").append(value.getQuality()).append("\n")
                    .append("Review:  ").append(value.getReview()).append("\n")
                    .append("==================================").append("\n");
        }

        reviewTextArea.setText(review.toString());
    }

    public void onBackClick(ActionEvent event) throws IOException {
        try {
            URL addITemURL = getClass().getResource("/templates/HomePage.fxml");
            if (addITemURL == null) {
                throw new NullPointerException("Missing resources on: HomePage.fxml");
            }
            FXMLLoader loader = new FXMLLoader(addITemURL);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            HomePageController controller = loader.getController();
            controller.initialiseHomepage(user);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error loading HomePage.fxml: %s", e.getMessage()));
        }
    }

    public void onAddReviewClick(ActionEvent event) {
        if (user.getUsername().equals(username)) {
            SameUserLabel.setText("You cannot review your own item");
        } else
            try {
                URL addITemURL = getClass().getResource("/templates/AddReview.fxml");
                if (addITemURL == null) {
                    throw new NullPointerException("Missing resources on: AddReview.fxml");
                }
                FXMLLoader loader = new FXMLLoader(addITemURL);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                AddReviewController controller = loader.getController();
                controller.initialiseAddReviewController(user, items, reviews);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void onFavoriteClick(ActionEvent event) {
        if(!checkIfFavoriteExist()) {
            addUserToFavorites();
            favoriteButton.setText("Un-favorite User");
        } else {
            removeUserFromFavorites();
            favoriteButton.setText("Favorite User");
        }
    }

    private boolean removeUserFromFavorites() {
        final String removeUserFromFavorites =
                "DELETE FROM favorites " +
                        "WHERE Username = ? " +
                        "AND FavoriteUser = ?";

        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement checkIfTableExistsStatement =
                    dbConnection.prepareStatement(removeUserFromFavorites);
            checkIfTableExistsStatement.setString(1, user.getUsername());
            checkIfTableExistsStatement.setString(2, items.getUsername());
            checkIfTableExistsStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    private boolean addUserToFavorites() {
        final String addUserToFavorites =
                "INSERT INTO favorites (" +
                        "Username, " +
                        "FavoriteUser" +
                        ")" +
                        " VALUES (" +
                        " ? , " +
                        " ? " +
                        ")";

        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement checkIfTableExistsStatement =
                    dbConnection.prepareStatement(addUserToFavorites);
            checkIfTableExistsStatement.setString(1, user.getUsername());
            checkIfTableExistsStatement.setString(2, items.getUsername());
            checkIfTableExistsStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private boolean checkIfFavoriteExist() {
        final String checkIfFavoritesExists =
                "SELECT count(*) " +
                        "FROM favorites " +
                        "WHERE Username = ? " +
                        "AND FavoriteUser = ?;";

        try (Connection dbConnection = new UsersDatabase().getDatabaseConnection()) {
            PreparedStatement checkIfTableExistsStatement =
                    dbConnection.prepareStatement(checkIfFavoritesExists);
            checkIfTableExistsStatement.setString(1, user.getUsername());
            checkIfTableExistsStatement.setString(2, items.getUsername());
            ResultSet resultSet = checkIfTableExistsStatement.executeQuery();
            resultSet.next();
            if(resultSet.getInt(1) <= 0) {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
