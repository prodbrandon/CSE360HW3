package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import application.User;
import databasePart1.DatabaseHelper;

/**
 * Test class for user management features.
 * Tests user registration, role management, and invitation code functionality.
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("User Management Tests")
public class UserManagementTest {
    
    private DatabaseHelper dbHelper;
    private String testUsername;
    
    @BeforeEach
    public void setUp() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        
        // Generate a unique username for each test to avoid conflicts
        testUsername = "test_user_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @AfterEach
    public void tearDown() {
        try {
            // Clean up test user if it exists
            if (dbHelper.doesUserExist(testUsername)) {
                dbHelper.deleteUser(testUsername);
            }
            dbHelper.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Should register a new user successfully")
    public void testUserRegistration() throws SQLException {
        // Create a test user
        User testUser = new User(testUsername, "Test123!", "student");
        
        // Register the user
        dbHelper.register(testUser);
        
        // Verify the user exists
        assertTrue(dbHelper.doesUserExist(testUsername), "User should exist after registration");
        
        // Verify the role is correct
        String role = dbHelper.getUserRole(testUsername);
        assertEquals("student", role, "User role should match the one provided during registration");
    }
    
    @Test
    @Order(2)
    @DisplayName("Should generate and validate invitation codes")
    public void testInvitationCodeGeneration() throws SQLException {
        // Generate an invitation code
        String inviteCode = dbHelper.generateInvitationCode();
        
        // Verify the code is not null or empty
        assertNotNull(inviteCode, "Generated invitation code should not be null");
        assertFalse(inviteCode.isEmpty(), "Generated invitation code should not be empty");
        
        // Validate the invitation code
        boolean isValid = dbHelper.validateInvitationCode(inviteCode);
        assertTrue(isValid, "Generated invitation code should be valid");
        
        // Validate the code a second time (should fail as codes are one-time use)
        boolean isStillValid = dbHelper.validateInvitationCode(inviteCode);
        assertFalse(isStillValid, "Invitation code should not be valid after first use");
    }
    
    @Test
    @Order(3)
    @DisplayName("Should update user roles correctly")
    public void testUserRoleManagement() throws SQLException {
        // Create a test user with initial role
        User testUser = new User(testUsername, "Test123!", "student");
        dbHelper.register(testUser);
        
        // Create an admin user for role management
        String adminUsername = "admin_" + UUID.randomUUID().toString().substring(0, 8);
        User adminUser = new User(adminUsername, "Admin123!", "admin");
        dbHelper.register(adminUser);
        
        try {
            // Update the test user's role
            List<String> newRoles = new ArrayList<>();
            newRoles.add("student");
            newRoles.add("reviewer");
            
            dbHelper.updateUserRoles(testUsername, newRoles, adminUsername);
            
            // Verify the role was updated
            String updatedRole = dbHelper.getUserRole(testUsername);
            assertTrue(updatedRole.contains("student"), "Updated role should contain 'student'");
            assertTrue(updatedRole.contains("reviewer"), "Updated role should contain 'reviewer'");
        } finally {
            // Clean up admin user
            if (dbHelper.doesUserExist(adminUsername)) {
                dbHelper.deleteUser(adminUsername);
            }
        }
    }
}