package com.example.desktopapp440;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;

public class LogInController {

     String SelectStatement = "select Username , Password from users where Username = ? and Password = ?";

     String sqlUser = "root";
     String sqlPass = "BonkRipper420$";
    String url = "jdbc:mysql://localhost:3306/desktopappdb";

    @FXML
    Button LoginButton;
    @FXML
    Button SignUpButton;
    @FXML
    TextField UserName_Field;
    @FXML
    PasswordField Password_Field;


    @FXML
    public void onLogInButtonClick() {
        if(ValidateInput()){
            System.out.println("all good");
        }
    }

    private boolean ValidateInput() {
        try (
            Connection connection = DriverManager.getConnection(url, sqlUser, sqlPass)){
            System.out.println("DBConnected");
            PreparedStatement preparedStatement = connection.prepareStatement(SelectStatement);

            String UsernameInput = UserName_Field.getText();
            String PasswordInput = Password_Field.getText();

            preparedStatement.setString(1, UsernameInput);
            preparedStatement.setString(2, PasswordInput);


            System.out.println("Connection");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                    return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
        }

        return false;
    }
}