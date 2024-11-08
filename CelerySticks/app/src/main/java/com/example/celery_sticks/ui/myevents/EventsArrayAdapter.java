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

import com.example.celery_sticks.Event;
import com.example.celery_sticks.R;

import java.util.ArrayList;
import java.util.Date;


public class EventsArrayAdapter extends ArrayAdapter<Event> {
    public EventsArrayAdapter(Context context, ArrayList<Event> events) {super (context, 0, events); }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_content, parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        TextView name = view.findViewById(R.id.event_name);
        TextView time = view.findViewById(R.id.event_time);
        TextView date = view.findViewById(R.id.entrant_name_text);
        TextView location = view.findViewById(R.id.entrant_location_text);
        ImageView image = view.findViewById(R.id.entrant_image);

        Date eventDate = event.getEventDate();

        String dayOfWeek = (String) DateFormat.format("EEEE", eventDate);
        String dayNum = (String) DateFormat.format("dd", eventDate);
        String monthName = (String) DateFormat.format("MMM", eventDate);
        String timeStr = (String) DateFormat.format("hh:mm a", eventDate);

        name.setText(event.getEventName());
        time.setText(String.format("%s - %s", dayOfWeek, timeStr));
        date.setText(String.format("%s %s", monthName, dayNum));
        location.setText(event.getLocation());
        // set image to imageview here

        return view;
    }
}
