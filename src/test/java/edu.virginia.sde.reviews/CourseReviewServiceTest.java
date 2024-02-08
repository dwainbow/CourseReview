package edu.virginia.sde.reviews;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseReviewServiceTest {
    Config config;
    DatabaseDriver db;
    CourseReviewsService courseReviewsService;
    @BeforeAll
    public void setup() throws SQLException {
        Config.setupTestDb();
        config = Config.getInstance();
        config.db.clearTables();
        db = config.db;
    }


    @BeforeEach
    public void clear() throws SQLException{
        db.clearTables();
    }
    @AfterAll
    public void close() throws SQLException {
        db.clearTables();
        db.disconnect();
    }

    @Test
    @DisplayName("Test add review")
    public void addReview() throws SQLException {
        Course c = new Course("CS", 2130, "Hell");
        db.addCourse(c);
        db.addUser("coleman", "password");

        courseReviewsService = new CourseReviewsService(c);

        Review r = new Review("This sucks", "coleman", c, 5);
        courseReviewsService.addReview(r);
        assertEquals(courseReviewsService.getAllReviews(c).get(0), r);
    }
    @Test
    @DisplayName("Adding multiple reviews")
    public void addMultipleReviews() throws SQLException {
        Course c = new Course("CS", 2130, "Hell");
        Course d = new Course("CS", 2110, "DSA");
        db.addCourse(c);
        db.addCourse(d);
        db.addUser("coleman", "password");

        courseReviewsService = new CourseReviewsService(c);

        Review r = new Review("This sucks", "coleman", c, 5);
        Review r1 = new Review("This sucks", "coleman", d, 5);
        courseReviewsService.addReview(r);
        courseReviewsService.addReview(r1);
        assertTrue(courseReviewsService.getAllReviews(c).contains(r));
        assertTrue(courseReviewsService.getAllReviews(d).contains(r1));
    }

}
