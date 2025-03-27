package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.List;

import application.studentDatabase;
import application.StudentHomePage;

/**
 * Test class for StudentHomePage functionality.
 * This is a simplified version that doesn't depend on TestFX.
 */
@DisplayName("Student Home Page UI Tests")
public class StudentHomePageUITest {
    
    private studentDatabase dbHelper;
    private int testUserId;
    
    @BeforeEach
    public void setUp() throws SQLException {
        dbHelper = new studentDatabase();
        dbHelper.connectToDatabase();
        
        testUserId = dbHelper.getUserId("testuser");
        if (testUserId == -1) {
            System.out.println("Test user not found in database");
        }
    }
    
    @AfterEach
    public void tearDown() {
        try {
            dbHelper.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("Database connection should be established")
    public void testDatabaseConnection() {
        // Assert that database helper is not null
        assertNotNull(dbHelper, "Database helper should not be null");
        
        try {
            // Use raw type List with @SuppressWarnings to avoid warnings
            @SuppressWarnings("rawtypes")
            List questions = dbHelper.getQuestions();
            assertNotNull(questions, "Should be able to retrieve questions");
        } catch (SQLException e) {
            fail("Database operation failed: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should create a question successfully")
    public void testQuestionCreation() {
        try {
            String title = "Test Question for UI";
            String content = "This is a test question for the UI test";
            
            int questionId = dbHelper.addQuestion(title, content, testUserId);
            assertTrue(questionId > 0, "Question ID should be greater than 0");
            
            // Clean up the test data
            dbHelper.deleteQuestion(questionId);
        } catch (SQLException e) {
            fail("Question creation failed: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Search functionality should return results")
    public void testSearchFunctionality() {
        int questionId = -1;
        try {
            String searchTerm = "UNIQUEUISEARCHTERM";
            String title = "UI Test Question with " + searchTerm;
            
            // Create test question for searching
            questionId = dbHelper.addQuestion(title, "Content for search test", testUserId);
            
            // Use raw type List with @SuppressWarnings to avoid warnings
            @SuppressWarnings("rawtypes")
            List results = dbHelper.searchQuestions(searchTerm);
            
            assertFalse(results.isEmpty(), "Search should return results");
        } catch (SQLException e) {
            fail("Search test failed: " + e.getMessage());
        } finally {
            // Ensure cleanup even if assertions fail
            if (questionId > 0) {
                try {
                    dbHelper.deleteQuestion(questionId);
                } catch (SQLException e) {
                    System.err.println("Failed to clean up test question: " + e.getMessage());
                }
            }
        }
    }
}