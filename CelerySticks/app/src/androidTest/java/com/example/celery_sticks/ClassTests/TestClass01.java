package com.example.celery_sticks.ClassTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.celery_sticks.MainActivity;
import com.example.celery_sticks.QRCodeGenerator;
import com.example.celery_sticks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Instrumented tests for app classes
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestClass01 {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Rule
    public GrantPermissionRule permissionNotification = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Tests QRCodeGenerator,
     * makes new encoded qrCode with "testString",
     * decodes encoded qrCode,
     * ensures it is not null (not empty)
     */
    @Test
    public void testQRCodeGenerator() {
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator("testString");
        String encodedQR = qrCodeGenerator.generate();

        byte[] decodedQR = Base64.decode(encodedQR, Base64.DEFAULT);
        Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedQR, 0, decodedQR.length);

        assertNotNull(qrBitmap);
    }
}
