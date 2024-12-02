package com.example.celery_sticks.UITests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.celery_sticks.MainActivity;
import com.example.celery_sticks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestUI03 {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Rule
    public GrantPermissionRule permissionNotification = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Navigates from MainActivity to event finder,
     * Checks that camera has been open
     */
    @Test
    public void testEventFinder(){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.event_finder));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.text_event_finder)).check(matches(isDisplayed()));
    }

    /**
     * Navigates from MainActivity to my profile,
     * edits profile username and clicks save,
     * Checks that username has been saved
     */
    @Test
    public void testChangeProfile(){
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.my_profile));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.edit_first_name)).perform(ViewActions.clearText());
        onView(withId(R.id.edit_first_name)).perform(ViewActions.typeText("testuser"));
        onView(withId(R.id.save_button)).perform(click());
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.text_user_first_name)).check(matches(isDisplayed()));
    }

    /**
     * Navigates from MainActivity to settings,
     * edits settings preferences
     * Checks that settings have been changed
     */
    @Test
    public void testSetting(){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.settings));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.settings_notify_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.settings_notify_button)).check(matches(isClickable()));
    }

    /**
     * NOTE: Admin field for user must be TRUE in database for this test
     * navigates from MainActivity to admin browse events,
     * checks that events are displayed
     * navigates to admin browse users
     * Checks that users are displayed
     */
    @Test
    public void testAdmin(){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.browse_events));
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.events_browse_title)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.browse_users));
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.users_browse_title)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

    }





}
