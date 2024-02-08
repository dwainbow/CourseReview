package edu.virginia.sde.reviews;

import java.util.Objects;

public class Review {
    private final String message;
    private final String author;
    private final Course course;
    private final int rating;
    private final String timeStamp;
    private  final String subject;
    private  final int courseNumber;
    private  final String courseTitle;

    public Review(String message, String author, Course course, int rating) {
        this.message = message;
        this.author = author;
        this.course = course;
        this.rating = rating;
        this.subject = course.getMnemonic();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getCourseTitle();

        
        this.timeStamp = Integer.toString((int)(System.currentTimeMillis()));
    }

    public Review(String message, String author, Course course, int rating, String timeStamp){
        this.message = message;
        this.author = author;
        this.course = course;
        this.rating = rating;
        this.timeStamp = timeStamp;
        this.subject = course.getMnemonic();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getCourseTitle();

    }
    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public Course getCourse() {
        return course;
    }

    public int getRating() {
        return rating;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getSubject() {
        return subject;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return rating == review.rating && Objects.equals(message, review.message) && Objects.equals(author, review.author) && Objects.equals(course, review.course) && Objects.equals(timeStamp, review.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, author, course, rating, timeStamp);
    }
}
