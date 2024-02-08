package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MyReviewsController {

    @FXML
    private Button backButton;

    private MyReviewsService service;

    private String username;

    private Review selectedCourse;


    @FXML
    private TableColumn<Course, Integer> courseNumberColumn;

    @FXML
    private TableColumn<Course, Float> courseRatingColumn;


    @FXML
    private TableColumn<Course, String> courseTitleColumn;

    @FXML
    private TableColumn<Course, String> courseReviewComment;


    @FXML
    private TableColumn<Course, String> subjectColumn;

    @FXML
    private TableView<Review> table;

    // addReview (String comment, String author, String title, String dateTime, int rating)
    //TODO: update page with user's reviews
    public void initialize(String user) {

        try {
            this.username= user;
            this.service = new MyReviewsService(username);
            ObservableList<Review> tableReviews = FXCollections.observableArrayList();
            courseRatingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
            courseReviewComment.setCellValueFactory(new PropertyValueFactory<>("message"));
            subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
            courseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("courseNumber"));
            courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
            table.setItems(tableReviews);
            addOnClick();
            updateTable();
            table.setPlaceholder(new Label("You haven't made any reviews yet"));

        } catch (Exception e) {
        }
    }

    private void addOnClick()
    {
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { 
                this.selectedCourse = table.getSelectionModel().getSelectedItem();
                if (selectedCourse != null) {
                    try {
                        showCourseReview(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
    private void showCourseReview(MouseEvent event) throws IOException
    {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("course-reviews.fxml"));
        Parent root = loader.load();
        CourseReviewsController courseReviewsController = loader.getController();
        courseReviewsController.initialize(selectedCourse.getCourse(),username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();        
        stage.setScene(new Scene(root));
        stage.show();
        
    }
    


    public void showCourseReview(Review review) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        CourseSearchController courseSearchController = loader.getController();
        courseSearchController.initialize(username);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    //Basic Idea: each review written by a certain user, stored in currentUser variable, should be added to table and displayed

    public void back(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        Parent root = loader.load();
        CourseSearchController courseSearchController = loader.getController();
        courseSearchController.initialize(username);
        Stage stage =  (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void updateTable()
    {
        update(service.getMyReviews(), table);
        
    }

     void update(List<Review> allReviews, TableView<Review> table2) {
        ObservableList<Review> tableReviews= FXCollections.observableArrayList();
        try {
            tableReviews.addAll(allReviews);
            table2.getItems().clear();
            table2.getItems().addAll(tableReviews);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
