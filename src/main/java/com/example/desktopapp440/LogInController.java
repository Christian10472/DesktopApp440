package com.example.desktopapp440;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogInController {
    private String SelectStatement = "select* from users where Username = ? and Password = ?";
    private String sqlUser = "root";
    private String sqlPass = "root";
    private String url = "jdbc:mysql://localhost:3306/desktopappdb";


    @FXML
    public Button LoginButton;
    @FXML
    public  Button SignUpButton;
    @FXML
    public TextField UserName_Field;
    @FXML
    public PasswordField Password_Field;
    @FXML
    public Label ErrorLabel;


    @FXML
    public void onLogInButtonClick(ActionEvent event) throws IOException {
        if(ValidateInput()){
            System.out.println("all good");
            Parent root = FXMLLoader.load(getClass().getResource("Home_Page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        ErrorLabel.setText("*Incorrect Username or Password*");
    }

    @FXML
    public void onSignUpButtonClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SIgn_Up.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private boolean ValidateInput() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/desktopappdb", sqlUser,sqlPass);

            System.out.println("DBConnected");
            ResultSet resultSet;
             PreparedStatement preparedStatement = con.prepareStatement(SelectStatement) ;

                String UsernameInput = UserName_Field.getText();
                String PasswordInput = Password_Field.getText();

                preparedStatement.setString(1, UsernameInput);
                preparedStatement.setString(2, PasswordInput);


                System.out.println("Connection");
                resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                if( (UsernameInput.equals(resultSet.getString("Username"))) && PasswordInput.equals(resultSet.getString("Password "))) {
                    UserGetterNSetter User;
                    User = new UserGetterNSetter();
                    User.setUsername(resultSet.getString(("Username")));
                    User.setPassword(resultSet.getString("Password"));
                    User.setFirstName(resultSet.getString("FirstName"));
                    User.setLastName(resultSet.getString("LastName"));
                    User.setEmail(resultSet.getString("Email"));
                    return true;
                }
            }


        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return false;
    }
}