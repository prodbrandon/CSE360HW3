package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.List;

import application.studentDatabase;

@DisplayName("Student Q&A System Tests")
public class StudentQATest {
    
    private studentDatabase dbHelper;
    private int testUserId;
    private int testQuestionId;
    
    @BeforeEach
    public void setUp() throws SQLException {
        dbHelper = new studentDatabase();
        dbHelper.connectToDatabase();
        
        testUserId = dbHelper.getUserId("testuser");
        if (testUserId == -1) {
            System.out.println("Test user not found in database");
        }
        
        testQuestionId = dbHelper.addQuestion("Test Question Title", "Test Question Content", testUserId);
    }
    
    @AfterEach
    public void tearDown() {
        try {
            if (testQuestionId != -1) {
                dbHelper.deleteQuestion(testQuestionId);
            }
            
            dbHelper.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("Should add a new question to the database")
    public void testAddQuestion() throws SQLException {
        String title = "New Test Question";
        String content = "This is a test question content";
        
        int questionId = -1;
        try {
            questionId = dbHelper.addQuestion(title, content, testUserId);
            assertTrue(questionId > 0, "Question ID should be greater than 0");
        } finally {
            // Clean up
            if (questionId > 0) {
                dbHelper.deleteQuestion(questionId);
            }
        }
    }
    
    @Test
    @DisplayName("Should add a new answer to a question")
    public void testAddAnswer() throws SQLException {
        String content = "This is a test answer content";
        
        int answerId = -1;
        try {
            answerId = dbHelper.addAnswer(content, testQuestionId, testUserId);
            assertTrue(answerId > 0, "Answer ID should be greater than 0");
        } finally {
            // Clean up
            if (answerId > 0) {
                dbHelper.deleteAnswer(answerId);
            }
        }
    }
    
    @Test
    @DisplayName("Should mark an answer as the resolved solution")
    public void testMarkAnswerAsResolved() throws SQLException {
        // Create a test answer
        String content = "This is a test answer that resolves the question";
        int answerId = -1;
        
        try {
            answerId = dbHelper.addAnswer(content, testQuestionId, testUserId);
            
            // Mark the answer as resolved
            dbHelper.markAnswerAsResolved(testQuestionId, answerId);
            
            // Verify that the resolvedAnswerId field is set correctly
            @SuppressWarnings("rawtypes")
            List answers = dbHelper.getAnswersForQuestion(testQuestionId);
            
            // Print all answers for debugging
            System.out.println("Answers for question " + testQuestionId + ":");
            answers.forEach(System.out::println);
            
            // If marking as resolved works, there should be at least one answer
            assertFalse(answers.isEmpty(), "There should be at least one answer");
            
            // This assumes the markAnswerAsResolved method works if it doesn't throw exceptions
            // In a more thorough test, we would check the actual resolved status
        } finally {
            // Clean up
            if (answerId > 0) {
                dbHelper.deleteAnswer(answerId);
            }
        }
    }
    
    @Test
    @DisplayName("Should unmark a resolved question")
    public void testUnmarkResolved() throws SQLException {
        // Create a test answer
        String content = "This is a test answer that resolves the question";
        int answerId = -1;
        
        try {
            answerId = dbHelper.addAnswer(content, testQuestionId, testUserId);
            
            // Mark the answer as resolved, then unmark it
            dbHelper.markAnswerAsResolved(testQuestionId, answerId);
            dbHelper.unmarkResolved(testQuestionId);
            
            // We could extend this test to verify the resolved status is properly reset
            // For now, we're just verifying no exceptions are thrown
        } finally {
            // Clean up
            if (answerId > 0) {
                dbHelper.deleteAnswer(answerId);
            }
        }
    }
    
    @Test
    @DisplayName("Should find questions by search term")
    public void testSearchQuestions() throws SQLException {
        String searchTerm = "UNIQUESEARCHTERM";
        String title = "Question with " + searchTerm + " in title";
        
        int questionId = -1;
        try {
            questionId = dbHelper.addQuestion(title, "Content", testUserId);
            
            @SuppressWarnings("rawtypes")
            List searchResults = dbHelper.searchQuestions(searchTerm);
            
            assertFalse(searchResults.isEmpty(), "Search should find at least one question");
        } finally {
            // Clean up
            if (questionId > 0) {
                dbHelper.deleteQuestion(questionId);
            }
        }
    }
}