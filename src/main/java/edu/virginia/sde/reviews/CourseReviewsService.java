package edu.virginia.sde.reviews;

import java.sql.SQLException;
import java.util.List;

public class CourseReviewsService {
    private final DatabaseDriver db = Config.getInstance().db;
    private final Course course;
    private final String student;

    public CourseReviewsService(Course course, String studentUsername) {
        this.course = course;
        try {
            if (db.isUsernameAvailable(studentUsername)) {
                throw new IllegalArgumentException("Student username doesn't exist");
            }
            if (db.getCourseById(course.getCourseNumber()).isEmpty()) {
                throw new IllegalArgumentException("Course not in database");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.student = studentUsername;

    }

    public CourseReviewsService(Course course) {
        this.course = course;
        this.student = null;
    }

    public Course getCourse() {
        return course;
    }

    public float getRating() {
        try {
            return db.getCourseRating(course);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addReview(Review review){

        try {
            if (db.hasReviewedCourse(review.getAuthor(), review.getCourse().getCourseTitle())){
                return false;
            }
            return db.addReview(review);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //Overloaded method bc not sure what's most convenient
    public void deleteReview(Review review){
        try {
            db.deleteReview(review);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void deleteReview(String user, String courseTitle){
        try {
            db.deleteReview(user, courseTitle);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Review> getAllReviews(Course c){
        try {
            return db.getReviewsForCourse(c);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}