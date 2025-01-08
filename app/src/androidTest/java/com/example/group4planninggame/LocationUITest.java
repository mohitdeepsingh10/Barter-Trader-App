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
public class LocationUITest {

    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice device;

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Before
    public void setup() throws UiObjectNotFoundException {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.example.group4planninggame");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg("com.example.group4planninggame").depth(0)), LAUNCH_TIMEOUT);
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

    private void loginUser() throws UiObjectNotFoundException {
        UiObject alreadyRegisteredButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/skipRegistration"));
        alreadyRegisteredButton.click();

        UiObject emailField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/LoginEmailAddress"));
        emailField.setText("test01@dal.ca");

        UiObject passwordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/loginPassword"));
        passwordField.setText("123");

        UiObject loginButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signInPushButton"));
        loginButton.clickAndWaitForNewWindow();
    }

    @Test
    public void locationShownInDashboard() throws UiObjectNotFoundException{
        loginUser();
        UiObject2 toolbar = device.findObject(By.res("com.example.group4planninggame:id/toolbar"));
        UiObject2 tbstring = toolbar.getChildren().get(0);
        assertTrue(tbstring.wait(Until.textContains(" in "), LAUNCH_TIMEOUT));
    }
}
