package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    LoginService service = LoginService.getInstance();


    @FXML
    private Button closeButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField userNameField;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button createUserButton;


    private String username;

    public void close(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You're about to exit!");
        alert.setContentText("Are you sure you would like to exit?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.close();
        }

    }
    private void showCourseReviewsScene(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        Parent root = loader.load();
        CourseSearchController courseSearchController = loader.getController();
        courseSearchController.initialize(username);
        //courseSearchController.displayName(userNameField.getText().strip());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void addNewUserScene(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add-user.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void login(ActionEvent e) throws IOException, SQLException {

        String username = userNameField.getText().strip();
        String password = passwordField.getText().strip();
        

        try {
            validateInputs();
            this.username = username;
        } catch (Exception error) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(error.getMessage());
            alert.showAndWait();
            return;
        }
        

        if(!service.usernameExists(username))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username or Password is incorrect");
            alert.showAndWait();
            return;
        }
        
        if(service.isCorrectLoginInfo(username, password)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Login Succesful!");
            alert.showAndWait();
            showCourseReviewsScene(e);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username or Password is incorrect");
            alert.showAndWait();
        }

    }

    private void validateInputs()
    {
        if (userNameField.getText() == null || userNameField.getText().length() == 0) {
            throw new IllegalArgumentException("Username must not be empty");
        }
        if (passwordField.getText() == null || passwordField.getText().length() == 0) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        if (passwordField.getText().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
    }



}
