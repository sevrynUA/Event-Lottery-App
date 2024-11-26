package com.example.celery_sticks;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Represents the activity for a user to sign up for the app by providing basic personal information.
 */
public class StartUpActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editPhoneNumber;
    private Button signupButton;
    private FirebaseFirestore db;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    /**
     * Initializes the activity and sets up the layout and database connection.
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_user);

        // Initialize database
        db = FirebaseFirestore.getInstance();

        editFirstName = findViewById(R.id.edit_first_name);
        editLastName = findViewById(R.id.edit_last_name);
        editEmail = findViewById(R.id.edit_email);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(v -> saveUserData());
        checkNotificationPermission(); // Check and request notification permission if needed
    }

    /**
     * Validates the inputs meet the required format
     * @param firstName first name user input
     * @param lastName last name user input
     * @param email email user input
     * @param phoneNumber phone number user input
     * @return boolean representing whether or not the inputs are valid
     */
    private boolean inputValidation(String firstName, String lastName, String email, String phoneNumber) {
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(lastName)) {
            return false;
        } else if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*")) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // From https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext by user1737884, Downloaded 2024-11-04
            return false;
        } else if (!phoneNumber.matches("\\d{10}") && !TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        return true;
    }

    /**
     * Saves user data to database
     */
    private void saveUserData() {
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String email = editEmail.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();
        // Input validation for empty required fields
        if (!inputValidation(firstName, lastName, email, phoneNumber)) {
            Toast.makeText(this, "Please fill in all required information", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get device ID
        String userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // every user is an entrant by default
        String role = "entrant";

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("userID", userID);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("email", email);
        userData.put("phoneNumber", phoneNumber);
        userData.put("encodedImage", "");
        userData.put("role", role);
        userData.put("notificationSetting", true);
        userData.put("admin", false); // user is NOT admin by default

        db.collection("users").document(userID).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Intent completedIntent = new Intent();
                    completedIntent.putExtra("firstName", firstName);
                    completedIntent.putExtra("lastName", lastName);
                    completedIntent.putExtra("userID", userID);
                    setResult(RESULT_OK, completedIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Checks if the application has permission to post notifications.
     */
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if permission is already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    /**
     * Method that handles the result of the permission request for posting notifications.
     * @param requestCode  Request code passed when requesting permission.
     * @param permissions  Requested permissions (POST_NOTIFICATIONS).
     * @param grantResults Grant results for the corresponding permissions, either PERMISSION_GRANTED or PERMISSION_DENIED.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
