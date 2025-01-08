package com.example.group4planninggame;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.group4planninggame.utils.Constants;

import org.junit.Before;
import org.junit.Rule;

import java.util.Date;
import java.util.Locale;

public class BaseTest {

    protected UiDevice device;
    private static final int LAUNCH_TIMEOUT = 5000;

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION");

    @Before
    public void setup() throws UiObjectNotFoundException {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.example.group4planninggame");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg("com.example.group4planninggame").depth(0)), LAUNCH_TIMEOUT);
    }

    protected void loginUser() throws UiObjectNotFoundException {
        UiObject emailField = findObjectById("LoginEmailAddress");
        emailField.setText("test01@dal.ca");
        findObjectById("loginPassword").setText("123");
        findObjectById("signInPushButton").clickAndWaitForNewWindow();
    }

    protected void loginUser2() throws UiObjectNotFoundException {
        UiObject emailField = findObjectById("LoginEmailAddress");
        emailField.setText("test@dal.ca");
        findObjectById("loginPassword").setText("123");
        findObjectById("signInPushButton").clickAndWaitForNewWindow();
    }

    protected void changeUserRole() throws UiObjectNotFoundException {
        UiObject settingsButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/settingsButton"));
        settingsButton.click();
        UiObject switchRoleButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/switchRoleButton"));
        switchRoleButton.click();
    }

    protected void proceedToOfferExchange() throws UiObjectNotFoundException {
        findObjectById("offerExchangeButton").click();
        findObjectByText("Yes").click();
    }
    protected void searchAndSelectProduct(String productName) throws UiObjectNotFoundException {
        findObjectById("viewProductButton").click();
        UiObject searchField = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/searchBar"));
        searchField.setText(productName);
        UiObject searchButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/searchButton"));
        searchButton.click();
        UiObject product = device.findObject(new UiSelector().text(productName));
        product.click();
    }

    protected UiObject findRoleSpecificDashboardContent() throws UiObjectNotFoundException {
        String currentRole = Constants.USER_ROLE;
        return currentRole.equals(Constants.ROLE_PROVIDER)
                ? device.findObject(new UiSelector().textContains("Provider"))
                : device.findObject(new UiSelector().textContains("Receiver"));
    }

    protected UiObject findObjectById(String id) {
        return device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/" + id));
    }

    protected UiObject findObjectByText(String text) {
        return device.findObject(new UiSelector().text(text));
    }

    protected UiObject findObjectByTextContains(String text) {
        return device.findObject(new UiSelector().textContains(text));
    }

    protected void searchForProduct(String name, String category) throws UiObjectNotFoundException {
        findObjectById("viewProductButton").click();
        findObjectById("searchBar").setText(name);
        findObjectById("categoryFilter").click();
        findObjectByText(category).click();
        findObjectById("searchButton").click();
        UiObject product = device.findObject(new UiSelector().text(name));
        product.click();
    }

    protected void enterExchangeOfferDetails(String itemName, String email) throws UiObjectNotFoundException {
        findObjectById("itemNameInput").setText(itemName);
        findObjectById("userEmailInput").setText(email);
        findObjectById("submit").click();
    }

   protected void logoutUser() throws UiObjectNotFoundException {
       UiObject settingsButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/settingsButton"));
       settingsButton.click();
        UiObject logoutButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/Logout"));
        logoutButton.click();
    }

