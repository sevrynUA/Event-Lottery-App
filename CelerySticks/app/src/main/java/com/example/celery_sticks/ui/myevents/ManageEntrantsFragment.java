package com.example.celery_sticks.ui.myevents;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.example.celery_sticks.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ManageEntrantsFragment extends AppCompatActivity {
    private Button backButton;
    private Button mapButton;
    private Button selectedButton;
    private Button notifyButton;
    private Button lotteryButton;
    private TextView lotteryStatusText;

    private String eventID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private ArrayList<User> registrantList = new ArrayList<User>();
    private ListView registrantListView;
    private UserArrayAdapter registrantAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_event_entrants);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        backButton = findViewById(R.id.manage_entrants_back_button);
        mapButton = findViewById(R.id.map_button);
        selectedButton = findViewById(R.id.selected_button);
        notifyButton = findViewById(R.id.notify_button);
        lotteryButton = findViewById(R.id.lottery_button);
        lotteryStatusText = findViewById(R.id.lottery_status);

        registrantListView = findViewById(R.id.waitlist_entrants);
        registrantAdapter = new UserArrayAdapter(this, registrantList);
        registrantListView.setAdapter(registrantAdapter);

        refreshList();

        lotteryButton.setOnClickListener(view -> {
            // lottery stuff?
        });

        selectedButton.setOnClickListener(view -> {
            // selected entrants fragment
        });

        backButton.setOnClickListener(view -> {
            finish();
        });

    }

    public interface DataCallback {
        void onDataRecieved(ArrayList<String> data);
    }


    public void refreshList() {
        registrantList.clear();

        CollectionReference users = db.collection("users");
        getRegistrants(new DataCallback() {
            @Override
            public void onDataRecieved(ArrayList<String> data) {
                for (String userID : data) {
                    getRegistrantData(userID, new DataCallback() {
                        @Override
                        public void onDataRecieved(ArrayList<String> data) {
                            registrantList.add(new User(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4)));
                            registrantAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });
    }

    public void getRegistrants(DataCallback callback) {
        final ArrayList<String>[] registrants = new ArrayList[1];
        CollectionReference events = db.collection("events");
        events.document(eventID).get().addOnSuccessListener(event -> {
            if (event.exists()) {
                registrants[0] = (ArrayList<String>) event.get("registrants");
                callback.onDataRecieved(registrants[0]);
            }
        });
    }

    public void getRegistrantData(String userID, DataCallback callback) {
        final ArrayList<String>[] userData = new ArrayList[]{new ArrayList<>()};
        CollectionReference users = db.collection("users");
        users.document(userID).get().addOnSuccessListener(user -> {
            if (user.exists()) {
                userData[0].add(user.getString("firstName"));
                userData[0].add(user.getString("lastName"));
                userData[0].add(user.getString("email"));
                userData[0].add(user.getString("role"));
                userData[0].add(userID);
                callback.onDataRecieved(userData[0]);
            }
        });
    }
}
