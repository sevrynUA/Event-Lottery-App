package com.example.celery_sticks.ui.myevents;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.celery_sticks.Notification;
import com.example.celery_sticks.R;
import com.example.celery_sticks.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.celery_sticks.ui.browseusers.ProfileDetailsViewModel;

import java.util.ArrayList;

/**
 * Adapter which manages the ListView displaying users in various activities.
 * This adapter handles the display of user details and allows for user removal from an event or the database.
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private FirebaseFirestore db;
    private String eventID;
    private boolean isDeleteMode;

    /**
     * Creates the UserArrayAdapter
     * @param context the current context for  views
     * @param users the list of users to be displayed in the ListView
     * @param eventID the ID of the event associated for removal
     * @param isDeleteMode flag indicating viewing profiles in admin mode
     */
    public UserArrayAdapter(Context context, ArrayList<User> users, String eventID, boolean isDeleteMode) {super (context, 0, users); db = FirebaseFirestore.getInstance(); this.eventID = eventID; this.isDeleteMode = isDeleteMode;}


    /**
     * Retrieves the view for each item in the list.
     * @param position the position of the user in the list
     * @param convertView the recycled view (if any) to reuse
     * @param parent the parent View that this view will be attached to
     * @return the View representing the user at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.entrants_in_list_content, parent, false);
        } else {
            view = convertView;
        }
        User user = getItem(position);
        TextView entrantNameText = view.findViewById(R.id.entrant_name_text);
        TextView entrantLocationText = view.findViewById(R.id.entrant_location_text);
        ImageView image = view.findViewById(R.id.entrant_image);
        ImageView acceptedImage = view.findViewById(R.id.entrant_accepted);
        ImageView cancelledImage = view.findViewById(R.id.entrant_cancelled);
        ImageButton deleteButton = view.findViewById(R.id.delete_user_button);


        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        entrantNameText.setText(String.format("%s %s", firstName, lastName));
        // TODO set location with geolocation here
        // TODO set image here

        deleteButton.setVisibility(View.VISIBLE); // Set default for delete user button
        deleteButton.setEnabled(true);

        if (eventID != null && !eventID.isEmpty()) {
            DocumentReference eventDoc = db.collection("events").document(eventID);

            eventDoc.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    ArrayList<String> cancelledList = (ArrayList<String>) documentSnapshot.get("cancelled");
                    ArrayList<String> acceptedList = (ArrayList<String>) documentSnapshot.get("accepted");

                    // When viewing the cancelled entrants list, make delete user image invisible and delete button disabled
                    // Set cancelled entrants image to visible
                    if ((cancelledList != null && cancelledList.contains(user.getUserID()))){
                        image.setVisibility(View.GONE);
                        cancelledImage.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.GONE);
                        deleteButton.setEnabled(false);

                    // When viewing the accepted entrants list, make delete user image invisible and delete button disabled
                    // Set accepted entrants image to visible
                    } else if (acceptedList != null && acceptedList.contains(user.getUserID())){
                        image.setVisibility(View.GONE);
                        acceptedImage.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.GONE);
                        deleteButton.setEnabled(false);
                    } else { // Ensure delete user button / image is visable
                        deleteButton.setVisibility(View.VISIBLE);
                        deleteButton.setEnabled(true);
                    }
                }
            });
        }

        if (isDeleteMode) {
            view.setOnClickListener(v -> {
                // Trigger navigation to the user's profile
                String clickedUserID = user.getUserID();
                Context context = getContext();
                Intent intent = new Intent(context, ProfileDetailsViewModel.class);
                intent.putExtra("clickedUserID", clickedUserID);
                context.startActivity(intent);
            });
        }

        deleteButton.setOnClickListener(v -> {
            if (isDeleteMode) { // When delete mode is active, remove user from db. This is for admin profile browsing
                db.collection("users").document(user.getUserID()).delete()
                        .addOnSuccessListener(aVoid -> {
                            remove(user);
                            notifyDataSetChanged();
                        });
            } else {
                DocumentReference eventDoc = db.collection("events").document(eventID);
                eventDoc.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("name");
                        ArrayList<String> registrantsList = (ArrayList<String>) documentSnapshot.get("registrants");
                        ArrayList<String> selectedList = (ArrayList<String>) documentSnapshot.get("selected");

                        // When viewing user in the registered list, remove user from the list in the db and local list
                        if (registrantsList != null && registrantsList.contains(user.getUserID())) {
                            remove(user);
                            notifyDataSetChanged();
                            eventDoc.update("registrants", FieldValue.arrayRemove(user.getUserID()));
                        }

                        // When viewing user in the selected list, remove user from the list in the db and local list
                        if (selectedList != null && selectedList.contains(user.getUserID())) {
                            remove(user);
                            notifyDataSetChanged();
                            eventDoc.update("selected", FieldValue.arrayRemove(user.getUserID()));
                        }
                        // Users removed get added to the cancelled list to ensure they cannot reregister
                        eventDoc.update("cancelled", FieldValue.arrayUnion(user.getUserID()))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "User has been removed.", Toast.LENGTH_SHORT).show();
                                });
                        String notificationMessage = "You have been removed from the waitlist for " + eventName + ". You did not accept the invitation in time.";
                        ArrayList<String> removalID = new ArrayList<String>();
                        removalID.add(user.getUserID());
                        Notification cancelledNotification = new Notification("You have been cancelled from the " + eventName + " waitlist.", notificationMessage, removalID);
                        cancelledNotification.newNotification();
                    }
                });
            }
        });
        return view;
    }
}


