package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Objects;

/**
 * Represents the activity opened by clicking on "QR Code" in an event details page, which displays the QR code for that event.
 */
public class EventQRCodeView extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String eventID;
    String eventCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_view);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");
        eventCategory = intent.getStringExtra("category");

        db.collection("events").document(eventID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Button qrDelete = findViewById(R.id.delete_qr);
                        TextView qrGone = findViewById(R.id.no_qr_text);
                        String encodedQR = (String) documentSnapshot.get("qrcode");

                        qrGone.setVisibility(View.GONE);
                        qrDelete.setVisibility(View.GONE);

                        if (Objects.equals(encodedQR,"")){
                            qrGone.setVisibility(View.VISIBLE);
                        }
                        if (Objects.equals(eventCategory,"admin")){
                            qrDelete.setVisibility(View.VISIBLE);
                        }
                        // bitmap to bytes adapted from https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
                        byte[] decodedQR = Base64.decode(encodedQR, Base64.DEFAULT);
                        Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedQR, 0, decodedQR.length);
                        // set qrImage to decoded bitmap
                        ImageView qrImage = findViewById(R.id.qr_image_view);
                        qrImage.setImageBitmap(qrBitmap);
                        qrImage.setVisibility(View.VISIBLE);

                        Button backButton = findViewById(R.id.qr_view_back);
                        backButton.setOnClickListener(view -> {
                            finish();
                        });

                        qrDelete.setOnClickListener(view -> {
                            if(!Objects.equals(encodedQR,"")) {
                                HashMap<String, Object> eventData = new HashMap<>();
                                eventData.put("qrcode", "");
                                db.collection("events").document(eventID).update(eventData)
                                        .addOnSuccessListener(success -> {
                                            qrGone.setVisibility(View.VISIBLE);
                                            qrImage.setVisibility(View.GONE);
                                        });
                            }
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
