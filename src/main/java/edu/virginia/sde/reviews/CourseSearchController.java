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
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class CourseSearchController {

    CourseSearchService courseSearchService = new CourseSearchService();
    private String username;
    private Course selectedCourse;

    @FXML
    private TextField subject;
    @FXML
    private TableColumn<Course, String> subjectColumn;

    @FXML
    private TextField courseNum;
    @FXML
    private TableColumn<Course, Integer> numColumn;

    @FXML
    private TextField courseTitle;

    @FXML
    private TableColumn<Course, String> titleColumn;
    @FXML
    private TableColumn<Course,Double> ratingColumn;

    @FXML
    private Button myReviews;

    @FXML
    private Button logout;

    @FXML
    private Button search;

    @FXML
    private Button addCourse;
    @FXML
    private Label resultsDisplay;
    @FXML
    private TableView<Course> table;



    public void initialize(String username) {
        try {
            this.username = username;
            intializeTable();
            addOnClick();
            updateTable();
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }

    private void intializeTable()
    {
        ObservableList<Course> courses = FXCollections.observableArrayList();
        resultsDisplay.setVisible(false);
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
        numColumn.setCellValueFactory(new PropertyValueFactory<>("courseNumber"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("avgRating"));
        ratingColumn.setCellFactory(column -> new TableCell<Course, Double>() {
            @Override
            protected void updateItem(Double rating, boolean empty) {
                super.updateItem(rating, empty);

                if (empty || rating == null) {
                    setText(null); // Display nothing if the rating is null or empty
                } else if (rating == -1) {
                    setText(""); // Display blank if the rating is -1
                } else {
                    // Display the rating with 2 digits after the decimal point
                    setText(String.format("%.2f", rating));
                }
            }
        });
        table.setItems(courses);
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
        //TODO: ideally i would want to stay in the same window when opening the course review rather than opening a new window,
        // but i can't find a good solution other than using a singleton controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("course-reviews.fxml"));
        Parent root = loader.load();
        CourseReviewsController courseReviewsController = loader.getController();
        courseReviewsController.initialize(selectedCourse,username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();        
        stage.setScene(new Scene(root));
        stage.show();
        
    }

    public void logout(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void addCourse(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add-Scene.fxml"));
        Parent root = loader.load();
        AddCourseController addCourseController = loader.getController();
        addCourseController.initialize(username);
        Stage stage =  (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void myReviews(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("my-reviews.fxml"));
        Parent root = loader.load();
        MyReviewsController myReviewsController = loader.getController();
        myReviewsController.initialize(this.username);
        Stage stage =  (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void search(ActionEvent e) throws IOException, SQLException {
        try {
            resultsDisplay.setVisible(false);
            validateInputs();
            searchHelper();
            
        } catch (Exception error) {
            resultsDisplay.setVisible(true);
            resultsDisplay.setText(error.getMessage());
        }

    }

    private void searchHelper()
    {
        String courseSubject = subject.getText().strip().toUpperCase();
        String courseNumber = courseNum.getText().strip();
        String title = courseTitle.getText().strip();
        List results;

        if (courseSubject.isEmpty() && courseNumber.isEmpty() && title.isEmpty()) {
            results = courseSearchService.getAllCourses();
            update(results, table);
        }  
        if (!courseSubject.isEmpty() && !courseNumber.isEmpty() && !title.isEmpty()) {
            results = courseSearchService.getCourse(courseSubject, Integer.parseInt(courseNumber), title);
            update(results, table);
        }   
        
        if (!courseSubject.isEmpty() && courseNumber.isEmpty() && title.isEmpty()) {
            results = courseSearchService.searchByMnemonnic(courseSubject);
            update(results, table);
        }
        if (!courseNumber.isEmpty() && courseSubject.isEmpty() && title.isEmpty()) {
            results = courseSearchService.searchByNumber(Integer.parseInt(courseNumber));
            update(results, table);
        }
        if (!title.isEmpty() && courseNumber.isEmpty() && courseSubject.isEmpty()) {
            results = courseSearchService.searchByTitle(title);
            update(results, table);
        }

        if(!courseSubject.isEmpty() && !courseNumber.isEmpty() && title.isEmpty())
        {
            results = courseSearchService.searchByMnemonicId(courseSubject, Integer.parseInt(courseNumber));
            update(results, table);
        }
        
        if(!courseSubject.isEmpty() && courseNumber.isEmpty() && !title.isEmpty())
        {
            results = courseSearchService.searchByMnemonicTitle(courseSubject, title);
            update(results, table);
        }
        if(courseSubject.isEmpty() && !courseNumber.isEmpty() && !title.isEmpty())
        {
            results = courseSearchService.searchByIdTitle(title, Integer.parseInt(courseNumber));
            update(results, table);
        }

    }
    private void validateInputs() {
        
        var mnemonic = subject.getText();
        if (!mnemonic.isEmpty() && (mnemonic.length() > 4 || mnemonic.length() < 2 || !isAllCharacters(mnemonic)) ){
            
            throw new IllegalArgumentException("Invalid Subject");

        }


        if(courseNum.getText().isEmpty()){
            return;
        }

        if(!courseNum.getText().isEmpty())
        {
            try {
                var courseNumber = Integer.parseInt(courseNum.getText());           
            } catch (Exception e) {
                throw new IllegalArgumentException("Course Number must be a number");
            }
        }
        
        var courseNumber = Integer.parseInt(courseNum.getText());
        if (courseNumber < 1000 || courseNumber > 9999 )
        {
                throw new IllegalArgumentException("Course Number must be between 1000 and 9999");
        }
        
        var title = courseTitle.getText();
        
        if (title.length() > 50) {
            throw new IllegalArgumentException("Invalid Course Title");
        }
        resultsDisplay.setText("Course added successfully!");
    
        
    }

    // needs to show errors when searching 

    private void updateTable()
    {
        update(courseSearchService.getAllCourses(), table);
    }
    private boolean isAllCharacters(String s)
    {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLetter(s.charAt(i)) || !Character.isLetterOrDigit(s.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }

    static void update(List<Course> allCourses, TableView<Course> table) {
        ObservableList<Course> tableCourses= FXCollections.observableArrayList();
        try {
            tableCourses.addAll(allCourses);
            table.getItems().clear();
            table.getItems().addAll(tableCourses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
