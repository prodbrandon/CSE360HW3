package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import userNameRecognizerTestbed.UserNameRecognizer;

/**
 * Test class for the username validation system.
 * Tests requirements like: starting character types, valid characters, and length constraints.
 */
@DisplayName("Username Validator Tests")
public class UsernameValidatorTest {
    
    @Test
    @DisplayName("Valid usernames should pass validation")
    public void testValidUsernames() {
        // Valid usernames with various characteristics
        String[] validUsernames = {
            "JohnDoe123",          // Alphanumeric
            "jane.doe",            // With period
            "user_name",           // With underscore
            "admin-user",          // With hyphen
            "john.doe_admin"       // Multiple special chars
        };
        
        for (String username : validUsernames) {
            String result = UserNameRecognizer.checkForValidUserName(username);
            assertTrue(result.isEmpty(), 
                      "Username '" + username + "' should be valid but got error: " + result);
        }
    }
    
    @Test
    @DisplayName("Username with valid start character should pass initial validation")
    public void testValidStartCharacter() {
        // Valid start characters: a-z, A-Z
        String username = "ValidStart";
        String result = UserNameRecognizer.checkForValidUserName(username);
        
        assertTrue(result.isEmpty(), "Username starting with letter should be valid");
        assertEquals(-1, UserNameRecognizer.userNameRecognizerIndexofError, 
                    "Valid username should not set error index");
    }
    
    @Test
    @DisplayName("Username with invalid start character should fail validation")
    public void testInvalidStartCharacter() {
        // Invalid start characters: digits, special chars
        String username = "_invalidStart";
        String result = UserNameRecognizer.checkForValidUserName(username);
        
        assertFalse(result.isEmpty(), "Username starting with special char should be invalid");
        assertTrue(result.contains("must start with"), 
                  "Error should mention start character requirement");
        assertEquals(0, UserNameRecognizer.userNameRecognizerIndexofError, 
                    "Error index should point to the first character");
    }
    
    @ParameterizedTest
    @DisplayName("Usernames with invalid length should fail validation")
    @CsvSource({
        "abc, 4",                  // Too short (less than 4 chars)
        "abcdefghijklmnopqrst, 16" // Too long (more than 16 chars)
    })
    public void testInvalidLength(String username, int requiredLength) {
        String result = UserNameRecognizer.checkForValidUserName(username);
        
        assertFalse(result.isEmpty(), "Username with invalid length should be rejected");
        assertTrue(result.contains(String.valueOf(requiredLength)), 
                  "Error should mention the required length");
    }
    
    @Test
    @DisplayName("Empty username should be rejected")
    public void testEmptyUsername() {
        String result = UserNameRecognizer.checkForValidUserName("");
        
        assertFalse(result.isEmpty(), "Empty username should be rejected");
        assertTrue(result.contains("empty"), "Error should mention that input is empty");
        assertEquals(0, UserNameRecognizer.userNameRecognizerIndexofError, 
                    "Error index should be at the beginning for empty input");
    }
    
    @Test
    @DisplayName("Username with invalid characters should be rejected")
    public void testInvalidCharacters() {
        String username = "user@name";  // @ is not allowed
        String result = UserNameRecognizer.checkForValidUserName(username);
        
        assertFalse(result.isEmpty(), "Username with invalid characters should be rejected");
        assertEquals(4, UserNameRecognizer.userNameRecognizerIndexofError, 
                    "Error index should point to the invalid character");
    }
    
    @Test
    @DisplayName("Username with period but invalid character after period should be rejected")
    public void testInvalidAfterPeriod() {
        String username = "user..name";  // Two consecutive periods
        String result = UserNameRecognizer.checkForValidUserName(username);
        
        assertFalse(result.isEmpty(), 
                   "Username with invalid character after period should be rejected");
        assertTrue(result.contains("after a period"), 
                  "Error should mention the requirement after a period");
    }
}