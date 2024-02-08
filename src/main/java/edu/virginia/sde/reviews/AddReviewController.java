package edu.virginia.sde.reviews;

import java.io.IOException;

import javax.swing.Action;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AddReviewController {

    public TextField title;
    CourseReviewsService service;
    Course course;
    String username;

    @FXML
    public Button back_from_reviews;

    @FXML
    private Button addReview;

    @FXML
    private TextField comment;

    @FXML
    private TableColumn<Review, String> comments;

    @FXML
    private TextField rating;

    @FXML
    private Label resultsDisplay;
    
    public void initialize(Course course, String username) {
        this.service = new CourseReviewsService(course,username);
        this.username = username;
        this.course = course;
    }

    public void goBack(ActionEvent event) throws IOException
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("course-reviews.fxml"));
        Parent root = loader.load();
        CourseReviewsController courseReviewsController = loader.getController();
        courseReviewsController.initialize(course,username);
        Stage stage =  (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }


    public void handleAddReviewButton(ActionEvent event) {
        resultsDisplay.setVisible(true);
        try {
            validateInputs();
            Review review = new Review(comment.getText(), username, course, Integer.parseInt(rating.getText().trim()));
            if(service.addReview(review))
            {
                resultsDisplay.setText("Review added successfully");

            }
            else
            {
                resultsDisplay.setText("You have already reviewed this course");
            }
        } catch (Exception e) {
        }

    }

    private void validateInputs()
    {
        if (rating.getText().isEmpty())
        {
            resultsDisplay.setText("Please fill out all required fields");
            throw new IllegalArgumentException();
        }

        {
            int rating = Integer.parseInt(this.rating.getText());
            if (rating < 1 || rating > 5)
            {
                resultsDisplay.setText("Rating must be between 1 and 5");
                throw new IllegalArgumentException();
            }
        }
    }

}