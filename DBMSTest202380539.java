import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class DBMSTest202380539 {
    private final DBMSInterface dbms = new DBMS();

    @Test
    public void Test1() {
        Student student1 = new Student("S4412", "Jalal Rudaki", 80, 90);
        dbms.insertStudent(student1);
        Student queryResult1 = dbms.queryByStudentNumber("S4412");
        assertEquals(student1, queryResult1);
    }

    @Test
    public void Test2() {
        Student student1 = new Student("S9762", "Hafez Shirazi", 80, 90);
        dbms.insertStudent(student1);
        List<Student> queryResult1 = dbms.queryByScore(170);
        List<Student> queryMustBe = new ArrayList<>();
        queryMustBe.add(student1);
        assertEquals(queryMustBe, queryResult1);
    }

    @Test
    public void Test3() {
        Student student1 = new Student("S2112", "Jalal Molana", 60, 90);
        Student student2 = new Student("S4412", "Saadi Shirazi", 60, 90);
        dbms.insertStudent(student1);
        dbms.insertStudent(student2);
        dbms.deleteStudent(student1);
        Student queryResult1 = dbms.queryByStudentNumber("S4412");
        assertEquals(student2, queryResult1);
    }
}