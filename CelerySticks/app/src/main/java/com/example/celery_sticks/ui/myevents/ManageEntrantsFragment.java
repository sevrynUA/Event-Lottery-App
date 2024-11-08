package com.example.celery_sticks.ui.myevents;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.example.celery_sticks.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ManageEntrantsFragment extends AppCompatActivity implements LotteryFragment.LotteryDialogueListener {
    private Button backButton;
    private Button mapButton;
    private Button selectedButton;
    private Button notifyButton;
    private Button lotteryButton;
    private TextView lotteryStatusText;

    private Integer registrantCount = 0;
    private Integer selectedCount = 0;

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

        initialize();

        lotteryButton.setOnClickListener(view -> {
            new LotteryFragment().show(getSupportFragmentManager(), "Lottery");
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

    public void startLottery(Editable input) {
        DocumentReference event = db.collection("events").document(eventID);
        Integer quantity = Integer.parseInt(input.toString());
        Integer numberOfRegistrants = registrantCount;

        if (quantity > numberOfRegistrants) {
            Toast.makeText(this, "Not enough registrants!", Toast.LENGTH_SHORT).show();
        } else if (quantity == 0) {
            Toast.makeText(this, "Lottery requires 1 minimum!", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<String> userIDs = new ArrayList<>();
            ArrayList<String> removingIDs = new ArrayList<>();

            for (User registrant : registrantList) {
                userIDs.add(registrant.getUserID());
            }
            Collections.shuffle(userIDs); // randomize the user ids

            int i;
            for (i = 0; i < quantity; i++) {
                // Remove User object from registrantList
                removingIDs.add(userIDs.get(i));


                // Remove userID from registrants, add to selected
                event.update("registrants", FieldValue.arrayRemove(userIDs.get(i)));
                int finalI = i;
                event.update("selected", FieldValue.arrayUnion(userIDs.get(i)))
                        .addOnSuccessListener(success -> {
                            // Initialize UI on last add
                            if (finalI == quantity - 1) {
                                int j;
                                for (j = 0; j < registrantList.size(); j++) {
                                    if (removingIDs.contains(registrantList.get(j).getUserID())) {
                                        registrantList.remove(registrantList.get(j));
                                    }
                                }
                               initialize();
                            }
                        }
                );
            }
        }
    }


    public void initialize() {
        final Integer[] numberOfRegistrants = new Integer[1];
        final Integer[] numberOfSelected = new Integer[1];
        registrantList.clear();
        selectedCount = 0;
        registrantCount = 0;


        getUsers("selected", new DataCallback() {
            @Override
            public void onDataRecieved(ArrayList<String> data) {
                if (data != null) {
                    numberOfSelected[0] = data.size();
                    if (numberOfSelected[0] == 0) {
                        lotteryStatusText.setText("The selection process has not yet been initiated");
                    } else {
                        lotteryStatusText.setText("The selection process has already started");
                    }
                    for (String userID : data) {
                        getRegistrantData(userID, new DataCallback() {
                            @Override
                            public void onDataRecieved(ArrayList<String> data) {
                                selectedCount++;
                                registrantList.add(new User(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4)));
                                registrantAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
        getUsers("registrants", new DataCallback() {
            @Override
            public void onDataRecieved(ArrayList<String> data) {
                if (data != null) {
                    numberOfRegistrants[0] = data.size();
                    if (numberOfRegistrants[0] == 0) {
                        lotteryStatusText.setText("Maximum number of registrants have been selected");
                        lotteryButton.setVisibility(View.GONE);
                    } else {
                        lotteryButton.setVisibility(View.VISIBLE);
                    }
                    for (String userID : data) {
                        getRegistrantData(userID, new DataCallback() {
                            @Override
                            public void onDataRecieved(ArrayList<String> data) {
                                registrantCount++;
                                registrantList.add(new User(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4)));
                                registrantAdapter.notifyDataSetChanged();

                            }
                        });
                    }
                }
            }
        });
    }


    public void getUsers(String arrayType, DataCallback callback) {
        final ArrayList<String>[] users = new ArrayList[1];
        CollectionReference events = db.collection("events");
        events.document(eventID).get().addOnSuccessListener(event -> {
            if (event.exists()) {
                users[0] = (ArrayList<String>) event.get(arrayType);
                callback.onDataRecieved(users[0]);
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
