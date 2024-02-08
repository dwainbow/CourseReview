package edu.virginia.sde.reviews;

import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseSearchServiceTest {
    CourseSearchService courseSearchService;

    @BeforeAll
    public void setup() throws SQLException {
        Config.setupTestDb();
        courseSearchService = new CourseSearchService();
    }

    @BeforeEach
    public void clear() throws SQLException{
        Config.getInstance().db.clearTables();
    }

    @AfterAll
    public void drop() throws SQLException{
        Config.getInstance().db.clearTables();
        Config.getInstance().db.disconnect();
    }

    private List<Course> createCoursesList(){
        List<Course> c = new ArrayList<>();
        c.add(new Course("CS", 3140, "SDE"));
        c.add(new Course("BIO", 3120, "Premed_Bio"));
        c.add(new Course("MATH", 1200, "Nerd_math"));
        c.add(new Course("ECON", 2120, "ECON_2120"));
        c.add(new Course("ECON", 2100, "MICRO"));
        c.add(new Course("CS", 3120, "CSO"));
        return c;
    }

    private void addCourseList() throws SQLException {
        var x = createCoursesList();
        for (Course y: x){
            Config.getInstance().db.addCourse(y);
        }
    }
    @Test
    public void getAllCourse(){
        var c = createCoursesList();

        for (Course course: c){
            courseSearchService.addCourse(course);
        }
        assertIterableEquals(c, courseSearchService.getAllCourses());
    }

   @Test
   //TODO
   public void searchByMnemonnic(){
       var c = createCoursesList();
       var exp = c.stream().filter(t -> Objects.equals(t.getMnemonic(), "CS")).toList();

   }

    @Test
    public void searchByNumber() throws SQLException {
        addCourseList();
        var c = createCoursesList();
        var exp = c.stream().filter(t -> t.getCourseNumber() == 3120).toList();
        assertIterableEquals(exp, courseSearchService.searchByNumber(3120));
    }

}
