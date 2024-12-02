package com.example.celery_sticks.ui.myevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.celery_sticks.Event;
import com.example.celery_sticks.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * Adapter which manages the ListView displaying the events in the MyEvents activity
 */
public class EventsArrayAdapter extends ArrayAdapter<Event> {
    private Random rand = new Random();
    private int colorStart = rand.nextInt(5);

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
        TextView date = view.findViewById(R.id.event_date_text);
        TextView location = view.findViewById(R.id.event_location_text);
        ImageView image = view.findViewById(R.id.event_image);

        Date eventDate = event.getEventDate();

        String dayOfWeek = (String) DateFormat.format("EEEE", eventDate);
        String dayNum = (String) DateFormat.format("dd", eventDate);
        String monthName = (String) DateFormat.format("MMM", eventDate);
        String timeStr = (String) DateFormat.format("hh:mm a", eventDate);

        name.setText(event.getEventName());
        time.setText(String.format("%s - %s", dayOfWeek, timeStr));
        date.setText(String.format("%s %s", monthName, dayNum));
        location.setText(event.getLocation());

        // set color here
        if ((position + colorStart) % 5 == 1) {
            view.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.list_blue));
        } else if((position + colorStart) % 5 == 2) {
            view.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.list_orange));
        } else if((position + colorStart) % 5 == 3) {
            view.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.list_green));
        } else if((position + colorStart) % 5 == 4) {
            view.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.list_red));
        }

        // set image to imageview here
        String imageData = event.getPosterImage();
        if (imageData != null) {
            if (!imageData.equals("")) {

                byte[] decodedImage = Base64.decode(imageData, Base64.DEFAULT);

                Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                // set qrImage to decoded bitmap
                image.setImageBitmap(qrBitmap);
            }
        }

        return view;
    }
}
