package edu.virginia.sde.reviews;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DatabaseDriver {
    private final String sqliteFilename;
    private Connection connection;

    public static void main(String[] args) throws SQLException {
        DatabaseDriver driver = new DatabaseDriver("database.sqlite");
        driver.connect();
        driver.createTables();
        driver.clearTables();

        Course sde = new Course("CS", 2130, "sde");
        driver.addCourse(sde);
        driver.addUser("Cole", "password");

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println(driver.addReview("Was a class", "Cole", "sde",  timeStamp, 5));
    }
    public DatabaseDriver (String sqlListDatabaseFilename) {
        this.sqliteFilename = sqlListDatabaseFilename;
    }

   
    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new IllegalStateException("The connection is already opened");
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFilename);
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        connection.setAutoCommit(false);
    }
    public void commit() throws SQLException {
        connection.commit();
    }
    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public void createTables() throws SQLException {
        var statement = connection.createStatement();
        var userSQL = "CREATE TABLE IF NOT EXISTS USERS(USERNAME TEXT NOT NULL PRIMARY KEY," +
                      "PASSWORD TEXT NOT NULL)";
        var courseSQL = "CREATE TABLE IF NOT EXISTS COURSES(TITLE TEXT PRIMARY KEY NOT NULL, MNEMONIC TEXT NOT NULL, NUMBER INTEGER NOT NULL)";
        var reviewSQL = "CREATE TABLE IF NOT EXISTS REVIEWS(COMMENT TEXT, AUTHOR TEXT NOT NULL, TITLE TEXT NOT NULL, RATING INTEGER NOT NULL, TIMESTAMP TEXT NOT NULL, FOREIGN KEY(AUTHOR) REFERENCES USERS(USERNAME) ON DELETE CASCADE, FOREIGN KEY (TITLE) REFERENCES COURSES(TITLE) ON DELETE CASCADE)";
        statement.executeUpdate(userSQL);
        statement.executeUpdate(courseSQL);
        statement.executeUpdate(reviewSQL);
        connection.commit();
    }
    //maybe add a list of courses to help?




    //Q: Do title, mnemonic, and number all have to be unique?
    public void addCourse(Course course) throws SQLException {
        try {
            var statement = connection.createStatement();
            var sql = String.format("INSERT INTO COURSES(TITLE, MNEMONIC, NUMBER) VALUES (\"%s\",\"%s\",%d)",course.getCourseTitle(),course.getMnemonic(),course.getCourseNumber());
            statement.executeUpdate(sql);
            connection.commit();
            
        } catch (SQLException e) {
            rollback();
            //disconnect();
            throw e;
        }
    }

    public List<Course> getCourse(String title, String mnemonic, int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Courses WHERE TITLE = (?) AND MNEMONIC = (?) AND NUMBER = (?)");
       statement.setInt(3, id);
        statement.setString(2, mnemonic);
        statement.setString(1, title);
        var rs = statement.executeQuery();
        return coursesFromResultSet(rs);
    }

    public void addUser(String username, String password) throws SQLException, IllegalArgumentException{
        try {
            if(password.length() < 8){
                throw new IllegalArgumentException("Password must be at least 8 characters long");
                
            }
            var statement = connection.createStatement();
            var sql = String.format("INSERT INTO USERS(USERNAME, PASSWORD) VALUES (\"%s\",\"%s\")",username,password);
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            //maybe put username already exists here
            rollback();
            throw e;
        }
    }


    public boolean addReview(Review review) throws SQLException{
        return addReview(review.getMessage(), review.getAuthor(), review.getCourse().getCourseTitle(), review.getTimeStamp(), review.getRating());
    }
    public boolean addReview (String comment, String author, String title, String dateTime, double rating) throws SQLException{
        //var authorHasReviewed = connection.prepareStatement("SELECT * FROM REVIEWS WHERE AUTHOR=(?)");
        //authorHasReviewed.setString(1, author);
        //var q = authorHasReviewed.executeQuery();
        //if (q.next()){
            //return false;
        //}

        var sql = connection.prepareStatement("INSERT INTO REVIEWS(COMMENT, AUTHOR, TITLE, RATING, TIMESTAMP) VALUES (?, ?, ?, ?, ?)");
        sql.setString(1, comment);
        sql.setString(2, author);
        sql.setString(3, title);
        sql.setDouble(4, rating);
        sql.setString(5, dateTime);
        sql.executeUpdate();

        commit();
        return true;
    }


    //Returns -1 if rating doesn't exist
