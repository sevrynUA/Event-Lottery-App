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
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
    private String userID = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyEventsViewModel myEventsViewModel =
                new ViewModelProvider(this).get(MyEventsViewModel.class);

        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
                        Boolean registered = document.getBoolean("registered");
                        Boolean accepted = document.getBoolean("accepted");
                        String organizerID = document.getString("organizerID");

                        if (organizerID != null && organizerID.equals(userID)) {
                            createdList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
                        } else {
                            temporaryList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventQR, eventLocation));
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


        createEventLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                createEvent(data.getStringExtra("eventID"));
            }
        });

        // Recieve userID and set to variable
        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
        }

        return root;
    } // end of onCreate



    public void onStart() {
        super.onStart();
        if (userID != null) {
            // Display only entrant UI if user is only an entrant
            updateOrganizerUIVisibility(userID);
        }
    }

    public interface EventDataCallback {
        void onDataRecieved(Map<String, Object> eventData);
    }

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
                    intent.putExtra("registered", (Boolean) eventData.get("registered"));
                    intent.putExtra("accepted", (Boolean) eventData.get("accepted"));
                    intent.putExtra("availability", (String) eventData.get("availability"));
                    intent.putExtra("price", (String) eventData.get("price"));

                    startActivity(intent);
                }
            }
        });


    }

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
                        eventData.put("image", document.getString("image"));
                        eventData.put("qrcode", document.getString("qrcode"));
                        eventData.put("location", document.getString("location"));
                        eventData.put("availability", document.getString("availability"));
                        eventData.put("price", document.getString("price"));
                        eventData.put("registered", document.getBoolean("registered"));
                        eventData.put("accepted", document.getBoolean("accepted"));

                    }
                    callback.onDataRecieved(eventData);
                }
            }
        });
        return eventData;
    }

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
                    Log.d("food", String.valueOf(createdList.size()));
                }
            }
        });
    }

    public void refreshLists() {
        registeredAdapter.notifyDataSetChanged();
        invitationAdapter.notifyDataSetChanged();
        acceptedAdapter.notifyDataSetChanged();
        createdAdapter.notifyDataSetChanged();
        temporaryAdapter.notifyDataSetChanged();
    }

    public void createEventClicked() {
        Intent intent = new Intent(getContext(), AddEventFragment.class);
        createEventLauncher.launch(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}