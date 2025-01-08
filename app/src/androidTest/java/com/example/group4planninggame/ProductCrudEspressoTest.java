package com.example.group4planninggame;

import android.content.Intent;
import android.location.Location;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

import com.example.group4planninggame.activities.ProductPostingActivity;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.managers.CrudSingleton;
import com.example.group4planninggame.models.Product;
import com.example.group4planninggame.utils.Constants;

@RunWith(AndroidJUnit4.class)
public class ProductCrudEspressoTest {
    Crud dbCrud;

    @Rule
    public ActivityScenarioRule<ProductPostingActivity> activityRule = new ActivityScenarioRule<>(ProductPostingActivity.class);

    @Before
    public void setup() throws InterruptedException {
        dbCrud = CrudSingleton.getInstance().getCrud();

        Location location1 = new Location("");
        location1.setLatitude(44.637523);
        location1.setLongitude(-63.594868);

        Constants.USER_LOCATION = location1;

        String email = "test@dal.ca";
        String title = "Test Item";
        String condition = "Like New";
        String category = "Books";
        String description = "Stuff about this item.";
        double lat = 44.637523;
        double lon = -63.594868;
        String datePosted = "test-test-test";
        dbCrud.addProductToDatabase(email, title, condition, category, description,lat, lon, datePosted);

        Thread.sleep(1000);
    }

//    Database tests.

    @Test
    public void testAddProduct() throws InterruptedException {
        String email = "test@dal.ca";
        String title = "Test Item2";
        String condition = "Like New";
        String category = "Books";
        String description = "Stuff about this item.";
        double lat = 44.637523;
        double lon = -63.594868;
        String datePosted = "test-test-test";
        dbCrud.addProductToDatabase(email, title, condition, category, description,lat, lon, datePosted);

        Thread.sleep(1000);

        assertNotNull(dbCrud.getProduct(email + " " + title));
    }

    @Test
    public void testGetProduct() {
        Product prod = dbCrud.getProduct("test@dal.ca Test Item");
        assertNotNull(prod);
    }

    @After
    public void teardown() {
        Constants.USER_LOCATION = null;
        dbCrud.removeProduct("test@dal.ca Test Item");
    }
}
