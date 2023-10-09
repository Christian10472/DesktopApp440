package com.example.desktopapp440;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SIgnUpController {

    private String password = "root";
    private String sqlPass ;
    private boolean allGood;
    private Stage stage;
    private Scene scene;
    @FXML
    private Label FirstText, UsernameText, EmailText, PasswordText;
    @FXML
    private TextField FirstUser, LastUser, UsernameUser, EmailUser, PasswordUser, PasswordConfirmUser;
    @FXML
    private CheckBox TermsCheck;
    @FXML
    private Button SignUp;

    @FXML
    protected void onTermsCheckOnClick(){
        if (TermsCheck.isSelected()){
            SignUp.setDisable(false);
        }else{
            SignUp.setDisable(true);
        }
    }
    @FXML
    protected void onSignUpClick(ActionEvent event) throws IOException {
        allGood = true;
        //Checking if any parameters are null
        isUserPropmptEmpty();
        doPasswordsMatch();
        //checking if parameters are valid to database
        if(allGood == true){
            doesUserExist();
            if(allGood == true){
                addUser();
                SuccessScreen(event);
            }

        }

    }

    public void isUserPropmptEmpty(){
        FirstText.setText("");
        UsernameText.setText("");
        EmailText.setText("");
        PasswordText.setText("");
        if((isNull(FirstUser) == true) || (isNull(LastUser) == true)){
            FirstText.setText("*Please enter a valid name");
        }
        if(isNull(UsernameUser) == true){
            UsernameText.setText("*Please enter a valid Username");
        }
        if(isNull(EmailUser) == true){
            EmailText.setText("*Please enter a valid email address");
        }
        if(isNull(PasswordUser) == true){
            PasswordText.setText("*Please enter a valid email password");
        }
    }

    public boolean isNull(TextField x){
        if (x.getText().isEmpty()){
            allGood = false;
            return true;
        }
        return false;
    }

    public void doesUserExist(){
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/desktopappdb", "root", sqlPass);
            String SQL = "SELECT * FROM Users WHERE Username = ?;";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, UsernameUser.getText());
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                UsernameText.setText("*Username is already taken");
                allGood = false;
            }

            SQL = "SELECT * FROM Users WHERE Email = ?;";
            pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, EmailUser.getText());
            rs = pstmt.executeQuery();
            if(rs.next()) {
                EmailText.setText("*Email is already in use");
               allGood = false;
            }
        } catch (Exception e) {
            System.out.println("Error connecting to database");
            System.out.println(e.getMessage());
            allGood = false;
        }
    }

    public void doPasswordsMatch(){
        if ((PasswordUser.getText()).equals(PasswordConfirmUser.getText()) == false) {
            PasswordText.setText("*Passwords do not match");
            allGood = false;
        }
    }

    public void addUser(){
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/desktopappdb", "root", sqlPass);
            String SQL = "INSERT INTO Users(Username, Password, FirstName, LastName, Email) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, UsernameUser.getText());
            pstmt.setString(2, PasswordUser.getText());
            pstmt.setString(3, FirstUser.getText());
            pstmt.setString(4, LastUser.getText());
            pstmt.setString(5, EmailUser.getText());
            pstmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error inserting into table");
            System.out.println(e.getMessage());
            allGood = false;
        }
    }

        public void SuccessScreen(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("SignUpSuccess.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
}
