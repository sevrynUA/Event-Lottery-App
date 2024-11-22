package com.example.celery_sticks.ui.browseusers;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileDetailsViewModel extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String viewingUserID = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details);

        Intent intent = getIntent();
        viewingUserID = intent.getStringExtra("clickedUserID");

        TextView firstName = findViewById(R.id.profile_details_first_name);
        TextView lastName = findViewById(R.id.profile_details_last_name);
        TextView email = findViewById(R.id.profile_details_email);
        TextView phoneNumber = findViewById(R.id.profile_details_phone_number);
        TextView initials = findViewById(R.id.profile_details_icon_initials);
        ImageView profilePicture = findViewById(R.id.profile_details_user_image_profile_screen);
        Button facilityDetailsButton = findViewById(R.id.facility_details_button);
        Button deleteUserButton = findViewById(R.id.delete_user_button);
        Button backButton = findViewById(R.id.profile_details_back_button);

        db.collection("users").document(viewingUserID).get()
                .addOnSuccessListener(document -> {
                    String initialsStr = "";
                    initialsStr += document.getString("firstName").charAt(0);
                    initialsStr += document.getString("lastName").charAt(0);
                    initials.setText(initialsStr.toUpperCase());

                    firstName.setText(document.getString("firstName"));
                    lastName.setText(document.getString("lastName"));
                    email.setText(document.getString("email"));
                    phoneNumber.setText(document.getString("phoneNumber"));
                    // TODO JEREMY can you plz make the "profilePicture" imageView be the user image here ty

                    db.collection("facilities").document(viewingUserID).get()
                            .addOnSuccessListener(facilityDoc -> {
                                if (facilityDoc.exists()) {
                                    facilityDetailsButton.setText("View Facility Details");
                                    facilityDetailsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.vomitGreen)));
                                    facilityDetailsButton.setClickable(true);
                                } else {
                                    facilityDetailsButton.setText("This User Has No Facility");
                                    facilityDetailsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary)));
                                    facilityDetailsButton.setClickable(false);
                                }
                            });
                });

        backButton.setOnClickListener(view -> {
            Intent completedIntent = new Intent();
            setResult(RESULT_OK, completedIntent);
            finish();
        });

        deleteUserButton.setOnClickListener(view -> {
            db.collection("users").document(viewingUserID).delete()
                    .addOnSuccessListener(success -> {
                        Intent completedIntent = new Intent();
                        setResult(RESULT_OK, completedIntent);
                        finish();
                    });
        });
    }
}
