package com.example.group4planninggame;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Until;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.managers.CrudSingleton;

@RunWith(AndroidJUnit4.class)
public class ProductPostingUiAutomatorTest {

    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice device;

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Before
    public void setUp() throws UiObjectNotFoundException {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.example.group4planninggame");
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            device.wait(Until.hasObject(By.pkg("com.example.group4planninggame").depth(0)), LAUNCH_TIMEOUT);
        }

        // Navigate through login/sign-in
        UiObject alreadyRegisteredButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/skipRegistration"));
        if (alreadyRegisteredButton.exists()) {
            alreadyRegisteredButton.click();
            UiObject emailField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/LoginEmailAddress"));
            emailField.setText("testuser@example.com");
            UiObject passwordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/loginPassword"));
            passwordField.setText("password123");
            UiObject loginButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signInPushButton"));
            loginButton.clickAndWaitForNewWindow();
        }

        // Navigate to user settings
        UiObject2 toolbar = device.findObject(By.res("com.example.group4planninggame:id/toolbar"));
        UiObject2 settings = toolbar.getChildren().get(1);
        if(settings.isClickable()) {
            settings.click();
        }

        // Navigate to ProductPostingActivity from UserSettings
        UiObject postProductButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/postProductButton"));
        postProductButton.waitForExists(LAUNCH_TIMEOUT);
        postProductButton.click();
    }

    @Test
    public void testFormValidation_ErrorMessagesForMissingFields() throws UiObjectNotFoundException {
        // Verify that error messages are shown if required fields are not filled
        UiObject submitButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/submitButton")); submitButton.click();
        device.wait(Until.findObject(By.textContains("Please provide product name")), 5000);
        UiObject toastMessage = device.findObject(new UiSelector().textContains("Please provide product name"));
        assertNotNull(toastMessage);
    }

    @Test
    public void testSuccessfulProductPosting() throws UiObjectNotFoundException {
        Crud dbCrud = CrudSingleton.getInstance().getCrud();
        dbCrud.removeProduct("testuser@example.com Used Books");
        // Verify that the product is visible to receivers after successful form submission
        UiObject productNameTextBox = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/titleTextBox"));
        productNameTextBox.setText("Used Books");
        UiObject categorySpinner = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/itemCategorySpinner"));
        categorySpinner.click();
        UiObject categoryItem = device.findObject(new UiSelector().text("Books"));
        categoryItem.click();
        UiObject descriptionTextBox = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/multiAutoCompleteTextView"));
        descriptionTextBox.setText("A gently used collection of books in good condition");
        UiObject conditionSpinner = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/itemConditionSpinner"));
        conditionSpinner.click();
        UiObject conditionItem = device.findObject(new UiSelector().text("Good"));
        conditionItem.click();
        UiObject submitButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/submitButton"));
        submitButton.click();

        assertNotNull(dbCrud.getProduct("testuser@example.com Used Books"));

        dbCrud.removeProduct("testuser@example.com Used Books");
    }
}
