package com.example.celery_sticks;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeGenerator {
    Bitmap qrcode;
    public QRCodeGenerator() {
    }
    public Bitmap generate(String id) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            qrcode = encoder.encodeBitmap(id, BarcodeFormat.QR_CODE, 400, 400);
        } catch(Exception e) {
            Log.e("Error", "Generation failed" );
        }
        return qrcode;
    }

}