//    public float getCourseRating(Course c) throws SQLException{
//        var sql = connection.prepareStatement("SELECT AVG(RATING) from REVIEWS where TITLE = (?)");
//        sql.setString(1, c.getCourseTitle());
//        var rs = sql.executeQuery();
//        if(rs.wasNull()){
//            return -1;
//        }
//        if (!rs.next()){
//            return -1;
//        }
//        return rs.getInt(1);
//    }
    public float getCourseRating(Course c) throws SQLException {
        var sql = connection.prepareStatement("SELECT AVG(RATING) AS AVG_RATING FROM REVIEWS WHERE TITLE = (?)");
        sql.setString(1, c.getCourseTitle());
        var rs = sql.executeQuery();

        if (rs.next()) {
            // Check if the result is NULL
            float averageRating = rs.getFloat("AVG_RATING");
            if (rs.wasNull()) {
                return -1;
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            return Float.parseFloat(decimalFormat.format(averageRating));
        }

        return -1;
    }

    public boolean isUsernameAvailable(String username) throws  SQLException{
        var sql = connection.prepareStatement("SELECT * FROM USERS WHERE USERNAME = (?)");
        sql.setString(1, username);
        var rs = sql.executeQuery();
        return !rs.next();
    }
    public boolean checkUserExist(String username, String password) throws SQLException{

        if (username == (null) || password == (null)){
            return false;
        }
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT PASSWORD FROM USERS WHERE USERNAME = (?)");
        preparedStatement.setString(1, username);
        ResultSet res = preparedStatement.executeQuery();

        return (res.next() && Objects.equals(res.getString("Password"), password));
    }

    public boolean checkUserExist(String username) throws SQLException{
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE USERNAME = (?)");
        preparedStatement.setString(1, username);
        ResultSet res = preparedStatement.executeQuery();
        if(res.next()){
            return true;
        }
        return false;
    }


    public List<Course> getAllCourses() throws SQLException {
        var statement = connection.createStatement();
        var rs = statement.executeQuery("SELECT * FROM COURSES");
        return coursesFromResultSet(rs);
    }


    public Optional<Course> getCourseById(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT MNEMONIC, NUMBER, TITLE FROM Courses WHERE NUMBER = (?)");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        String mnemonic = rs.getString("Mnemonic");
        if (mnemonic == null){
            return Optional.empty();
        }
        int courseNumber = rs.getInt("Number");
        String courseTitle = rs.getString("Title");

        return Optional.of(new Course(mnemonic, courseNumber, courseTitle));
        
    }

    public List<Course> getCoursesById(int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Courses WHERE NUMBER = (?)");
        statement.setInt(1, id);
        var rs = statement.executeQuery();
        return coursesFromResultSet(rs);
    }

    
    


    public List<Course> getCoursesByMnemonic(String mnemonic) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Courses WHERE Mnemonic = (?)");
        statement.setString(1, mnemonic);
        var rs = statement.executeQuery();
        return coursesFromResultSet(rs);
    }

    public List<Course> getCoursesByMnemonicTitle(String mnemonic,String title) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Courses WHERE Mnemonic = (?) and Title LIKE (?)");
        statement.setString(1, mnemonic);
        statement.setString(2, "%" + title + "%");
        var rs = statement.executeQuery();
        return coursesFromResultSet(rs);
    }

    public List<Course> getCoursesByMnemonicId(String mnemonic,int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Courses WHERE Mnemonic = (?) and NUMBER = (?)");
        statement.setString(1, mnemonic);
        statement.setInt(2, id);
        var rs = statement.executeQuery();
        return coursesFromResultSet(rs);
    }

    public List<Course> getCoursesByIdTitle(String title,int id) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Courses WHERE TITLE = (?) and NUMBER = (?)");
        statement.setString(1, title);
        statement.setInt(2, id);
        var rs = statement.executeQuery();
        return coursesFromResultSet(rs);
    }



    public List<Course> getCoursesByTitle(String title) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Courses WHERE TITLE LIKE ?");
        statement.setString(1, "%" + title + "%");

        var rs = statement.executeQuery();
        return coursesFromResultSet(rs);
    }

    public List<Review> getReviewsForCourse(Course course) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Reviews WHERE Title=(?)");
        statement.setString(1, course.getCourseTitle());
        var rs = statement.executeQuery();
        return reviewsFromResultSet(rs);
    }

    public List<Review> getStudentsReviews(String studentUsername) throws SQLException{
        var statement = connection.prepareStatement("SELECT * FROM Reviews WHERE AUTHOR=(?)");
        statement.setString(1, studentUsername);
        var rs = statement.executeQuery();
        return reviewsFromResultSet(rs);
    }

    public boolean hasReviewedCourse(String author, String course) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Reviews WHERE Title=(?) AND AUTHOR=(?)");
        statement.setString(1, course);
        statement.setString(2, author);
        var rs = statement.executeQuery();
        return rs.next();
    }
    public void deleteReview(Review review) throws SQLException {
        deleteReview(review.getAuthor(), review.getCourse().getCourseTitle());
    }

    public void deleteReview(String author, String title) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM REVIEWS WHERE AUTHOR=(?) AND TITLE=(?)");
        statement.setString(1, author);
        statement.setString(2, title);
        statement.executeUpdate();
        commit();
    }
    private List<Review> reviewsFromResultSet(ResultSet rs) throws SQLException{
        List<Review> toRet = new ArrayList<>();
        while (rs.next()){
            String timestamp = rs.getString("Timestamp");
            int rating = rs.getInt("Rating");
            String author = rs.getString("Author");
            String comment = rs.getString("Comment");
            String course = rs.getString("Title");
            Course c = this.courseFromTitle(course);

            Review r = new Review(comment, author, c, rating, timestamp);
            toRet.add(r);
        }
        return toRet;
    }

    private Course courseFromTitle(String title) throws SQLException {
        var statement = connection.prepareStatement("SELECT * FROM Courses WHERE TITLE=?");
        statement.setString(1,title);

        var rs = statement.executeQuery();
        var x = coursesFromResultSet(rs);
        if (x.isEmpty()){
            throw new RuntimeException("Invalid course title");
        }
        return x.get(0);
    }
    private List<Course> coursesFromResultSet(ResultSet rs) throws SQLException{
        List<Course> returnedCourses = new ArrayList<>();
        while (rs.next()){
            String mnemonic = rs.getString("Mnemonic");
            int courseNumber = rs.getInt("Number");
            String courseTitle = rs.getString("Title");
            returnedCourses.add(new Course(mnemonic, courseNumber, courseTitle));
        }
        return returnedCourses;
    }

    public void clearTables() throws SQLException {
        connection.setAutoCommit(false);
        var statement = connection.createStatement();
        try {
            statement.executeUpdate("DELETE FROM REVIEWS ");
            statement.executeUpdate("DELETE FROM COURSES");
            statement.executeUpdate("DELETE FROM USERS");
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw e;        
        }
    }
}



