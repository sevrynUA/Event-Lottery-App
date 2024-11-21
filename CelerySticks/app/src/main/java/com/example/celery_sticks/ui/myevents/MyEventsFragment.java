package com.example.celery_sticks.ui.myevents;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.celery_sticks.Event;
import com.example.celery_sticks.R;
import com.example.celery_sticks.databinding.FragmentMyEventsBinding;
import com.example.celery_sticks.ui.eventadd.AddEventFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Represents the MyEvents page (homepage) of the app, used for managing events
 */
public class MyEventsFragment extends Fragment {

    private FragmentMyEventsBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Event> registeredList = new ArrayList<>();
    private ListView registeredListView;
    private EventsArrayAdapter registeredAdapter;

    private ArrayList<Event> acceptedList = new ArrayList<>();
    private ListView acceptedListView;
    private EventsArrayAdapter acceptedAdapter;

    private ArrayList<Event> invitationList = new ArrayList<>();
    private ListView invitationListView;
    private EventsArrayAdapter invitationAdapter;

    private ArrayList<Event> createdList = new ArrayList<>();
    private ListView createdListView;
    private EventsArrayAdapter createdAdapter;

    private ArrayList<Event> temporaryList = new ArrayList<>();
    private ListView temporaryListView;
    private EventsArrayAdapter temporaryAdapter;

    private ActivityResultLauncher<Intent> createEventLauncher; // used for refreshing list
    private ActivityResultLauncher<Intent> eventDetailsLauncher;
    private String userID = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyEventsViewModel myEventsViewModel =
                new ViewModelProvider(this).get(MyEventsViewModel.class);

        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initialize(root);

        createEventLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                createEvent(data.getStringExtra("eventID"));
            }
        });
        eventDetailsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                initialize(root);
            }
        });

        // Recieve userID and set to variable
        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
        }

        return root;
    } // end of onCreateView


    /**
     * On activity start, calls for re-checking of whether user should have access to organizer features
     */
    public void onStart() {
        super.onStart();
        if (userID != null) {
            // Display only entrant UI if user is only an entrant
            updateOrganizerUIVisibility(userID);
        }
    }

    /**
     * Refreshes UI by clearing and re-filling arrayLists with updated data from the database
     * @param root used to get view IDs for updating UI
     */
    public void initialize(View root) {
        registeredList.clear();
        invitationList.clear();
        acceptedList.clear();
        createdList.clear();
        temporaryList.clear();

        registeredListView = root.findViewById(R.id.registered_list);
        registeredAdapter = new EventsArrayAdapter(getContext(), registeredList);
        registeredListView.setAdapter(registeredAdapter);

        acceptedListView = root.findViewById(R.id.accepted_list);
        acceptedAdapter = new EventsArrayAdapter(getContext(), acceptedList);
        acceptedListView.setAdapter(acceptedAdapter);

        invitationListView = root.findViewById(R.id.invitation_list);
        invitationAdapter = new EventsArrayAdapter(getContext(), invitationList);
        invitationListView.setAdapter(invitationAdapter);

        createdListView = root.findViewById(R.id.created_by_me_list);
        createdAdapter = new EventsArrayAdapter(getContext(), createdList);
        createdListView.setAdapter(createdAdapter);

        // TEMPORARY LIST FOR TESTING PURPOSES
        temporaryListView = root.findViewById(R.id.temporary_list);
        temporaryAdapter = new EventsArrayAdapter(getContext(), temporaryList);
        temporaryListView.setAdapter(temporaryAdapter);

        Button createEventButton = root.findViewById(R.id.create_event_button);

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

                        if (organizerID != null && organizerID.equals(userID)) {
                            createdList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
                        } else {
                            checkIfUserRegistered(eventID, isUserRegistered -> {
                                if (isUserRegistered) {
                                    registeredList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
                                } else {
                                    temporaryList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
                                }
                                refreshLists();
                            });
                        }
//                        if (registered && !accepted) {
//                            registeredList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
//                            registeredAdapter.notifyDataSetChanged();
//                        } else if (registered && accepted) {
//                            acceptedList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
//                            acceptedAdapter.notifyDataSetChanged();
//                        } else if (!registered && !accepted && eventName != "test") {
//                            invitationList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
//                            invitationAdapter.notifyDataSetChanged();
//                        }
                    }

                    refreshLists();
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
        createdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                eventClicked(adapterView, view, i, l, "created");
            }
        });
        temporaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                eventClicked(adapterView, view, i, l, "temporary");
            }
        });

        createEventButton.setOnClickListener(view -> {
            createEventClicked();
        });
    }

    /**
     * This function expands each list view so that the my events menu only needs one scroll bar for all lists derived from https://stackoverflow.com/questions/4984313/how-to-set-space-between-listview-items-in-android
     * @param listView list view to expand
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
     * Interface used for asynchronously accessing data for registration
     */
    public interface RegistrationCallback {
        /**
         * Function is run when asynchronous access of data has been completed
         * @param eventData is the data accessed asynchronously
         */
        void onDataRecieved(ArrayList<String> eventData);
    }

    /**
     * Interface used for asynchronously returning data for registration
     */
    public interface RegistrationWaitCallback {
        /**
         * Function is run when asynchronous access of data has been completed
         * @param isRegistered is a boolean indicating whether the user is registered
         */
        void onDataReturned(Boolean isRegistered);
    }

    /**
     * Checks if user is registered in a given event
     * @param eventID is the ID of the event
     * @param callback is used for asynchronous data access, returning boolean of whether user is registered through onDataRecieved
     * @return null if no boolean is returned by onDataReturned
     */
    public Boolean checkIfUserRegistered(String eventID, EventDetailsViewModel.RegistrationWaitCallback callback) {
        getRegistrants(eventID, new EventDetailsViewModel.EventDetailsCallback() {
            @Override
            public void onDataRecieved(ArrayList<String> eventData) {
                Boolean isUserRegistered = eventData.contains(userID);
                callback.onDataReturned(isUserRegistered);
            }
        });
        return null;
    }

    /**
     * Gets array of registrants from the database for a given event
     * @param eventID is the ID of the event
     * @param callback is used for asynchronous data access, returning arrayList of userIDs through onDataRecieved
     * @return null if no data is returned by onDataReturned
     */
    public ArrayList<String> getRegistrants(String eventID, EventDetailsViewModel.EventDetailsCallback callback) {
        DocumentReference ref = db.collection("events").document(eventID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> registrants = null;
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        registrants = (ArrayList<String>) document.get("registrants");
                    }
                    callback.onDataRecieved(registrants);
                }
            }
        });
        return null;
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
        String eventID = "";
        if (eventCategory == "registered") {
            eventID = registeredList.get(i).getEventId();
        } else if (eventCategory == "accepted") {
            eventID = acceptedList.get(i).getEventId();
        } else if (eventCategory == "invitation") {
            eventID = invitationList.get(i).getEventId();
        } else if (eventCategory == "created") {
            eventID = createdList.get(i).getEventId();
        } else if (eventCategory == "temporary") {
            eventID = temporaryList.get(i).getEventId();
        }
        getEventData(eventID, new EventDataCallback() {
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
    public Map<String, Object> getEventData(String eventID, EventDataCallback callback) {
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

    /**
     * Displays or hides organizer UI features depending on the user's role
     * @param userID is the ID of the current user
     */
    private void updateOrganizerUIVisibility(String userID) {
        TextView createdByMeTitle = requireActivity().findViewById(R.id.created_by_me_title);
        ListView createdByMeList = requireActivity().findViewById(R.id.created_by_me_list);
        Button createEventButton = requireActivity().findViewById(R.id.create_event_button);
        DocumentReference ref = db.collection("users").document(userID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String role = document.getString("role");
                    if (role != null) {
                        if (role.equals("entrant")) {
                            createdByMeTitle.setVisibility(View.GONE);
                            createdByMeList.setVisibility(View.GONE);
                            createEventButton.setVisibility(View.GONE);
                        } else {
                            createdByMeTitle.setVisibility(View.VISIBLE);
                            createdByMeList.setVisibility(View.VISIBLE);
                            createEventButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    /**
     * Creates event object to be stored in an ArrayList when new event is added to database
     * @param eventID is the ID of the newly created event
     */
    public void createEvent(String eventID) {
        getEventData(eventID, new EventDataCallback() {
            @Override
            public void onDataRecieved(Map<String, Object> eventData) {
                if (!eventData.isEmpty()) {
                    String eventName = (String) eventData.get("name");
                    String eventDescription = (String) eventData.get("description");
                    String eventImage = (String) eventData.get("image");
                    Timestamp eventDate = (Timestamp) eventData.get("date");
                    Timestamp eventClose = (Timestamp) eventData.get("close");
                    Timestamp eventOpen = (Timestamp) eventData.get("open");
                    String eventQR = (String) eventData.get("qrcode");
                    String eventLocation = (String) eventData.get("location");
                    createdList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
                    createdAdapter.notifyDataSetChanged();
                    refreshLists();
                }
            }
        });
    }

    /**
     * Refreshes UI by calling .notifyDataSetChanged() on all ArrayLists
     */
    public void refreshLists() {
        registeredAdapter.notifyDataSetChanged();
        invitationAdapter.notifyDataSetChanged();
        acceptedAdapter.notifyDataSetChanged();
        createdAdapter.notifyDataSetChanged();
        temporaryAdapter.notifyDataSetChanged();

        expandListViewHeight(temporaryListView);
        expandListViewHeight(registeredListView);
        expandListViewHeight(acceptedListView);
        expandListViewHeight(invitationListView);
        expandListViewHeight(createdListView);
    }

    /**
     * Opens event creation activity when "Create New Event" button is clicked
     */
    public void createEventClicked() {
        Intent intent = new Intent(getContext(), AddEventFragment.class);
        createEventLauncher.launch(intent);
    }


    /**
     * Function called when activity is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}