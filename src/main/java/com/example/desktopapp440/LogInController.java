package com.example.desktopapp440;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;

public class LogInController {
    private String Username_Input;
    private String Password_Input;
    private String SelectStatement = "select username , Password from users where username = ? and Password = ?";

    private String sqlUser = "root";
    private String sqlPass = "BonkRipper420$";

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
        if(ValidateInput()==true){
            System.out.println("success");
        }
    }

    private boolean ValidateInput() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/desktopappdata", sqlUser, sqlPass);
             PreparedStatement preparedStatement = connection.prepareStatement(SelectStatement)) {
            preparedStatement.setString(1, UserName_Field.getText());
            preparedStatement.setString(2, Password_Field.getText());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {

                if (UserName_Field.getText().equals(resultSet.getString("Username")) && Password_Field.getText().equals(resultSet.getString("Password"))) {
                    UserName_Field.setText("Success data connection");

                    return true;
                }
            }


        } catch (SQLException e) {
            System.out.println("error");
        }

        return false;
    }
}