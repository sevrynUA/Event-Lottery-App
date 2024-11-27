package com.example.celery_sticks.ui.browseusers;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.celery_sticks.Event;
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

public class BrowseUsersFragment extends Fragment {
    private FragmentBrowseUsersBinding binding;
    private ArrayList<User> browseList = new ArrayList<>();
    private ListView browseListView;
    private UserArrayAdapter browseAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<Intent> userDetailsLauncher;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBrowseUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
        }

        userDetailsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                initialize(root);
            }
        });

        initialize(root);
        return root;
    }

    public void initialize(View root) {
        browseList.clear();

        browseListView = root.findViewById(R.id.users_browse_list);
        browseAdapter = new UserArrayAdapter(getContext(), browseList, null, true);
        browseListView.setAdapter(browseAdapter);

        db.collection("users").get()
                        .addOnSuccessListener(success -> {
                            for (QueryDocumentSnapshot document: success) {
                                String thisUserID = document.getId();
                                if (thisUserID != userID) { // dont add current user to list
                                    getUserData(thisUserID, new DataCallback() {
                                        @Override
                                        public void onDataRecieved(ArrayList<String> data) {
                                            if (data != null) {
                                                browseList.add(new User(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4)));
                                                browseAdapter.notifyDataSetChanged();
                                                expandListViewHeight(browseListView);
                                            }
                                        }
                                    });
                                }
                            }
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
        float scale = getContext().getResources().getDisplayMetrics().density;
        // convert from dp to px and store
        par.height = (int) (heightDP * scale);

        // set the layout to the specified parameters
        listView.setLayoutParams(par);

        // submit the changes
        listView.requestLayout();
    }
}
