package com.example.group4planninggame;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.example.group4planninggame.utils.Constants;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SearchMapUITests {

    private static final String PACKAGE = "com.example.group4planninggame:id/";
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

    private void loginUser() throws UiObjectNotFoundException {
        device.wait(Until.hasObject(By.text("Log In")), LAUNCH_TIMEOUT);
        if(device.findObject(By.text("Log In")) == null) {
            return;
        }

        UiObject emailField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/LoginEmailAddress"));
        emailField.setText("test@dal.ca");

        UiObject passwordField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/loginPassword"));
        passwordField.setText("123");

        UiObject loginButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/signInPushButton"));
        loginButton.clickAndWaitForNewWindow();
    }

    private void checkUserIsReceiver() throws UiObjectNotFoundException {
        String currentRole = Constants.USER_ROLE;
        if (currentRole.equals(Constants.ROLE_PROVIDER)) {
            UiSelector settingsButton = new UiSelector().resourceId(PACKAGE + "settingsButton");
            UiObject stgbut = device.findObject(settingsButton);

            stgbut.clickAndWaitForNewWindow(LAUNCH_TIMEOUT);

            UiObject switchRoleButton = device.findObject(new UiSelector().resourceId(PACKAGE + "switchRoleButton"));
            switchRoleButton.click();
            UiObject closeButton =  device.findObject(new UiSelector().resourceId(PACKAGE + "closeSettingsButton"));
            closeButton.clickAndWaitForNewWindow();
        }
    }

    @Test
    public void mapButtonRedirectsToMap() throws UiObjectNotFoundException{
        loginUser();
        checkUserIsReceiver();

        UiObject2 searchButton = device.findObject(By.res(PACKAGE + "viewProductButton"));
        searchButton.click();
        device.wait(Until.hasObject(By.text("View Products")), LAUNCH_TIMEOUT);

        UiObject2 submitSearchButton = device.findObject(By.text("View Products"));
        submitSearchButton.click();
        device.wait(Until.hasObject(By.text("Search Results")), LAUNCH_TIMEOUT);

        UiObject2 mapViewButton = device.findObject(By.res(PACKAGE + "mapViewButton"));
        mapViewButton.click();
        device.wait(Until.hasObject(By.text("Items In Area")), LAUNCH_TIMEOUT);

        assertNotNull(device.findObject(By.text("Items In Area")));
    }

    @Test
    public void viewOnMapRedirectsToMap() throws UiObjectNotFoundException {
        loginUser();
        checkUserIsReceiver();

        UiObject2 searchButton = device.findObject(By.res(PACKAGE + "viewProductButton"));
        searchButton.click();
        device.wait(Until.hasObject(By.text("View Products")), LAUNCH_TIMEOUT);

        UiObject2 submitSearchButton = device.findObject(By.text("View Products"));
        submitSearchButton.click();
        device.wait(Until.hasObject(By.text("Search Results")), LAUNCH_TIMEOUT);

        UiObject2 recyclerView = device.findObject(By.clazz(RecyclerView.class));
        List<UiObject2> recyclerList = recyclerView.getChildren();
        recyclerList.get(0).click();
        device.wait(Until.hasObject(By.text("Offer Exchange")), LAUNCH_TIMEOUT);

        UiObject2 viewOnMapButton = device.findObject(By.res(PACKAGE + "viewOnMapButton"));
        viewOnMapButton.click();
        device.wait(Until.hasObject(By.text("Items In Area")), LAUNCH_TIMEOUT);

        assertNotNull(device.findObject(By.text("Items In Area")));
    }

    @Test
    public void searchByLocation() throws UiObjectNotFoundException {
        loginUser();
        checkUserIsReceiver();

        UiObject2 searchButton = device.findObject(By.res(PACKAGE + "viewProductButton"));
        searchButton.click();
        device.wait(Until.hasObject(By.text("View Products")), LAUNCH_TIMEOUT);

        UiObject2 locationSearch = device.findObject(By.hint("Enter Address"));
        locationSearch.setText("Toronto, Canada");

        UiObject2 submitSearchButton = device.findObject(By.text("View Products"));
        submitSearchButton.click();
        device.wait(Until.hasObject(By.text("Search Results")), LAUNCH_TIMEOUT);

        UiObject2 recyclerView = device.findObject(By.clazz(RecyclerView.class));
        List<UiObject2> recyclerList = recyclerView.getChildren();
        recyclerList.get(0).click();

        assertTrue(device.wait(Until.hasObject(By.text("Location Test Product")), LAUNCH_TIMEOUT));
    }

}
