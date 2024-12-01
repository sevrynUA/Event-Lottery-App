package com.example.celery_sticks.ui.myevents;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.celery_sticks.Event;
import com.example.celery_sticks.GeolocationMap;
import com.example.celery_sticks.R;
import com.example.celery_sticks.User;
import com.example.celery_sticks.databinding.FragmentBrowseEventsBinding;
import com.example.celery_sticks.databinding.FragmentBrowseUsersBinding;
import com.example.celery_sticks.ui.myevents.EventDetailsViewModel;
import com.example.celery_sticks.ui.myevents.EventsArrayAdapter;
import com.example.celery_sticks.ui.myevents.SelectedEntrantsFragment;
import com.example.celery_sticks.ui.myevents.UserArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents list of entrants who accept their invitation to an event
 */
public class AcceptedListFragment extends AppCompatActivity {
    private FragmentBrowseUsersBinding binding;
    private ArrayList<User> browseList = new ArrayList<>();
    private ListView browseListView;
    private UserArrayAdapter browseAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventID;

    private Integer acceptedCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accepted_list);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        initialize();
    }

    /**
     * Refreshes UI by clearing and re-filling arrayList with updated data from the database
     */
    public void initialize() {
        browseList.clear();
        acceptedCounter = 0;


        Button backButton = findViewById(R.id.accepted_list_back);
        browseListView = findViewById(R.id.accepted_list_list);
        browseAdapter = new UserArrayAdapter(this, browseList, eventID, false);
        browseListView.setAdapter(browseAdapter);

        db.collection("events").document(eventID).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        List<String> listOfAccepted = (List<String>) document.get("accepted");
                        if (listOfAccepted != null) {
                            for (String currentUserID : listOfAccepted) {
                                getUserData(currentUserID, new DataCallback() {
                                    @Override
                                    public void onDataRecieved(ArrayList<String> data) {
                                        if (data != null) {
                                            acceptedCounter += 1;
                                            browseList.add(new User(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4)));
                                            browseAdapter.notifyDataSetChanged();
                                            expandListViewHeight(browseListView);
                                        }
                                    }
                                });
                            }

                        }
                    }
                });
        backButton.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * Interface used for asynchronously accessing data for user details
     */
    public interface DataCallback {
        /**
         * Function is run when asynchronous access of data has been completed
         * @param data is the data accessed asynchronously
         */
        void onDataRecieved(ArrayList<String> data);
    }

    /**
     * Gets data from database for a given user
     * @param userID indicates which user to get data for
     * @param callback used for asynchronous data access, returns event data through .onDataRecieved
     */
    public void getUserData(String userID, DataCallback callback) {
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
     * This function expands a dynamic list view for correct height, derived from https://stackoverflow.com/questions/4984313/how-to-set-space-between-listview-items-in-android
     * @param listView listView to expand
     */
    public void expandListViewHeight(ListView listView) {
        ListAdapter viewAdapter = listView.getAdapter();

        if (viewAdapter == null) {
            return;
        }

        ViewGroup listview = listView;
        // total height of all elements in the list
        int totalHeight = 0;

        // add the heights
        for (int i = 0; i < viewAdapter.getCount(); i++) {
            // get item
            View listItem = viewAdapter.getView(i, null, listview);
            // get item length
            listItem.measure(0, 0);
            // increases height
            totalHeight += 150; // height of content
        }

        // set height based on total height
        ViewGroup.LayoutParams par = listView.getLayoutParams();
        // 10dp is the height of a divider
        int heightDP = totalHeight + (10 * (viewAdapter.getCount() + 1));

        // get conversion factor
        float scale = this.getResources().getDisplayMetrics().density;
        // convert from dp to px and store
        par.height = (int) (heightDP * scale);

        // set the layout to the specified parameters
        listView.setLayoutParams(par);

        // submit the changes
        listView.requestLayout();
    }
}
