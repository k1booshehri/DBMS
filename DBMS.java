import java.util.*;

interface myStorageBackendInterface<T, U> {
    void insert(T key, U item);
    List<U> search(T key);
    void delete(T key);
    void deleteUtil(T key, Student s);
}

public class DBMS implements DBMSInterface {
    private myStorageBackendInterface<String, Student> studentNumberIndex;
    private myStorageBackendInterface<Integer, Student> overallScoreIndex;

    /**
     * Convert student number string to an integer.
     *
     * @param s is a string which includes student number.
     * @return an integer.
     */
    public int stuNumToInt(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            result += ((int) s.charAt(i)) * (i + 1);
        }
        return result;
    }

    public DBMS() {
        this.studentNumberIndex = new MyStorageBackend<>(
                (s) -> (stuNumToInt((String) s))   // your perfect hash function here
        );
        this.overallScoreIndex = new MyStorageBackend<>(
                (S) -> (1000 * (int) S)   // your perfect hash function here
        );
    }

    /**
     * Insert a student.
     *
     * @param student is a student object.
     */
    public void insertStudent(Student student) {
        studentNumberIndex.insert(student.getStudentNumber(), student);
        overallScoreIndex.insert(student.getOverallScore(), student);
    }

    /**
     * Convert student number string to an integer.
     *
     * @param studentNumber is a student number string.
     * @return a student object.
     */
    public Student queryByStudentNumber(String studentNumber) {
        List<Student> list = studentNumberIndex.search(studentNumber);
        if (list.size() > 0) {
            return studentNumberIndex.search(studentNumber).get(0);
        }
        return null;
    }

    /**
     * Search by score.
     *
     * @param score is the overall score.
     * @return a list of students.
     */
    public List<Student> queryByScore(int score) {
        return overallScoreIndex.search(score);
    }

    /**
     * Delete a student.
     *
     * @param student is a student object.
     */
    @Override
    public void deleteStudent(Student student) {
        this.studentNumberIndex.delete(student.getStudentNumber());
        //this.overallScoreIndex.delete(student.getOverallScore());
        this.overallScoreIndex.deleteUtil(student.getOverallScore(), student);
    }
}

@FunctionalInterface
interface MyHashFunction<T> {
    int hash(T key);
}

class SkipNode {
    Pair<Integer, Student> data;
    SkipNode[] next = new SkipNode[4];

    SkipNode(Pair data) {
        this.data = data;
    }

    /**
     * gets next.
     *
     * @param level is the number of levels.
     * @return a node of skiplist.
     */
    SkipNode getNext(int level) {
        return this.next[level];
    }

    /**
     * sets next.
     *
     * @param next  is a node.
     * @param level is the number of levels.
     */
    void setNext(SkipNode next, int level) {
        this.next[level] = next;
    }

}

class MyStorageBackend<T, U> implements myStorageBackendInterface<T, U> {
    MyHashFunction expr;

    MyStorageBackend(MyHashFunction exprs) {
        this.expr = exprs;
    }

    private final SkipNode head = new SkipNode(null);
    private final Random rand = new Random();

    /**
     * Insert a node.
     *
     * @param data is the hashed value.
     * @param item is a student object.
     */
    @Override
    public void insert(T data, U item) {
        Pair<Integer, Student> pairToInsert = new Pair(expr.hash(data), item);
        SkipNode SkipNode = new SkipNode(pairToInsert);
        for (int i = 0; i < 4; i++) {
            int level = i;
            int interval = (int) Math.pow(2, i);
            if (rand.nextInt(interval) == 0) {
                SkipNode currentNode = head.getNext(level);
                if (currentNode == null) {
                    head.setNext(SkipNode, level);
                    return;
                }
                if (SkipNode.data.first < currentNode.data.first && currentNode != null) {
                    head.setNext(SkipNode, level);
                    SkipNode.setNext(currentNode, level);
                    return;
                }
                while (currentNode.getNext(level) != null && currentNode.data.first < (SkipNode.data.first) &&
                        currentNode.getNext(level).data.first < (SkipNode.data.first)) {
                    currentNode = currentNode.getNext(level);
                }
                SkipNode inherit = currentNode.getNext(level);
                currentNode.setNext(SkipNode, level);
                SkipNode.setNext(inherit, level);
            }
        }
    }

    /**
     * Delete a node.
     *
     * @param target is the target node t delete.
     */
    @Override
    public void delete(T target) {
        SkipNode objToDelete = null;
        int keepLevel = 0;
        for (int i = 3; i >= 0; i--) {
            SkipNode currentNode = head.getNext(i);
            while (currentNode != null && currentNode.data.first <= expr.hash(target)) {
                if (currentNode.data.first == expr.hash(target)) {
                    objToDelete = currentNode;
                    keepLevel++;
                }
                currentNode = currentNode.getNext(i);
            }
        }
        objToDelete.data = null;
        for (int i = 0; i < 4; i++) {
            int level = i;
            SkipNode currentNode = head;
            while (currentNode != null && currentNode.getNext(level) != null) {
                if (currentNode.getNext(level).data == null) {
                    SkipNode inherit = currentNode.getNext(level).getNext(level);
                    keepLevel++;
                    currentNode.setNext(inherit, level);
                }
                currentNode = currentNode.getNext(level);
            }
        }
    }

    /**
     * Delete a node.
     *
     * @param target is the target node t delete.
     * @param s ia a student object.
     */
    @Override
    public void deleteUtil(T target, Student s) {
        SkipNode objToDelete = null;
        int keepLevel = 0;
        for (int i = 3; i >= 0; i--) {
            SkipNode currentNode = head.getNext(i);
            while (currentNode != null && currentNode.data.first <= expr.hash(target)) {
                if (currentNode.data.first == expr.hash(target) && currentNode.data.second == s) {
                    objToDelete = currentNode;
                    keepLevel++;
                }
                currentNode = currentNode.getNext(i);
            }
        }
        objToDelete.data = null;
        for (int i = 0; i < 4; i++) {
            int level = i;
            SkipNode currentNode = head;
            while (currentNode != null && currentNode.getNext(level) != null) {
                if (currentNode.getNext(level).data == null) {
                    SkipNode inherit = currentNode.getNext(level).getNext(level);
                    keepLevel++;
                    currentNode.setNext(inherit, level);
                }
                currentNode = currentNode.getNext(level);
            }
        }
    }

    /**
     * Search by score/student name.
     *
     * @param data is the data to look up.
     * @return a list of students.
     */
    @Override
    public List<U> search(T data) {
        List<U> result = new ArrayList<>();
        List<Pair> resultAid = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            SkipNode currentNode = head.getNext(i);
            while (currentNode != null && currentNode.data.first <= expr.hash(data)) {
                if (currentNode.data.first == expr.hash(data)) {
                    boolean found = false;
                    for (int j = 0; j < resultAid.size(); j++) {
                        Student temp = (Student) resultAid.get(j).second;
                        Student draft = (Student) currentNode.data.second;
                        if (temp.getStudentNumber() == draft.getStudentNumber()) {
                            found = true;
                        }
                    }
                    if (found == false) {
                        resultAid.add(currentNode.data);
                    }
                }
                currentNode = currentNode.getNext(i);
            }
        }
        Collections.sort(resultAid, (d1, d2) -> (int) d2.first - (int) d1.first);
        for (int i = 0; i < resultAid.size(); i++) {
            result.add((U) resultAid.get(i).second);
        }
        return result;
    }
}
// Got skip list implementation idea from https://javarevisited.blogspot.com/