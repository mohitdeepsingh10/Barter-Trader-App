package com.example.group4planninggame;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.runner.RunWith;
import androidx.test.filters.LargeTest;

import com.example.group4planninggame.activities.DashboardActivity;

import org.junit.Rule;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DashboardEspressoTests {
    @Rule
    public ActivityScenarioRule<DashboardActivity> activityRule =
            new ActivityScenarioRule<>(DashboardActivity.class);

    private void loginUser() {
        // login
        onView(withId(R.id.skipRegistration)).perform(click());
        onView(withId(R.id.LoginEmailAddress)).perform(replaceText("test01@dal.ca"));
        onView(withId(R.id.loginPassword)).perform(replaceText("123"));
        onView(withId(R.id.signInPushButton)).perform(click());
    }
}
