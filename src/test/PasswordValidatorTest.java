package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import passwordEvaluationTestbed.PasswordEvaluator;

/**
 * Test class for the password validation system.
 * Tests various password requirements: uppercase, lowercase, digit, special char, and length.
 */
@DisplayName("Password Validator Tests")
public class PasswordValidatorTest {
    
    @Test
    @DisplayName("Valid passwords should pass validation")
    public void testValidPasswords() {
        // Test a valid password with all requirements met
        String validPassword = "College_1";
        String result = PasswordEvaluator.evaluatePassword(validPassword);
        
        assertTrue(result.isEmpty(), "A valid password should return an empty error message");
        assertTrue(PasswordEvaluator.foundUpperCase, "Should detect uppercase letters");
        assertTrue(PasswordEvaluator.foundLowerCase, "Should detect lowercase letters");
        assertTrue(PasswordEvaluator.foundNumericDigit, "Should detect numeric digits");
        assertTrue(PasswordEvaluator.foundSpecialChar, "Should detect special characters");
        assertTrue(PasswordEvaluator.foundLongEnough, "Should detect sufficient length");
    }
    
    @ParameterizedTest
    @DisplayName("Invalid passwords should fail validation with appropriate errors")
    @CsvSource({
        "password, Upper case", // Missing uppercase, digit, special char
        "PASSWORD, Lower case", // Missing lowercase, digit, special char
        "Password, Numeric digits", // Missing digit, special char
        "Password1, Special character", // Missing special char
        "Pw!1, Long Enough" // Too short
    })
    public void testInvalidPasswords(String password, String expectedError) {
        String result = PasswordEvaluator.evaluatePassword(password);
        
        assertFalse(result.isEmpty(), "Invalid password should return an error message");
        assertTrue(result.contains(expectedError), 
                   "Error message should mention the specific requirement that failed");
    }
    
    @ParameterizedTest
    @DisplayName("Empty password should be rejected")
    @ValueSource(strings = {"", "  "})
    public void testEmptyPassword(String password) {
        String result = PasswordEvaluator.evaluatePassword(password);
        
        assertFalse(result.isEmpty(), "Empty password should be rejected");
        assertTrue(result.contains(""), "Error for empty password should be mentioned");
    }
    
    @Test
    @DisplayName("Password with all requirements but with other invalid chars should be rejected")
    public void testInvalidCharacters() {
        String passwordWithOtherChars = "Password1!©"; // Contains copyright symbol
        String result = PasswordEvaluator.evaluatePassword(passwordWithOtherChars);
        
        assertFalse(result.isEmpty(), "Password with invalid chars should be rejected");
        assertTrue(PasswordEvaluator.foundOtherChar, "Should detect other invalid characters");
    }
    
    @Test
    @DisplayName("Password evaluator should reset state between evaluations")
    public void testStateReset() {
        // First evaluate an invalid password missing several requirements
        PasswordEvaluator.evaluatePassword("abc");
        
        // Then evaluate a valid password
        String validPassword = "College_1";
        String result = PasswordEvaluator.evaluatePassword(validPassword);
        
        assertTrue(result.isEmpty(), "Valid password should pass after evaluating an invalid one");
        assertTrue(PasswordEvaluator.foundUpperCase, "Should reset and detect uppercase letters");
        assertTrue(PasswordEvaluator.foundLowerCase, "Should reset and detect lowercase letters");
        assertTrue(PasswordEvaluator.foundNumericDigit, "Should reset and detect numeric digits");
        assertTrue(PasswordEvaluator.foundSpecialChar, "Should reset and detect special characters");
        assertTrue(PasswordEvaluator.foundLongEnough, "Should reset and detect sufficient length");
        assertFalse(PasswordEvaluator.foundOtherChar, "Should reset and not detect invalid chars");
    }
}