package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.example.celery_sticks.User;
import com.example.celery_sticks.databinding.FragmentBrowseUsersBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CancelledListFragment extends AppCompatActivity {
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
        setContentView(R.layout.cancelled_list);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        initialize();
    }

    public void initialize() {
        browseList.clear();
        acceptedCounter = 0;


        Button backButton = findViewById(R.id.cancelled_list_back);
        browseListView = findViewById(R.id.cancelled_list_list);
        browseAdapter = new UserArrayAdapter(this, browseList, eventID, false);
        browseListView.setAdapter(browseAdapter);

        db.collection("events").document(eventID).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        List<String> listOfAccepted = (List<String>) document.get("cancelled");
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

    public interface DataCallback {
        /**
         * Function is run when asynchronous access of data has been completed
         * @param data is the data accessed asynchronously
         */
        void onDataRecieved(ArrayList<String> data);
    }

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
