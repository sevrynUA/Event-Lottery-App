package com.example.celery_sticks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
//Adapted from android template
public class GeolocationMap extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        Button backButton = findViewById(R.id.map_back_button);
        backButton.setOnClickListener(view -> {
            finish();
        });
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /**
     * Manipulates the map when it's available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventID");
        DocumentReference geoDoc = db.collection("geolocation").document(eventID);
        geoDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map = document.getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        GeoPoint geoPoint = document.getGeoPoint(entry.getKey());
                        LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(location).title(entry.getKey()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        entry.getValue().toString();
                    }

                } else {
                    Log.d("Map", "get failed with ", task.getException());
                }
            }
        });
    }
}