package edu.virginia.sde.reviews;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseSearchService {

    DatabaseDriver db;
    public CourseSearchService(){
        this.db = Config.getInstance().db;
    }

    public List<Course> searchByNumber (int x){
        try {
            return db.getCoursesById(x);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> getCourse(String mnemonic, int number, String title){
        try {
            return db.getCourse(title, mnemonic, number);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> searchByMnemonnic(String mnemonic){
        try {

            return db.getCoursesByMnemonic(mnemonic);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> getAllCourses(){
        try {
            return db.getAllCourses();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> searchByMnemonicTitle(String mnemonic, String title){
        try {
            return db.getCoursesByMnemonicTitle(mnemonic, title);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> searchByMnemonicId(String mnemonic, int number){
        try {
            return db.getCoursesByMnemonicId(mnemonic, number);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> searchByIdTitle(String title, int number){
        try {
            return db.getCoursesByIdTitle(title, number);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public  List<Course> searchByTitle(String title){
        try {
            return db.getCoursesByTitle(title);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void addCourse(Course c){
        try {
            db.addCourse(c);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public float getRating(Course c){
        try {
            return db.getCourseRating(c);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
