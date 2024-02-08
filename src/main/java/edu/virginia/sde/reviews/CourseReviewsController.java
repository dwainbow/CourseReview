package edu.virginia.sde.reviews;

import java.io.IOException;
import java.util.List;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CourseReviewsController {

    private Course course;
    private String username;
    private CourseReviewsService service;
    private Review selectedReview;

    @FXML
    private Button backButton;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button addReview;

    @FXML
    private TableColumn<Review, Review> commentsColumn;

    @FXML
    private TableColumn<Review, Integer> ratingColumn;

    @FXML
    private TableColumn<Review, Review> timeColumn;

    @FXML
    private TableView<Review> table;

    @FXML
    private Text courseTitle;

    @FXML
    private Text avgRating;


    public void initialize(Course course,String username ) {
        try {
            this.course = course;
            this.username= username;
            
            this.service = new CourseReviewsService(course);
            courseTitle.setText(course.getMnemonic() + " " + course.getCourseNumber() + ": " + course.getCourseTitle());
            if(service.getRating() == -1.0){
                avgRating.setText("No ratings available");
            }
            else{
                String formattedRating = String.format("%.2f", service.getRating());
                avgRating.setText("Average rating: " + formattedRating);
            }
            initializeTable();
            addOnClick();
            update(service.getAllReviews(course), table);
            table.setPlaceholder(new Label("this course has no reviews"));

            
        
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void addOnClick()
    {
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { 
                this.selectedReview= table.getSelectionModel().getSelectedItem();
                if (selectedReview != null) {
                    try {
                        showEditReview(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void showEditReview(MouseEvent event) throws IOException
    {

        if (!username.equals(selectedReview.getAuthor())){
            this.alert();
        }
        else{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-review.fxml"));
            Parent root = loader.load();
            EditReviewController editReviewController = loader.getController();
            editReviewController.initialize(selectedReview,username,course);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }

    }

    private void alert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Error- you are not the author of this review!");
        alert.showAndWait();

    }
    private void initializeTable(){
    ObservableList<Review> reviews = FXCollections.observableArrayList();
            commentsColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
            ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));
            table.setItems(reviews);
    }

    public void backButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        Parent root = loader.load();
        CourseSearchController courseSearchController = loader.getController();
        courseSearchController.initialize(username);
        Stage stage =  (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void addReview(ActionEvent event) throws IOException {
       FXMLLoader loader = new FXMLLoader(getClass().getResource("add-review.fxml"));
       Parent root = loader.load();
       AddReviewController addReviewController = loader.getController();
       Stage stage =  (Stage) ((Node) event.getSource()).getScene().getWindow();
       Scene scene = new Scene(root);
       addReviewController.initialize(this.course, this.username);
       stage.setScene(scene);
       stage.show();
    }

    static void update(List<Review> allCourses, TableView<Review> table) {
        ObservableList<Review> tableCourses= FXCollections.observableArrayList();
        try {
            tableCourses.addAll(allCourses);
            table.getItems().clear();
            table.getItems().addAll(tableCourses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}