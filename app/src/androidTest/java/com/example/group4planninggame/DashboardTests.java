package com.example.group4planninggame;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DashboardTests extends BaseTest {

    @Test
    public void testRoleSpecificDashboard() throws UiObjectNotFoundException {
        loginUser();
        assertTrue(findRoleSpecificDashboardContent().exists());
    }

    @Test
    public void testRoleSwitchUpdatesUI() throws UiObjectNotFoundException, InterruptedException {
        loginUser();
        Thread.sleep(5000);
        changeUserRole();
        assertTrue(findRoleSpecificDashboardContent().exists());
    }

    @Test
    public void testUserLogsOutAndRedirectedToLogin() throws UiObjectNotFoundException, InterruptedException {
        loginUser();

        logoutUser();
        assertTrue(findObjectById("signInPushButton").exists());
    }
}
