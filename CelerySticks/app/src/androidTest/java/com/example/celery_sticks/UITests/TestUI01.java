package com.example.celery_sticks.UITests;


import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.action.ViewActions.click;

import static org.junit.Assert.assertEquals;


import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


import com.example.celery_sticks.MainActivity;
import com.example.celery_sticks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Instrumented tests, testing the startUp activity and database integrations
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class TestUI01 {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Rule
    public GrantPermissionRule permissionNotification = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Deletes any existing user from database to bring up StartUpActivity page,
     * Fills out information to add a new user to the database,
     * Checks if new user has been added to the database,
     * Deletes the new user from the database.
     */
    @Test
    public void testALoginPage(){
        scenario.getScenario().onActivity(activity -> {
            String userID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            db.collection("users").document(userID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TestUI01", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TestUI01", "Error deleting document", e);
                        }
                    });
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.edit_first_name)).perform(ViewActions.typeText("AndroidTestFirstname"));
        onView(withId(R.id.edit_last_name)).perform(ViewActions.typeText("AndroidTestLastName"));
        onView(withId(R.id.edit_email)).perform(ViewActions.typeText("AndroidTestEmail@Email.com"));
        onView(withId(R.id.signup_button)).perform(click());

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
                            assertEquals(doc.get("firstName"), "AndroidTestFirstname"); // User added to Database
                        }
                    });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            userRef.delete();
        });


    }


    /**
     * Deletes any existing user from database to bring up StartUpActivity page,
     * Fills out information for a new user, but with an improperly formatted email and tries to signup,
     * Checks that user has not been signed up,
     * Switches to properly formatted email, and improperly formatted phone number and tries to sign up,
     * Checks that user has not been signed up,
     * Switches to properly formatted phone number and signs up,
     * Checks that user has been added to the database.
     */
    @Test
    public void testFormatting(){
        scenario.getScenario().onActivity(activity -> {
            String userID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            db.collection("users").document(userID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TestUI01", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TestUI01", "Error deleting document", e);
                        }
                    });
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.edit_first_name)).perform(ViewActions.typeText("ImproperFormatting"));
        onView(withId(R.id.edit_last_name)).perform(ViewActions.typeText("AndroidTestLastName"));
        onView(withId(R.id.edit_email)).perform(ViewActions.typeText("AndroidBadEmail"));
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_button)).check(matches(isDisplayed())); // has not added user

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.edit_email)).perform(ViewActions.clearText());
        onView(withId(R.id.edit_email)).perform(ViewActions.typeText("Android@Android.com"));
        onView(withId(R.id.edit_phone_number)).perform(ViewActions.typeText("0"));
        onView(withId(R.id.signup_button)).check(matches(isDisplayed())); // has not added user


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.edit_phone_number)).perform(ViewActions.clearText());
        onView(withId(R.id.edit_phone_number)).perform(ViewActions.typeText("5555555555"));
        onView(withId(R.id.signup_button)).perform(click());


        scenario.getScenario().onActivity(activity -> {
            String userID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            DocumentReference userRef = db.collection("users").document(userID);
            userRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String phoneNumber = task.getResult().getString("phoneNumber");
                            assertEquals(phoneNumber, "5555555555"); // Phone number should now have updated because the formatting is correct
                        }
                    });
        });
    }
}
