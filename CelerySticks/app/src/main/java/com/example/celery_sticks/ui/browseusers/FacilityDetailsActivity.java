package com.example.celery_sticks.ui.browseusers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class FacilityDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String facilityID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_details);

        facilityID = getIntent().getStringExtra("facilityID");
        Button backButton = findViewById(R.id.facility_details_back_button);
        Button deleteButton = findViewById(R.id.delete_facility_button);

        TextView facilityName = findViewById(R.id.facility_name);
        TextView facilityEmail = findViewById(R.id.facility_email);
        TextView facilityPhoneNumber = findViewById(R.id.facility_phone_number);
        TextView initials = findViewById(R.id.facility_details_icon_initials);

        db.collection("facilities").document(facilityID).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {

                        String initialsStr = "";
                        String facilityFullName = document.getString("facilityName");
                        String[] words = facilityFullName.split("\\s+");
                        for (String word : words) {
                            initialsStr += word.charAt(0);
                        }
                        initials.setText(initialsStr.toUpperCase());
                        facilityName.setText(document.getString("facilityName"));
                        facilityEmail.setText(document.getString("email"));
                        String phoneNumber = document.getString("phoneNumber");
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            facilityPhoneNumber.setText(phoneNumber);
                        } else {
                            facilityPhoneNumber.setText("Not Applicable");
                        }
                    } else {
                        facilityName.setText("Facility not found");
                        facilityEmail.setText("Facility not found");
                        facilityPhoneNumber.setText("Facility not found");
                    }
                });

        backButton.setOnClickListener(view -> {
            Intent completedIntent = new Intent();
            setResult(RESULT_OK, completedIntent);
            finish();
        });

        deleteButton.setOnClickListener(view -> {
            db.collection("facilities").document(facilityID).delete()
                    .addOnSuccessListener(aVoid -> {
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
        });
    }
}

