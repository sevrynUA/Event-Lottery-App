package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyEventsFragment extends Fragment {

    private FragmentMyEventsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyEventsViewModel myEventsViewModel =
                new ViewModelProvider(this).get(MyEventsViewModel.class);

        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayList<Event> registeredList = new ArrayList<>();
        ListView registeredListView;
        EventsArrayAdapter registeredAdapter;

        ArrayList<Event> acceptedList = new ArrayList<>();
        ListView acceptedListView;
        EventsArrayAdapter acceptedAdapter;

        ArrayList<Event> invitationList = new ArrayList<>();
        ListView invitationListView;
        EventsArrayAdapter invitationAdapter;

        registeredListView = root.findViewById(R.id.registered_list);
        registeredAdapter = new EventsArrayAdapter(getContext(), registeredList);
        registeredListView.setAdapter(registeredAdapter);

        acceptedListView = root.findViewById(R.id.accepted_list);
        acceptedAdapter = new EventsArrayAdapter(getContext(), acceptedList);
        acceptedListView.setAdapter(acceptedAdapter);

        invitationListView = root.findViewById(R.id.invitation_list);
        invitationAdapter = new EventsArrayAdapter(getContext(), invitationList);
        invitationListView.setAdapter(invitationAdapter);

        Button createEventButton = root.findViewById(R.id.create_event_button);



        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                        String eventDetailsQR = document.getString("qrcode");
                        String eventSignUpQR = document.getString("signupqrcode");
                        String eventLocation = document.getString("location");
                        Boolean registered = document.getBoolean("registered");
                        Boolean accepted = document.getBoolean("accepted");
                        if (registered && !accepted) {
                            registeredList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventDetailsQR, eventSignUpQR, eventLocation));
                            registeredAdapter.notifyDataSetChanged();
                        } else if (registered && accepted) {
                            acceptedList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventDetailsQR, eventSignUpQR, eventLocation));
                            acceptedAdapter.notifyDataSetChanged();
                        } else if (!registered && !accepted && eventName != "test") {
                            invitationList.add(new Event(eventName, eventID, eventDescription, eventImage, eventDate, eventClose, eventOpen, eventDetailsQR, eventSignUpQR, eventLocation));
                            invitationAdapter.notifyDataSetChanged();
                        }
                    }
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

        createEventButton.setOnClickListener(view -> {
                createEventClicked();
            });




        return root;
    }

    public void eventClicked(AdapterView<?> adapterView, View view, int i, long l, String eventCategory) {
        Intent intent = new Intent(getContext(), EventDetailsViewModel.class);

        //intent.putExtra("field", data); // use this to add in info about the event
        startActivity(intent);
    }

    public void createEventClicked() {
        Intent intent = new Intent(getContext(), AddEventFragment.class);

        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}