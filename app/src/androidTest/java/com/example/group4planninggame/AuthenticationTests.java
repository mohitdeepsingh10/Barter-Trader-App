package com.example.group4planninggame;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AuthenticationTests extends BaseTest {

    @Test
    public void testUserRegistrationShowsRole() throws UiObjectNotFoundException {
        navigateToRegistration();
        registerUser("testuser@example.com", "password123", "password123", "Receiver");
        assertTrue(findObjectByTextContains("Receiver").exists());
    }

    private void navigateToRegistration() throws UiObjectNotFoundException {
        UiObject registerButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/register"));
        registerButton.clickAndWaitForNewWindow();
    }

    private void registerUser(String email, String password, String confirmPassword, String role) throws UiObjectNotFoundException {
        UiObject emailField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signUpEmailAddress"));
        emailField.setText(email);
        UiObject passwordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signUpPassword"));
        passwordField.setText(password);
        UiObject confirmPasswordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/repeatedSignUpPassword2"));
        confirmPasswordField.setText(confirmPassword);
        UiObject roleSpinner = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/RoleSpinner"));
        roleSpinner.click();
        UiObject roleOption = device.findObject(new UiSelector().text(role));
        roleOption.click();
        UiObject registerButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/SignUpPushButton"));
        registerButton.clickAndWaitForNewWindow();
    }

    @Test
    public void testInvalidEmailPreventsLogin() throws UiObjectNotFoundException {
        navigateToRegistration();
        attemptInvalidEmailRegistration();
        assertTrue(findObjectById("SignUpPushButton").exists());
    }

    private void attemptInvalidEmailRegistration() throws UiObjectNotFoundException {
        UiObject emailField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signUpEmailAddress"));
        emailField.setText("invalidemail");
        UiObject passwordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signUpPassword"));
        passwordField.setText("password123");
        UiObject confirmPasswordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/repeatedSignUpPassword2"));
        confirmPasswordField.setText("password123");
        UiObject roleSpinner = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/RoleSpinner"));
        roleSpinner.click();
        UiObject roleOption = device.findObject(new UiSelector().text("Receiver"));
        roleOption.click();
        UiObject registerButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/SignUpPushButton"));
        registerButton.click();
    }

    @Test
    public void testPasswordMismatchPreventsLogin() throws UiObjectNotFoundException {
        navigateToRegistration();
        attemptMismatchedPasswordRegistration();
        assertTrue(findObjectById("SignUpPushButton").exists());
    }

    private void attemptMismatchedPasswordRegistration() throws UiObjectNotFoundException {
        UiObject emailField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signUpEmailAddress"));
        emailField.setText("testuser@example.com");
        UiObject passwordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signUpPassword"));
        passwordField.setText("password123");
        UiObject confirmPasswordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/repeatedSignUpPassword2"));
        confirmPasswordField.setText("password1234");
        UiObject roleSpinner = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/RoleSpinner"));
        roleSpinner.click();
        UiObject roleOption = device.findObject(new UiSelector().text("Receiver"));
        roleOption.click();
        UiObject registerButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/SignUpPushButton"));
        registerButton.click();
    }

    @Test
    public void checkIfRegistrationPageIsVisible() throws UiObjectNotFoundException {
        navigateToRegistration();
        assertTrue(findObjectByText("Email").exists());
    }

}
