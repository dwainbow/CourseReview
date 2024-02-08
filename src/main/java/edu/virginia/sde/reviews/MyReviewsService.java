package edu.virginia.sde.reviews;

import java.sql.SQLException;
import java.util.List;

public class MyReviewsService {

    private static DatabaseDriver db;
    private static String user;
    public MyReviewsService(String user){
        this.db = Config.getInstance().db;
        this.user = user;
    }

    public List<Review> getMyReviews(){
        try {
            return db.getStudentsReviews(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
