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
import java.util.Objects;

/**
 * Represents the activity displayed when organizers select "Manage Entrants" on the details screen for their event; manages entrants and can start lottery
 */
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
    private Integer availability;
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
        if (intent.getStringExtra("availability") == null || Objects.equals(intent.getStringExtra("availability"), "")) {
            availability = 999999; // no upper limit on participants
        } else {
            availability = Integer.parseInt(intent.getStringExtra("availability"));
        }

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

    /**
     * Interface used for asynchronously accessing data for event details
     */
    public interface DataCallback {
        /**
         * Function is run when asynchronous access of data has been completed
         * @param data is the data accessed asynchronously
         */
        void onDataRecieved(ArrayList<String> data);
    }

    /**
     * Starts the lottery, checking for a valid number of draws, then setting the database to reflect results
     * @param input is the number of draws requested by the organizer
     */
    public void startLottery(Editable input) {
        DocumentReference event = db.collection("events").document(eventID);
        Integer quantity = Integer.parseInt(input.toString());
        Integer numberOfRegistrants = registrantCount;

        if (quantity + selectedCount > availability) {
            Toast.makeText(this, String.format("Only room for %s more!", String.valueOf(availability - selectedCount)), Toast.LENGTH_SHORT).show();
        } else if (quantity > numberOfRegistrants) {
            Toast.makeText(this, "Not enough registrants!", Toast.LENGTH_SHORT).show();
        }
        else if (quantity == 0) {
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


    /**
     * Refreshes the UI by clearing and filling the ArrayLists with updated data from the database
     */
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
                    } else if (numberOfSelected[0] == availability) {
                        lotteryButton.setVisibility(View.GONE);
                        lotteryStatusText.setText("Maximum availability has been filled");
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
                        lotteryStatusText.setText("The waitlist is currently empty");
                        lotteryButton.setVisibility(View.GONE);
                    } else {
                        if (numberOfSelected[0] != availability) {
                            lotteryButton.setVisibility(View.VISIBLE);
                        }
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


    /**
     * Gets the userIDs of the entrants in a given array within the database for the current event
     * @param arrayType is which array in the database for the event to get userIDs from
     * @param callback is used for asynchronous data access, returning arrayList through onDataRecieved
     */
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

    /**
     * Gets user data for a given userID
     * @param userID of the user whose data is to be fetched
     * @param callback is used for asynchronous data access, returning user data through onDataRecieved
     */
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
