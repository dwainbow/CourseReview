package edu.virginia.sde.reviews;

import java.util.ArrayList;

public class Course {
    private String mnemonic;
    private int courseNumber;
    private String courseTitle;
    private double avgRating;
    private ArrayList<Review> reviews;
    

    public Course(String mnemonic, int courseNumber, String courseTitle) {
        this.mnemonic = mnemonic.toUpperCase();
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle; 
        setAvgRating();

    }

    public String getMnemonic() {
        return mnemonic;
    }
    public int getCourseNumber() {
        return courseNumber;
    }
    public String getCourseTitle() {
        return courseTitle;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(){
        CourseReviewsService service = new CourseReviewsService(this);
        this.avgRating = service.getRating();
    }
    
    


    public String toString() {
        return mnemonic + " " + courseNumber + ": " + courseTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return course.equals(o);
    }


}
