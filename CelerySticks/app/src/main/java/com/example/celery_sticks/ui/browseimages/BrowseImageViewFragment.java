package com.example.celery_sticks.ui.browseimages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class BrowseImageViewFragment  extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_image_view);

        image = findViewById(R.id.admin_browse_image_view);


        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String title = intent.getStringExtra("title");
        String id = intent.getStringExtra("id");

        // get image from db
        getImage(id, type);


        Button backButton = findViewById(R.id.admin_browse_image_view_back);
        backButton.setOnClickListener(view -> {
            finish();
        });


        TextView typeText = findViewById(R.id.admin_browse_image_view_type);
        TextView titleText = findViewById(R.id.admin_browse_image_view_title);


        typeText.setText(type);
        titleText.setText(title);


        // delete
        Button deleteButton = findViewById(R.id.admin_browse_image_view_delete_button);
        deleteButton.setOnClickListener(view -> {
            deleteImage(id, type);
        });
    }


    public void deleteImage(String id, String type) {


        if (type.equals("Type: user profile")) {
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("encodedImage", "");


            db.collection("users").document(id).update(userData)
                    .addOnSuccessListener(aVoid -> {
                        finish();
                    });
        } else {

            HashMap<String, Object> eventData = new HashMap<>();
            eventData.put("image", "");

            db.collection("events").document(id).update(eventData)
                    .addOnSuccessListener(aVoid -> {
                        finish();
                    });
        }
    }


    public void getImage(String id, String type) {
        if (type.equals("Type: event poster")) {
            DocumentReference ref = db.collection("events").document(id);


            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        loadImage(document.getString("image"));
                    }
                }
            });
        } else {
            DocumentReference ref = db.collection("users").document(id);


            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        loadImage(document.getString("encodedImage"));
                    }
                }
            });
        }
    }


    private void loadImage(String data) {
        if (data != null) {

            byte[] decodedImage = Base64.decode(data, Base64.DEFAULT);
            Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            // set qrImage to decoded bitmap
            image.setImageBitmap(qrBitmap);
        }
    }
}

