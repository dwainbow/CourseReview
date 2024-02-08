package edu.virginia.sde.reviews;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EditReviewController {

    @FXML
    private TextField Rating;

    @FXML
    private TextField Comment;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteReview;

    @FXML
    private Button updateReview;

    @FXML
    private Label result;


    private Review review;
    private String username;
    private Course course;

    CourseReviewsService service;

    @FXML
    private Text avgRating;
    public void initialize(Review review,String username,Course course) {
        try {
            this.result.setVisible(false);
            this.review = review;

            this.Comment.setText(review.getMessage());
            this.Rating.setText(String.valueOf((review.getRating())));

            this.username= username;
            this.course = course;
            this.service = new CourseReviewsService(review.getCourse());
        
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteReview(ActionEvent event) {
        this.service.deleteReview(this.review);
        this.backButton.fire();
    }

    public void updateReview(ActionEvent event) {

        this.service.deleteReview(this.review);
        if (!this.validateInputs()){
            this.result.setVisible(true);
            this.result.setText("Error: Invalid review");
            this.result.setTextFill(Paint.valueOf("red"));
            return;
        }
        this.result.setVisible(true);
        this.result.setText("Updated review successfully!");
        this.result.setTextFill(Paint.valueOf("green"));
        int rating = Integer.parseInt(Rating.getText());
        Review rev = new Review(this.Comment.getText(), this.username, this.review.getCourse(), rating);
        this.service.addReview(rev);
        this.review = rev;
    }

    private boolean validateInputs(){
        if (!isAllDigits(Rating.getText())){
            return false;
        }
        if(Rating.getText().isEmpty())
        {
            return false;
            
        }
        return (Integer.parseInt(Rating.getText()) > 0 && Integer.parseInt(Rating.getText()) < 6);
    }

    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("course-reviews.fxml"));
        Parent root = loader.load();
        CourseReviewsController courseReviewsController = loader.getController();
        courseReviewsController.initialize(this.course,username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();        
        stage.setScene(new Scene(root));
        stage.show();

    }

    public boolean isAllDigits(String s) {
        for(int i = 0; i < s.length(); i++) {
            if(!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }



}
