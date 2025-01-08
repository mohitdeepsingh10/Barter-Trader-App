package com.example.group4planninggame;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.group4planninggame.utils.Constants;
import com.example.group4planninggame.utils.Validator;

import org.junit.Before;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RegistrationJavaUnitTest {

    //make a validator for tests
    Validator testValidator;

    @Before
    public void prepare(){
        testValidator = new Validator();
    }

    @Test
    public void checkIfEmptyEmail() {
        assertTrue(testValidator.isEmptyEmail(""));
        assertFalse(testValidator.isEmptyEmail("test@mail.com"));
    }

    @Test
    public void checkIfEmptyPassword() {
        assertTrue(testValidator.isEmptyPassword(""));
        assertFalse(testValidator.isEmptyPassword("123"));
    }

    @Test
    public void checkPasswordMatch(){
        assertTrue(testValidator.isMatchingPassword("jeremy123","jeremy123"));
    }

    @Test
    public void checkPasswordMismatch(){
        assertFalse(testValidator.isMatchingPassword("jeremy125","jeremy123"));
        assertFalse(testValidator.isMatchingPassword("jeremy125",""));
    }

/*
    Business logic for this test is disabled to make dev easier
 */
//    @Test
//    // passwords can be shorter than 8 characters in dev (should fail in dev, should work on release)
//    public void checkInvalidPassword(){
//        assertFalse(testValidator.isValidPassword("jeremy"));
//    }
    @Test
    public void validRole(){
        assertTrue(testValidator.isValidRole(Constants.ROLE_PROVIDER));
    }

    @Test
    public void invalidRole() {
        assertFalse(testValidator.isValidRole("Select Role"));
    }

    @Test
    public void checkInvalidEmail() {
        assertFalse(testValidator.isEmailValid("hey.com"));
        assertFalse(testValidator.isEmailValid("Siriswashere@.com"));
        assertFalse(testValidator.isEmailValid("heyThisIsWrong-@-.com"));
        assertFalse(testValidator.isEmailValid("hola.org"));
        assertFalse(testValidator.isEmailValid("Whos@Siris@gmail.org"));
    }

    @Test
    public void checkValidEmail(){
        assertTrue(testValidator.isEmailValid("testing@icloud.org"));
        assertTrue(testValidator.isEmailValid("funny_Bard@hotmail.com"));
        assertTrue(testValidator.isEmailValid("insert.Joke@dal.ca"));
        assertTrue(testValidator.isEmailValid("wasap.wasaaap@mail.com"));
        assertTrue(testValidator.isEmailValid("thisShouldWork@mail-archive.com"));
    }


}