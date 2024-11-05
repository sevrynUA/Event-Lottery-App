package com.example.celery_sticks;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Represents the activity for a user to sign up for the app by providing basic personal information.
 */
public class StartUpActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editPhoneNumber;
    private Button signupButton;
    private FirebaseFirestore db;

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
    }

    // TODO: Add input validation
    /**
     * Saves user data to database
     */
    private void saveUserData() {
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String email = editEmail.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();
        // Input validation for empty required fields
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill in all required information", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get device ID
        String userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("userID", userID);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("email", email);
        userData.put("phoneNumber", phoneNumber);
        userData.put("role", "entrant");



        db.collection("users").document(userID).set(userData)
                .addOnSuccessListener(aVoid -> {
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                });
    }

}
