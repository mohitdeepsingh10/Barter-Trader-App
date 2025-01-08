package com.example.group4planninggame;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProductSearchTests extends BaseTest {

    @Test
    public void searchButtonExists() throws UiObjectNotFoundException {
        loginUser();
        searchForProduct("test", "Books");
        assertTrue(findObjectById("searchButton").exists());
    }

    @Test
    public void searchItemExists() throws UiObjectNotFoundException {
        loginUser();
        searchForProduct("testitem", "Antiques");
        assertTrue(findObjectByText("testitem").exists());
    }
}
