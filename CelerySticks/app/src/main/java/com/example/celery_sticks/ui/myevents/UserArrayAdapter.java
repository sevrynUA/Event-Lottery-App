package com.example.celery_sticks.ui.myevents;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.celery_sticks.R;
import com.example.celery_sticks.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * Adapter which manages the ListView displaying users in an event's "Manage Entrants" activity
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    public UserArrayAdapter(Context context, ArrayList<User> users) {super (context, 0, users); }
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

        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        entrantNameText.setText(String.format("%s %s", firstName, lastName));
        // TODO set location with geolocation here
        // TODO set image here

        return view;
    }
}
