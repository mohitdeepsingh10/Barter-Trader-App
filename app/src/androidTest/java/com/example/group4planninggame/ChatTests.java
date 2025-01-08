package com.example.group4planninggame;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

import android.icu.text.SimpleDateFormat;

import com.example.group4planninggame.utils.Constants;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class ChatTests extends BaseTest {

    @Test //PASSES
    public void chatButtonExists() throws UiObjectNotFoundException {
        loginUser();
        checkIfProvider();
        openChatAccepted();
    }

    @Test // PASSES
    public void testChatEnabledAfterOfferNotAccepted() throws UiObjectNotFoundException {
        loginUser();
        checkIfProvider();
        openChatNotAccepted();
    }

    @Test // PASSES
    public void testRealTimeMessageUpdates() throws UiObjectNotFoundException {
        loginUser();
        checkIfProvider();
        openChatRealTimeMessage();
    }

    @Test //PASSES
    public void testLoadChatHistoryWithTimestamps() throws UiObjectNotFoundException, InterruptedException {
        loginUser();
        checkIfProvider();
        checkTimestamp("test1212");
    }

    @Test //PASSES
    public void testExitChatSavesLastMessage() throws UiObjectNotFoundException {
        loginUser();
        checkIfProvider();
        openChat2();
        //-> base test has helper methods
        UiObject lastMessage = findObjectByText("Test44");
        assertTrue(lastMessage.exists());
    }
    @Test
    public void testOtherUserGetsMessage() throws UiObjectNotFoundException {
        loginUser();
        checkIfProvider();
        openChat3();
        UiObject lastMessage = findObjectByText("Nintendo");
        assertTrue(lastMessage.exists());
    }

    @Test //not done
    public void testUnreadMessageOnLogin() throws UiObjectNotFoundException {
        simulateUnreadMessage();
        loginUser2();
        checkIfProvider();
        UiObject viewIncomingOffer = findObjectById("viewIncomingExchangeButton");
        viewIncomingOffer.click();
        findObjectByText("a").click();
        UiObject chatButton = findObjectById("chatButton");
        chatButton.click();
    }


    private void sendMessage(String message) throws UiObjectNotFoundException {
        UiObject chatInput = findObjectById("messageInput");
        chatInput.setText(message);
        findObjectById("sendButton").click();
    }


    private void simulateUnreadMessage() throws UiObjectNotFoundException {
        loginUser();
        checkIfReceiver();
        UiObject viewSubmittedOffer = findObjectById("submittedOffers");
        viewSubmittedOffer.click();
        findObjectByText("a").click();
        UiObject chatButton = findObjectById("chatButton");
        chatButton.click();
        sendMessage("poo");
        exitChat();
        UiObject backbutton1 = findObjectById("returnToOffersBtn");
        backbutton1.click();
        UiObject backbutton2 = findObjectById("backButton");
        backbutton2.click();
        logoutUser();
    }

}
