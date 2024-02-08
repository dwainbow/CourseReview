package edu.virginia.sde.reviews;
import java.io.IOException;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class AddCourseController{

    private String username;
    private Course selctedCourse;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;

    @FXML
    private TableColumn<Course, Integer> courseNumberColumn;

    @FXML
    private TextField courseNumberID;

    @FXML
    private TableColumn<Course, String> courseTitleColumn;

    @FXML
    private TextField mnemonicID;

    @FXML
    private TableColumn<Course, String> subjectColumn;

    @FXML
    private TextField titleID;
    
    @FXML
    private Label resultsDisplay;
    @FXML
    private TableView<Course> table;

    CourseSearchService service = new CourseSearchService();

    
    public void initialize(String username) {
        try {
            this.username = username;
            resultsDisplay.setVisible(false);
            initializedTable();
            addOnClick();
            updateTable();
        
        } catch (Exception e) {
        }
    }

    private void initializedTable()
    {
        ObservableList<Course> courses = FXCollections.observableArrayList();
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
        courseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("courseNumber"));
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        table.setItems(courses);
    }
    private void addOnClick()
    {
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { 
                this.selctedCourse = table.getSelectionModel().getSelectedItem();
                if (this.selctedCourse != null) {
                    try {
                        showCourseReview(event);
                    } catch (IOException e) {
                        
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
        courseReviewsController.initialize(this.selctedCourse,username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();        
        stage.setScene(new Scene(root));
        stage.show();
        
    }
    public  void courseSearch(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        Parent root = loader.load();
        CourseSearchController courseSearchController = loader.getController();
        courseSearchController.initialize(username);
        Stage stage =  (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    
    
    public void handleAddButton() throws SQLException
    {
        
        try {
            resultsDisplay.setVisible(true);
            validateInputs();
            
            var mnemonic = mnemonicID.getText().strip();
            var courseNumber =Integer.parseInt(courseNumberID.getText().strip()) ;
            var courseTitle = titleID.getText();
            var course = new Course(mnemonic, courseNumber, courseTitle);
            service.addCourse(course);
            updateTable();
        } 
        catch (Exception e) {         
            if(e.getMessage().contains("A PRIMARY KEY constraint failed"))
            {
                resultsDisplay.setText("Course already exists");
            }
            else if(e.getMessage().contains("connection closed"))
            {
                resultsDisplay.setText("Lost connection to database");
                
            }
            else
            {
                resultsDisplay.setText(e.getMessage());
            }
        }
    }
    private void updateTable()
    {
        CourseSearchController.update(service.getAllCourses(), table);
    }
   
  
    private void validateInputs() {
        
        var mnemonic = mnemonicID.getText();
        if(mnemonic.length() ==0)
        {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
        else if (mnemonic.length() > 4 || mnemonic.length() < 2 || !isAllCharacters(mnemonic)) {
            
            throw new IllegalArgumentException("Invalid Subject");
            
        }

        
        if(courseNumberID.getText().isEmpty())
        {
            throw new IllegalArgumentException("Course Number cannot be empty");
        }

        try {
            var courseNumber = Integer.parseInt(courseNumberID.getText());
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Course Number must be a number");
        }

        var courseNumber = Integer.parseInt(courseNumberID.getText());
        if (courseNumber < 1000 || courseNumber > 9999 )
        {
                throw new IllegalArgumentException("Course Number must be between 1000 and 9999");
        }
        
        var courseTitle = titleID.getText();
        if(courseTitle.isEmpty())
        {
            throw new IllegalArgumentException("Course Title cannot be empty");
        }
        
        else if (courseTitle.isEmpty() || courseTitle.length() > 50) {
            throw new IllegalArgumentException("Invalid Course Title");
        }
        resultsDisplay.setText("Course added successfully!");
    
        
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
}