    protected void checkTimestamp(String messageText) throws UiObjectNotFoundException, InterruptedException {
        UiObject viewIncomingOffer = findObjectById("viewIncomingExchangeButton");
        viewIncomingOffer.click();
        findObjectByText("xyz").click();
        UiObject chatButton = findObjectById("chatButton");
        chatButton.click();
        UiObject messageInput = findObjectById("messageInput");
        messageInput.click();
        messageInput.setText(messageText);
        UiObject sendButton = findObjectById("sendButton");
        sendButton.click();
        Thread.sleep(2000);
        UiObject chatMessage = device.findObject(new UiSelector().textContains(messageText));
        assertTrue("Sent message should exist", chatMessage.exists());
        UiObject timestamp = chatMessage.getFromParent(new UiSelector().resourceId("com.example.group4planninggame:id/rightTimestamp"));
        assertTrue("Timestamp should appear under the message", timestamp.exists());
    }
    protected String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }


    protected void openChatRealTimeMessage() throws UiObjectNotFoundException {
        UiObject viewIncomingOffer = findObjectById("viewIncomingExchangeButton");
        viewIncomingOffer.click();
        findObjectByText("xyz").click();
        UiObject chatButton = findObjectById("chatButton");
        chatButton.click();
        UiObject mesageButton = findObjectById("messageInput");
        mesageButton.click();
        mesageButton.setText("Test");
        UiObject sendButton = findObjectById("sendButton");
        sendButton.click();
        assertTrue(findObjectByText("Test").exists());
    }

    public void checkIfProvider() throws UiObjectNotFoundException{
        if(findObjectByText("Receiver").exists()) {
            UiObject settingsButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/settingsButton"));
            settingsButton.click();
            UiObject switchRoleButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/switchRoleButton"));
            switchRoleButton.click();
            UiObject closeSettingsButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/closeSettingsButton"));
            closeSettingsButton.click();
        }
    }

    public void checkIfReceiver() throws UiObjectNotFoundException{
        if(findObjectByText("Provider").exists()) {
            UiObject settingsButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/settingsButton"));
            settingsButton.click();
            UiObject switchRoleButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/switchRoleButton"));
            switchRoleButton.click();
            UiObject closeSettingsButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/closeSettingsButton"));
            closeSettingsButton.click();
        }
    }

    protected void exitChat() throws UiObjectNotFoundException {
        UiObject exitButton = findObjectById("leaveChatButton");
        exitButton.click();
    }

    protected void openChat2() throws UiObjectNotFoundException {
        UiObject viewIncomingOffer = findObjectById("viewIncomingExchangeButton");
        viewIncomingOffer.click();
        findObjectByText("xyz").click();
        UiObject chatButton = findObjectById("chatButton");
        chatButton.click();
        UiObject mesageButton = findObjectById("messageInput");
        mesageButton.click();
        mesageButton.setText("Test44");
        UiObject sendButton = findObjectById("sendButton");
        sendButton.click();
        UiObject exitButton = findObjectById("leaveChatButton");
        exitButton.click();
        chatButton.click();
        mesageButton.click();
    }

    protected void openChat() throws UiObjectNotFoundException {
        UiObject viewIncomingOffer = findObjectById("viewIncomingExchangeButton");
        viewIncomingOffer.click();
        findObjectByText("xyz").click();
        UiObject chatButton = findObjectById("chatButton");
        chatButton.click();
        UiObject mesageButton = findObjectById("messageInput");
        mesageButton.click();
        mesageButton.setText("Test");
        UiObject sendButton = findObjectById("sendButton");
        sendButton.click();
        assertTrue(findObjectByText("Test").exists());
    }


    protected void openChatAccepted() throws UiObjectNotFoundException {
        UiObject viewIncomingOffer = findObjectById("viewIncomingExchangeButton");
        viewIncomingOffer.click();
        findObjectByText("xyz").click();
        assertTrue(findObjectById("chatButton").exists());
    }

    protected void openChatNotAccepted() throws UiObjectNotFoundException{
        UiObject viewIncomingOffer = findObjectById("viewIncomingExchangeButton");
        viewIncomingOffer.click();
        findObjectByText("test").click();
        //UiObject chatButton = findObjectById("chatButton");
        assertFalse(findObjectById("chatButton").exists());
    }

    protected void openChat3() throws UiObjectNotFoundException {
        UiObject viewIncomingOffer = findObjectById("viewIncomingExchangeButton");
        viewIncomingOffer.click();
        findObjectByText("123testing").click();
        UiObject chatButton = findObjectById("chatButton");
        chatButton.click();
        UiObject mesageButton = findObjectById("messageInput");
        mesageButton.click();
        mesageButton.setText("Nintendo");
        UiObject sendButton = findObjectById("sendButton");
        sendButton.click();
        UiObject exitButton = findObjectById("leaveChatButton");
        exitButton.click();
        UiObject returnToOffersBtn = findObjectById("returnToOffersBtn");
        returnToOffersBtn.click();
        UiObject leaveIncomingOffers = findObjectById("leaveIncomingOffers");
        leaveIncomingOffers.click();
        UiObject settingsButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/settingsButton"));
        settingsButton.click();
        UiObject logoutButton = device.findObject(new UiSelector().resourceId("com.example.group4planninggame:id/Logout"));
        logoutButton.click();
        loginUser2();
        checkIfReceiver();
        UiObject viewIncomingOffer2 = findObjectById("submittedOffers");
        viewIncomingOffer2.click();
        findObjectByText("123testing").click();
        UiObject chatButton2 = findObjectById("chatButton");
        chatButton2.click();
    }

}
