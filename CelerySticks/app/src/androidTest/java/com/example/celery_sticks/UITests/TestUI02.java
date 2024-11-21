package com.example.celery_sticks.UITests;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

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


/**
 * Instrumented tests, testing the MainActivity, UI Navigation, and database integrations
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestUI02 {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Rule
    public GrantPermissionRule permissionNotification = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Navigates from MainActivity to facility information fragment,
     * Updates facility information and adds it,
     * Checks that facility information has been updated in the database.
     */
    @Test
    public void testAddFacility(){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.facility_information));
        onView(withId(R.id.edit_facility_name)).perform(ViewActions.clearText());
        onView(withId(R.id.edit_facility_name)).perform(ViewActions.typeText("AndroidTestFacility"));
        onView(withId(R.id.edit_email)).perform(ViewActions.clearText());
        onView(withId(R.id.edit_email)).perform(ViewActions.typeText("Facility@Facility.com"));
        onView(withId(R.id.facility_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        scenario.getScenario().onActivity(activity -> {
            String userID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            DocumentReference userRef = db.collection("facilities").document(userID);
            userRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            assertEquals(doc.get("facilityName"), "AndroidTestFacility"); // Facility added to Database
                        }
                    });
        });
    }

    /**
     * Navigates from MainActivity to profile information fragment,
     * Updates profile information and adds it,
     * Checks that profile information has been updated in the database.
     */
    @Test
    public void testEditProfile(){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.my_profile));
        onView(withId(R.id.edit_first_name)).perform(ViewActions.clearText());
        onView(withId(R.id.edit_first_name)).perform(ViewActions.typeText("NewProfile"));
        onView(withId(R.id.edit_last_name)).perform(ViewActions.clearText());
        onView(withId(R.id.edit_last_name)).perform(ViewActions.typeText("NewLastName"));
        onView(withId(R.id.edit_email)).perform(ViewActions.clearText());
        onView(withId(R.id.edit_email)).perform(ViewActions.typeText("New@Email.com"));
        onView(withId(R.id.save_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        scenario.getScenario().onActivity(activity -> {
            String userID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            DocumentReference userRef = db.collection("users").document(userID);
            userRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            assertEquals(doc.get("firstName"), "NewProfile"); // Facility added to Database
                        }
                    });
        });
    }

    /**
     * Adds a new event from MainActivity
     * Checks that new event is displayed.
     */
    @Test
    public void testAddEvent(){
        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.create_event_button)).perform(click());
        onView(withId(R.id.event_title_input)).perform(ViewActions.typeText("AndroidTestEvent"));
        onView(withId(R.id.event_date_month)).perform(PickerActions.setDate(2026, 10, 10));
        onView(withId(R.id.event_date_time)).perform(PickerActions.setTime(10, 10));
        onView(withId(R.id.event_location_input)).perform(ViewActions.typeText("AndroidTestLocation"));
        onView(withId(R.id.event_open_date_month)).perform(PickerActions.setDate(2026, 10, 9));
        onView(withId(R.id.event_open_date_time)).perform(PickerActions.setTime(10, 9));
        onView(withId(R.id.event_close_date_month)).perform(PickerActions.setDate(2026, 10, 9));
        onView(withId(R.id.event_close_date_time)).perform(PickerActions.setTime(11, 30));
        onView(withId(R.id.create_event_confirm_button)).perform(click());
        onView(withText("AndroidTestEvent")).check(matches(isDisplayed()));

    }

}