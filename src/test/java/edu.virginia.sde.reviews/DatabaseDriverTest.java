package edu.virginia.sde.reviews;

import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseDriverTest {
    DatabaseDriver db;

    @BeforeAll
    public void setup() throws SQLException {
        System.out.println("Setup called");
        this.db = new DatabaseDriver("test_db.sqlite");
        db.connect();
        db.createTables();
        System.out.println("Setup complete");
    }

    @BeforeEach
    public void clear() throws SQLException {
        db.clearTables();
    }

    @AfterAll
    public void drop() throws SQLException {
        db.disconnect();
    }

    @Test
    @DisplayName("Added course exists")
    public void testAddCourse() throws SQLException {
        Course c = new Course("CS", 3140, "sde");
        db.addCourse(c);
        assertEquals(db.getCourseById(3140).orElseThrow(), c);
    }

    @Test
    public void testAddReviews() throws SQLException {
        Course sde = new Course("CS", 2130, "sde");
        db.addCourse(sde);
        db.addUser("Cole", "password");
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        assertTrue(db.addReview("Was a class", "Cole", "sde", timeStamp, 5));
        assertEquals(5, db.getCourseRating(sde));
    }

    @Test
    public void getCourseRatingNoRating() throws SQLException{
        Course english = new Course("TEST", 1000, "testing course");
        db.addCourse(english);
        assertEquals(-1, db.getCourseRating(english));
    }
}
