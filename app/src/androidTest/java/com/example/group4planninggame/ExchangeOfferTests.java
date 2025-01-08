package com.example.group4planninggame;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ExchangeOfferTests extends BaseTest {

    @Test
    public void testOfferExchangeIncorrectEmailFormat() throws UiObjectNotFoundException, InterruptedException {
        loginUser();
        searchForProduct("Test Item2", "Books");
        proceedToOfferExchange();
        enterExchangeOfferDetails("testname", "invalidemail");
        Thread.sleep(3000);
        assertTrue(findObjectById("submit").exists());
    }

    @Test
    public void testOfferExchangeMissingName() throws UiObjectNotFoundException, InterruptedException {
        loginUser();
        searchForProduct("Test Item2", "Books");
        proceedToOfferExchange();
        enterExchangeOfferDetails("", "test@dal.ca");
        Thread.sleep(3000);
        assertTrue(findObjectById("submit").exists());
    }

}
