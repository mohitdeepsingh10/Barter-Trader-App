package com.example.group4planninggame;

import android.location.Location;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

import com.example.group4planninggame.models.Product;
import com.example.group4planninggame.activities.MainActivity;
import com.example.group4planninggame.utils.Constants;

@RunWith(AndroidJUnit4.class)
public class LocationEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        Location location1 = new Location("");
        location1.setLatitude(44.637523);
        location1.setLongitude(-63.594868);

        Constants.USER_LOCATION = location1;
    }

    @After
    public void teardown() {
        Constants.USER_LOCATION = null;
    }

    @Test
    public void testGetDistance() {
        Location location2 = new Location("");
        location2.setLatitude(44.637443);
        location2.setLongitude(-63.587307);

        Product product = new Product();

        double distance = product.getDistance(location2);
        Log.d("LOCATIONTEST", distance + "");
        assertTrue(distance < 600 && distance > 590);
    }
}
