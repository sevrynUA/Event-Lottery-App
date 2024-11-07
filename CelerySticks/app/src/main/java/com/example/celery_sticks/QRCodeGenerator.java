package com.example.celery_sticks;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

public class QRCodeGenerator {
    Bitmap qrCode;
    String eventID;
    ByteArrayOutputStream qrBytes;
    byte[] data;
    String encodedQR;
    public QRCodeGenerator(String id) {
        eventID = id;
    }
    public String generate() {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            qrCode = encoder.encodeBitmap(eventID, BarcodeFormat.QR_CODE, 400, 400);
        } catch(Exception e) {
            Log.e("QRCodeGenerator", "QR Code generation failed", e);
        }
        // bitmap to bytes adapted from https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
        qrBytes = new ByteArrayOutputStream();
        qrCode.compress(Bitmap.CompressFormat.PNG, 100, qrBytes);
        data = qrBytes.toByteArray();
        encodedQR = Base64.encodeToString(data, Base64.DEFAULT);
        return encodedQR;
    }

}
