package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.example.celery_sticks.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Represents the Selected Entrants page of the app
 */

public class SelectedEntrantsFragment extends AppCompatActivity {
    private ArrayList<User> selectedList = new ArrayList<User>();
    private ListView selectedListView;
    private UserArrayAdapter selectedAdapter;

    private Button notifyAll;
    private Button notifyRSVP;
    private Button notifyDeclined;
    private Button acceptedButton;
    private Button declinedButton;

    private Button backButton;
    private String eventID;
    private Integer selectedCount = 0;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_selected_entrants);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        notifyAll = findViewById(R.id.notify_all_selected_button);
        notifyRSVP = findViewById(R.id.notify_RSVP_button);
        notifyDeclined = findViewById(R.id.notify_all_declined_button);
        acceptedButton = findViewById(R.id.list_accepts_button);
        declinedButton = findViewById(R.id.list_declines_button);
        backButton = findViewById(R.id.selected_entrants_back_button);
        //selectedListView = findViewById(R.id.waitlist_selected_list);
        //selectedAdapter = new UserArrayAdapter(this, selectedList);
        //selectedListView.setAdapter(selectedAdapter);

        // initialize();
        notifyAll.setOnClickListener(view -> {
            //notify all
        });
        notifyRSVP.setOnClickListener(view -> {
            //notify rsvp
        });
        notifyDeclined.setOnClickListener(view -> {
            //notify declined
        });

        acceptedButton.setOnClickListener(view -> {
            //accepted list
        });

        declinedButton.setOnClickListener(view -> {
            //declined list
        });

        backButton.setOnClickListener(view -> {
            finish();
        });

    }

    /**
     * Interface used for asynchronously accessing data for event details
     */
    /**
    public interface DataCallback {
        void onDataRecieved(ArrayList<String> data);
    }

    public void getUsers(String arrayType, SelectedEntrantsFragment.DataCallback callback) {
        final ArrayList<String>[] users = new ArrayList[1];
        CollectionReference events = db.collection("events");
        events.document(eventID).get().addOnSuccessListener(event -> {
            if (event.exists()) {
                users[0] = (ArrayList<String>) event.get(arrayType);
                callback.onDataRecieved(users[0]);
            }
        });
    }

    public void getRegistrantData(String userID, SelectedEntrantsFragment.DataCallback callback) {
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


    public void initialize() {
        selectedList.clear();
        selectedCount = 0;

        getUsers("selected", new SelectedEntrantsFragment() {
            @Override
            public void onDataRecieved(ArrayList<String> data) {
                if (data != null) {
                    for (String userID : data) {
                        getRegistrantData(userID, new SelectedEntrantsFragment.DataCallback() {
                            @Override
                            public void onDataRecieved(ArrayList<String> data) {
                                selectedCount++;
                                selectedList.add(new User(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4)));
                                selectedAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }
     */

}

