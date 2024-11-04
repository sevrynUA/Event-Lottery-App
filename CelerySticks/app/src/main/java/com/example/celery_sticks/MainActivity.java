package com.example.celery_sticks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.provider.Settings;
import android.view.View;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;


import com.example.celery_sticks.ui.eventadd.AddEventFragment;
import com.example.celery_sticks.ui.myevents.EventDetailsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import com.example.celery_sticks.ui.myevents.EventsArrayAdapter;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.databinding.ActivityMainBinding;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;

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
    private String userID;


    private ArrayList<Event> registeredList = new ArrayList<>();
    private ListView registeredListView;
    private EventsArrayAdapter registeredAdapter;

    private ArrayList<Event> acceptedList = new ArrayList<>();
    private ListView acceptedListView;
    private EventsArrayAdapter acceptedAdapter;

    private ArrayList<Event> invitationList = new ArrayList<>();
    private ListView invitationListView;
    private EventsArrayAdapter invitationAdapter;

    private Button CreateEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database
        db = FirebaseFirestore.getInstance();

        // Get device ID
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


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

      
        // Check if user exists in database
        checkUser();

        registeredListView = findViewById(R.id.registered_list);
        registeredAdapter = new EventsArrayAdapter(this, registeredList);
        registeredListView.setAdapter(registeredAdapter);

        acceptedListView = findViewById(R.id.accepted_list);
        acceptedAdapter = new EventsArrayAdapter(this, acceptedList);
        acceptedListView.setAdapter(acceptedAdapter);

        invitationListView = findViewById(R.id.invitation_list);
        invitationAdapter = new EventsArrayAdapter(this, invitationList);
        invitationListView.setAdapter(invitationAdapter);

        CreateEventButton = findViewById(R.id.create_event_button);


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
                        } else if (!registered && !accepted) {
                            invitationList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventDetailsQR, eventSignUpQR, eventLocation));
                            invitationAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        registeredListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                eventClicked(adapterView, view, i, l, "registered");
            }
        });
        acceptedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                eventClicked(adapterView, view, i, l, "accepted");
            }
        });
        invitationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                eventClicked(adapterView, view, i, l, "invitation");
            }
        });

        CreateEventButton.setOnClickListener(view ->{
            Intent createEventIntent = new Intent(getApplicationContext(), AddEventFragment.class);
            startActivity(createEventIntent);
        });


    }
    public void eventClicked(AdapterView<?> adapterView, View view, int i, long l, String eventCategory) {
        Intent intent = new Intent(getApplicationContext(), EventDetailsViewModel.class);

        //intent.putExtra("field", data); // use this to add in info about the event
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

    /**
     * Checks if the user exists in the database and faciliates the user to sign up if not.
     */
    private void checkUser() {
        db.collection("users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document != null) {
                                // User exists
                                Log.d("MainActivity", "User found: " + document.getData());

                            } else {
                                // User does not exist
                                Log.d("MainActivity", "User not found, starting StartUpActivity");
                                Intent intent = new Intent(getApplicationContext(), StartUpActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Log.e("MainActivity", "Error getting user data", task.getException());
                        }
                    }
                });
    }


}