package com.example.celery_sticks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.example.celery_sticks.ui.myevents.EventsArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.databinding.ActivityMainBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseFirestore db;

    private ArrayList<Event> registeredList = new ArrayList<>();
    private ListView registeredListView;
    private EventsArrayAdapter registeredAdapter;

    private ArrayList<Event> acceptedList = new ArrayList<>();
    private ListView acceptedListView;
    private EventsArrayAdapter acceptedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.my_events, R.id.event_finder, R.id.my_profile, R.id.settings, R.id.facility_information)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        registeredListView = findViewById(R.id.registered_list);
        registeredAdapter = new EventsArrayAdapter(this, registeredList);
        registeredListView.setAdapter(registeredAdapter);

        acceptedListView = findViewById(R.id.accepted_list);
        acceptedAdapter = new EventsArrayAdapter(this, acceptedList);
        acceptedListView.setAdapter(acceptedAdapter);

        CollectionReference events = db.collection("events");
        events.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Timestamp eventDate = document.getTimestamp("date");
                        Timestamp eventClose = document.getTimestamp("close");
                        Timestamp eventOpen = document.getTimestamp("open");
                        String eventName = document.getString("name");
                        String eventID = document.getId();
                        String eventDescription = document.getString("description");
                        String eventImage = document.getString("image");
                        String eventDetailsQR = document.getString("qrcode");
                        String eventSignUpQR = document.getString("signupqrcode");
                        String eventLocation = document.getString("location");
                        Boolean registered = document.getBoolean("registered");
                        Boolean accepted = document.getBoolean("accepted");
                        if (registered && !accepted) {
                            registeredList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventDetailsQR, eventSignUpQR, eventLocation));
                            registeredAdapter.notifyDataSetChanged();
                        } else if (registered && accepted) {
                            acceptedList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventDetailsQR, eventSignUpQR, eventLocation));
                            acceptedAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        // OPEN LOGIN SCREEN AUTOMATICALLY (will check device details in future)
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        // intent.putExtra("name of string data", string variable) // how to pass str info in case needed
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu); // commented out
        return false; // disable 3 dot menu
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}