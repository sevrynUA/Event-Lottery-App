package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.example.celery_sticks.User;
import com.example.celery_sticks.ui.myevents.AcceptedListFragment;
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

    private ActivityResultLauncher<Intent> acceptedListLauncher;
    private ActivityResultLauncher<Intent> cancelledListLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_selected_entrants);

        acceptedListLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                initialize();
            }
        });
        cancelledListLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                initialize();
            }
        });

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        acceptedButton = findViewById(R.id.list_accepts_button);
        declinedButton = findViewById(R.id.list_declines_button);
        backButton = findViewById(R.id.selected_entrants_back_button);

        selectedListView = findViewById(R.id.waitlist_selected_list);
        selectedAdapter = new UserArrayAdapter(this, selectedList, eventID, false);
        selectedListView.setAdapter(selectedAdapter);

        initialize();

        acceptedButton.setOnClickListener(view -> {
            Intent acceptedIntent = new Intent(this, AcceptedListFragment.class);
            acceptedIntent.putExtra("eventID", eventID);
            acceptedListLauncher.launch(acceptedIntent);
        });

        declinedButton.setOnClickListener(view -> {
            Intent cancelledIntent = new Intent(this, CancelledListFragment.class);
            cancelledIntent.putExtra("eventID", eventID);
            cancelledListLauncher.launch(cancelledIntent);
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
     * Gets the userIDs of the entrants in a given array within the database for the current event
     * @param arrayType is which array in the database for the event to get userIDs from
     * @param callback is used for asynchronous data access, returning arrayList through onDataRecieved
     */
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

    /**
     * Gets user data for a given userID
     * @param userID of the user whose data is to be fetched
     * @param callback is used for asynchronous data access, returning user data through onDataRecieved
     */
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



    /**
     * Refreshes the UI by clearing and filling ArrayList with updated data from the database
     */
    public void initialize() {
        selectedList.clear();
        selectedCount = 0;

        getUsers("selected", new DataCallback() {
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

}

