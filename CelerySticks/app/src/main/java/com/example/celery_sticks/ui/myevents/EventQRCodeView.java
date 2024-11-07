package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.QRCodeGenerator;
import com.example.celery_sticks.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Map;

public class EventQRCodeView extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_view);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        db.collection("events").document(eventID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String encodedQR = (String) documentSnapshot.get("qrcode");
                        // bitmap to bytes adapted from https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
                        byte[] decodedQR = Base64.decode(encodedQR, Base64.DEFAULT);
                        Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedQR, 0, decodedQR.length);
                        // set qrImage to decoded bitmap
                        ImageView qrImage = findViewById(R.id.qr_image_view);
                        qrImage.setImageBitmap(qrBitmap);

                        Button backButton = findViewById(R.id.qr_view_back);
                        backButton.setOnClickListener(view -> {
                            finish();
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                    }
                });
    }
}
