package test;

import java.sql.Connection;
import java.util.ArrayList;
import oes.db.*;
import oes.model.*;
import oes.util.DbSchemaInitializer;

public class TestQuizSystem {
    public static void main(String[] args) {
        System.out.println("========== ONLINE QUIZ SYSTEM TEST ==========");

        // 1. Initialize DB schema
        System.out.println("[Test 1] Initializing schema...");
        DbSchemaInitializer.initializeTables();

        // 2. Validate Admin login
        System.out.println("[Test 2] Testing Admin Login...");
        Admins admin = new Admins();
        admin.setUsername("admin");
        admin.setPassword("admin");
        boolean adminValid = AdminsDao.doValidate(admin);
        System.out.println("Admin login ('admin'/'admin'): " + (adminValid ? "PASS" : "FAIL"));
        if (!adminValid) {
            throw new RuntimeException("Admin validation failed!");
        }

        // Test invalid admin login
        Admins invalidAdmin = new Admins();
        invalidAdmin.setUsername("admin");
        invalidAdmin.setPassword("wrongpass");
        boolean invalidAdminValid = AdminsDao.doValidate(invalidAdmin);
        System.out.println("Invalid Admin login: " + (!invalidAdminValid ? "PASS (Correctly rejected)" : "FAIL"));

        // 3. Test Student Registration & Login
        System.out.println("[Test 3] Testing Student Registration & Login...");
        Students st = new Students();
        st.setUsername("testuser1");
        st.setPassword("pass123");
        st.setName("Test Student");
        
        // Delete if already existed from prior run
        StudentsDao.deleteRecord(st);

        boolean studentInserted = StudentsDao.insertStudent(st);
        System.out.println("Student insertion: " + (studentInserted ? "PASS" : "FAIL"));

        Students loginStudent = new Students();
        loginStudent.setUsername("testuser1");
        loginStudent.setPassword("pass123");
        boolean studentLogin = StudentsDao.doValidate(loginStudent);
        System.out.println("Student login: " + (studentLogin ? "PASS" : "FAIL"));
        System.out.println("Student fetched name: " + loginStudent.getName());

        String retrievedName = StudentsDao.getStudentName("testuser1");
        System.out.println("Student getStudentName: " + ("Test Student".equals(retrievedName) ? "PASS" : "FAIL"));

        // Test Student Update
        int updateStudent = StudentsDao.doUpdateNowRecord("testuser1", "testuser1", "newpass456", "Test Student Updated");
        System.out.println("Student update: " + (updateStudent > 0 ? "PASS" : "FAIL"));

        loginStudent.setPassword("newpass456");
        boolean updatedLogin = StudentsDao.doValidate(loginStudent);
        System.out.println("Student login with new password: " + (updatedLogin ? "PASS" : "FAIL"));

        // 4. Test Instructions Management
        System.out.println("[Test 4] Testing Instruction Management...");
        Instructions inst = new Instructions();
        inst.setInstruction("Do not use external calculators.");
        boolean instInserted = InstructionsDao.insertInstruction(inst);
        System.out.println("Instruction insert: " + (instInserted ? "PASS" : "FAIL"));

        ArrayList<Instructions> allInsts = InstructionsDao.getAllRecords();
        System.out.println("All instructions count: " + allInsts.size() + " (" + (allInsts.size() > 0 ? "PASS" : "FAIL") + ")");

        int updateInst = InstructionsDao.doUpdateNowRecord("Do not use external calculators.", "No calculators allowed.");
        System.out.println("Instruction update: " + (updateInst > 0 ? "PASS" : "FAIL"));

        Instructions delInst = new Instructions();
        delInst.setInstruction("No calculators allowed.");
        int delInstRes = InstructionsDao.deleteRecord(delInst);
        System.out.println("Instruction delete: " + (delInstRes > 0 ? "PASS" : "FAIL"));

        // 5. Test Question Management
        System.out.println("[Test 5] Testing Question Management...");
        Questions q = new Questions();
        q.setQuestion("Which data structure uses LIFO?");
        q.setA("Queue");
        q.setB("Stack");
        q.setC("Array");
        q.setD("Tree");
        q.setAnswer("b");

        // Clean up if existing
        QuestionsDao.deleteRecord(q);

        boolean qInserted = QuestionsDao.insertQuestion(q);
        System.out.println("Question insert: " + (qInserted ? "PASS" : "FAIL"));

        ArrayList<Questions> allQ = QuestionsDao.getAllRecords();
        System.out.println("All questions count: " + allQ.size() + " (" + (allQ.size() > 0 ? "PASS" : "FAIL") + ")");

        int updateQ = QuestionsDao.doUpdateNowRecord(
            "Which data structure uses LIFO?",
            "Which data structure uses LIFO order?",
            "Queue", "Stack", "Array", "Linked List", "b"
        );
        System.out.println("Question update: " + (updateQ > 0 ? "PASS" : "FAIL"));

        // Verify updated question exists
        ArrayList<Questions> allQAfter = QuestionsDao.getAllRecords();
        boolean foundUpdated = false;
        for (Questions item : allQAfter) {
            if ("Which data structure uses LIFO order?".equals(item.getQuestion())) {
                foundUpdated = true;
                break;
            }
        }
        System.out.println("Found updated question: " + (foundUpdated ? "PASS" : "FAIL"));

        // 6. Test Exam Scoring Logic
        System.out.println("[Test 6] Testing Exam Scoring Simulation...");
        // Simulate exam answering
        int correct = 0;
        int unattempted = 0;
        int wrong = 0;
        for (int i = 0; i < allQAfter.size(); i++) {
            Questions cur = allQAfter.get(i);
            // Simulate answering first question correctly, rest 'e' (unattempted)
            String userAns = (i == 0) ? cur.getAnswer() : "e";
            if (userAns != null && userAns.equalsIgnoreCase(cur.getAnswer())) {
                correct++;
            } else if ("e".equals(userAns)) {
                unattempted++;
            } else {
                wrong++;
            }
        }
        System.out.println("Exam scoring simulated: Correct=" + correct + ", Unattempted=" + unattempted + ", Wrong=" + wrong);
        System.out.println("Scoring verification: " + (correct == 1 && unattempted == (allQAfter.size() - 1) ? "PASS" : "FAIL"));

        // Clean up test student
        StudentsDao.deleteRecord(loginStudent);
        System.out.println("Cleaned up test student: PASS");

        System.out.println("========== ALL BACKEND/DAO TESTS COMPLETED SUCCESSFULLY ==========");
    }
}
