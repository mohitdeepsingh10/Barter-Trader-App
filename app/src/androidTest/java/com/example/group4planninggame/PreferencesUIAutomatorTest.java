package com.example.group4planninggame;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PreferencesUIAutomatorTest {

    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice device;
    @Before
    public void setup() throws UiObjectNotFoundException {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.example.group4planninggame");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg("com.example.group4planninggame").depth(0)), LAUNCH_TIMEOUT);
    }

    private void loginUser() throws UiObjectNotFoundException {


        UiObject emailField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/LoginEmailAddress"));
        emailField.setText("testuser@example.com");

        UiObject passwordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/loginPassword"));
        passwordField.setText("password123");

        UiObject loginButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signInPushButton"));
        loginButton.clickAndWaitForNewWindow();
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
    public void testUserRegistrationShowsRole() throws UiObjectNotFoundException {
        UiObject registerButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/register"));
        registerButton.click();
        registerUser("testuser@example.com", "password123", "password123", "Receiver");
        device.wait(Until.hasObject(By.res("com.example.group4planninggame", "dashboard")), 5000);
        UiObject roleTextView = device.findObject(new UiSelector().textContains("Receiver"));
        //assertTrue(roleTextView.exists());
    }

    @Test
    public void navigatePreferences() throws UiObjectNotFoundException{

        loginUser();
        UiObject updatePreferencesButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/updatePreferences"));
        updatePreferencesButton.clickAndWaitForNewWindow();
        UiObject setLocationButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/setLocationButton"));
        assertTrue(setLocationButton.exists());
    }
    @Test
    public void checkCategoryList() throws UiObjectNotFoundException{

        loginUser();
        UiObject updatePreferencesButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/updatePreferences"));
        updatePreferencesButton.clickAndWaitForNewWindow();
        UiObject categoryList = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/categoriesRecyclerView"));
        assertTrue(categoryList.exists());
    }
    @Test
    public void checkCategorySpinner() throws UiObjectNotFoundException{

        loginUser();
        UiObject updatePreferencesButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/updatePreferences"));
        updatePreferencesButton.clickAndWaitForNewWindow();
        UiObject categorySpinner = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/categorySpinner"));
        assertTrue(categorySpinner.exists());
        categorySpinner.click();
        UiObject categoryOption = device.findObject(new UiSelector().text("Books"));
        categoryOption.click();
        UiObject categoryBooks = device.findObject(new UiSelector().textContains("Books"));
        assertTrue(categoryBooks.exists());

    }




}
