package com.example.celery_sticks.ui.browseevents;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.format.DateFormat;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.celery_sticks.Event;
import com.example.celery_sticks.R;
import com.example.celery_sticks.databinding.FragmentBrowseEventsBinding;
import com.example.celery_sticks.ui.myevents.EventDetailsViewModel;
import com.example.celery_sticks.ui.myevents.EventsArrayAdapter;
import com.example.celery_sticks.ui.myevents.MyEventsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the BrowseEvents page of the app for admins only
 */
public class BrowseEventsFragment extends Fragment {
    private FragmentBrowseEventsBinding binding;
    private ArrayList<Event> browseList = new ArrayList<>();
    private ListView browseListView;
    private EventsArrayAdapter browseAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;

    private ActivityResultLauncher<Intent> eventDetailsLauncher;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBrowseEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
        }

        initialize(root);

        eventDetailsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                initialize(root);
            }
        });
        return root;
    }

    /**
     * Refreshes UI by clearing and re-filling arrayList with updated data from the database
     * @param root used to get view IDs for updating UI
     */
    public void initialize(View root) {
        browseList.clear();

        browseListView = root.findViewById(R.id.events_browse_list);
        browseAdapter = new EventsArrayAdapter(getContext(), browseList);
        browseListView.setAdapter(browseAdapter);

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
                        String eventQR = document.getString("qrcode");
                        String eventLocation = document.getString("location");
                        String organizerID = document.getString("organizerID");

                        browseList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
                        refreshList();
                    }
                }
            }
        });



        browseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                eventClicked(adapterView, view, i, l, "admin");
            }
        });
    }

    /**
     * Refreshes UI by calling .notifyDataSetChanged() on ArrayList
     */
    public void refreshList() {
        browseAdapter.notifyDataSetChanged();
        expandListViewHeight(browseListView);
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
        float scale = getContext().getResources().getDisplayMetrics().density;
        // convert from dp to px and store
        par.height = (int) (heightDP * scale);

        // set the layout to the specified parameters
        listView.setLayoutParams(par);

        // submit the changes
        listView.requestLayout();
    }

    /**
     * Interface used for asynchronously accessing data for event details
     */
    public interface EventDataCallback {
        /**
         * Function is run when asynchronous access of data has been completed
         * @param eventData is the data accessed asynchronously
         */
        void onDataRecieved(Map<String, Object> eventData);
    }

    /**
     * Opens event details page when an event is clicked
     * @param adapterView is the ListView in which an event was clicked
     * @param view is the event which was clicked
     * @param i is the index of the corresponding event object in the ArrayList
     * @param l is the row index of the clicked item provided by the onItemClick function; not used here
     * @param eventCategory is a string indicating which ArrayList contains the clicked event
     */
    public void eventClicked(AdapterView<?> adapterView, View view, int i, long l, String eventCategory) {
        Intent intent = new Intent(getContext(), EventDetailsViewModel.class);
        Map<String, Object> eventData = new HashMap<>();
        String eventID = browseList.get(i).getEventId();

        getEventData(eventID, new MyEventsFragment.EventDataCallback() {
            @Override
            public void onDataRecieved(Map<String, Object> eventData) {
                if (!eventData.isEmpty()) {
                    String dateDOW = (String) DateFormat.format("EEEE", ((Timestamp) eventData.get("date")).toDate());
                    String dateMonth = (String) DateFormat.format("MMMM", ((Timestamp) eventData.get("date")).toDate());
                    String dateDayNum = (String) DateFormat.format("dd", ((Timestamp) eventData.get("date")).toDate());
                    String dateTimeStr = (String) DateFormat.format("hh:mm a", ((Timestamp) eventData.get("date")).toDate());

                    String closeDOW = (String) DateFormat.format("EEEE", ((Timestamp) eventData.get("close")).toDate());
                    String closeMonth = (String) DateFormat.format("MMMM", ((Timestamp) eventData.get("close")).toDate());
                    String closeDayNum = (String) DateFormat.format("dd", ((Timestamp) eventData.get("close")).toDate());
                    String closeTimeStr = (String) DateFormat.format("hh:mm a", ((Timestamp) eventData.get("close")).toDate());

                    String openDOW = (String) DateFormat.format("EEEE", ((Timestamp) eventData.get("open")).toDate());
                    String openMonth = (String) DateFormat.format("MMMM", ((Timestamp) eventData.get("open")).toDate());
                    String openDayNum = (String) DateFormat.format("dd", ((Timestamp) eventData.get("open")).toDate());
                    String openTimeStr = (String) DateFormat.format("hh:mm a", ((Timestamp) eventData.get("open")).toDate());

                    intent.putExtra("date", String.format("%s, %s %s - %s", dateDOW, dateMonth, dateDayNum, dateTimeStr));
                    intent.putExtra("close", String.format("%s, %s %s - %s", closeDOW, closeMonth, closeDayNum, closeTimeStr));
                    intent.putExtra("open", String.format("%s, %s %s - %s", openDOW, openMonth, openDayNum, openTimeStr));
                    intent.putExtra("name", (String) eventData.get("name"));
                    intent.putExtra("description", (String) eventData.get("description"));
                    intent.putExtra("image", (String) eventData.get("image"));
                    intent.putExtra("qrcode", (String) eventData.get("qrcode"));
                    intent.putExtra("location", (String) eventData.get("location"));
                    intent.putExtra("availability", (String) eventData.get("availability"));
                    intent.putExtra("price", (String) eventData.get("price"));
                    intent.putExtra("eventID", (String) eventData.get("eventID"));
                    intent.putExtra("category", eventCategory);
                    intent.putExtra("userID", userID);


                    eventDetailsLauncher.launch(intent);
                }
            }
        });


    }

    /**
     * Gets data from database for a given event
     * @param eventID indicates which event to get data for
     * @param callback used for asynchronous data access, returns event data through .onDataRecieved
     * @return event data in case of .onDataRecieved failure
     */
    public Map<String, Object> getEventData(String eventID, MyEventsFragment.EventDataCallback callback) {
        DocumentReference ref = db.collection("events").document(eventID);
        Map<String, Object> eventData = new HashMap<>();
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        eventData.put("date", document.getTimestamp("date"));
                        eventData.put("close", document.getTimestamp("close"));
                        eventData.put("open", document.getTimestamp("open"));
                        eventData.put("name", document.getString("name"));
                        eventData.put("description", document.getString("description"));
                        eventData.put("qrcode", document.getString("qrcode"));
                        eventData.put("location", document.getString("location"));
                        eventData.put("availability", document.getString("availability"));
                        eventData.put("price", document.getString("price"));
                        eventData.put("eventID", document.getId());

                    }
                    callback.onDataRecieved(eventData);
                }
            }
        });
        return eventData;
    }
}
