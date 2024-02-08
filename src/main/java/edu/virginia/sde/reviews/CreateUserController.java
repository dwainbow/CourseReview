package edu.virginia.sde.reviews;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class CreateUserController {

    @FXML
    private Label create_user_result;

    @FXML
    private Button create_user;

    @FXML
    private TextField new_username;

    @FXML
    private TextField new_password;

    @FXML
    private Button return_to_login;


    private final LoginService service = LoginService.getInstance();

    public void initialize() {

    }


    public void handleCreateUserButton(ActionEvent event) throws RuntimeException {
        String newUsername = new_username.getText();
        String newPassword = new_password.getText();

        if (newPassword.isEmpty() || newUsername.isEmpty()){
            this.create_user_result.setText("Please enter both username and password");
            this.create_user_result.setTextFill(Paint.valueOf("red"));
            return;
        }
        if (service.usernameExists(newUsername)){
            this.create_user_result.setText("Error: Username is taken");
            this.create_user_result.setTextFill(Paint.valueOf("red"));
            return;
        }
        if (!service.addUser(newUsername, newPassword)){
            this.create_user_result.setText("Error: password too short");
            this.create_user_result.setTextFill(Paint.valueOf("red"));
            return;
        }
        this.create_user_result.setText("User successfully created. Please return to login");
        this.create_user_result.setTextFill(Paint.valueOf("green"));
    }

    public void returnToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent login = loader.load();
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        primaryStage.setScene(new Scene(login));
    }
}
