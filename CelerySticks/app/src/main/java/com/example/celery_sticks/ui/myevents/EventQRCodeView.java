package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.QRCodeGenerator;
import com.example.celery_sticks.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class EventQRCodeView extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String eventID;
    Bitmap qrcode;
    QRCodeGenerator generator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_view);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            qrcode = encoder.encodeBitmap(eventID, BarcodeFormat.QR_CODE, 400, 400);
        } catch(Exception e) {
            Log.e("Error", "Generation failed" );
        }


        ImageView qrImage = findViewById(R.id.qr_image_view);
        qrImage.setImageBitmap(qrcode);

        Button backButton = findViewById(R.id.qr_view_back);
        backButton.setOnClickListener(view -> {
            finish();
        });
    }
}
