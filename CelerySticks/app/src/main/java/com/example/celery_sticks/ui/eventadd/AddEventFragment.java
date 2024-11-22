package com.example.celery_sticks.ui.eventadd;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.celery_sticks.QRCodeGenerator;
import com.example.celery_sticks.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * This is the fragment which manages the UI and inputs for the creation of a new event
 */
public class AddEventFragment extends AppCompatActivity {

    private EditText title;
    private EditText participationLimit;
    private EditText cost;
    private EditText description;
    private Button geolocationButton;

    private Button openDateMonthButton;
    private Button openDateTimeButton;
    private Button closeDateMonthButton;
    private Button closeDateTimeButton;
    private Button dateMonthButton;
    private Button dateTimeButton;
    private Button dateConfirmButton;
    private Button openDateConfirmButton;
    private Button closeDateConfirmButton;

    private DatePicker openDateMonth;
    private DatePicker closeDateMonth;
    private DatePicker dateMonth;

    private TimePicker openDateTime;
    private TimePicker closeDateTime;
    private TimePicker dateTime;


    private EditText location;
    private Button createEventButton;
    private Button cancelButton;
    private RelativeLayout geolocationButtonBackground;
    private RelativeLayout geolocationButtonIcon;

    private FirebaseFirestore db;

    private boolean geolocationStatus = true; // defaults to true for UI purposes

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm"); // format used for parsing strings into dates

    /**
     * Main functions executed for the create event screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        //time buttons and pickers
        openDateMonthButton = findViewById(R.id.event_open_date_month_button);
        openDateTimeButton = findViewById(R.id.event_open_date_time_button);
        closeDateMonthButton = findViewById(R.id.event_close_date_month_button);
        closeDateTimeButton = findViewById(R.id.event_close_date_time_button);
        dateMonthButton = findViewById(R.id.event_date_month_button);
        dateTimeButton = findViewById(R.id.event_date_time_button);
        dateConfirmButton = findViewById(R.id.event_date_confirm_button);
        closeDateConfirmButton = findViewById(R.id.event_close_date_confirm_button);
        openDateConfirmButton = findViewById(R.id.event_open_date_confirm_button);

        openDateMonth = findViewById(R.id.event_open_date_month);
        closeDateMonth = findViewById(R.id.event_close_date_month);
        dateMonth = findViewById(R.id.event_date_month);

        openDateTime = findViewById(R.id.event_open_date_time);
        closeDateTime = findViewById(R.id.event_close_date_time);
        dateTime = findViewById(R.id.event_date_time);

        // everything else
        title = findViewById(R.id.event_title_input);
        participationLimit = findViewById(R.id.event_participation_limit_input);
        cost = findViewById(R.id.event_cost_input);
        description = findViewById(R.id.event_description_input);
        location = findViewById(R.id.event_location_input);
        geolocationButton = findViewById(R.id.geolocation_button_event_create);
        createEventButton = findViewById(R.id.create_event_confirm_button);
        cancelButton = findViewById(R.id.create_event_cancel_button);

        geolocationButtonBackground = findViewById(R.id.geolocation_button_event_create_background);
        geolocationButtonIcon = findViewById(R.id.geolocation_button_event_create_icon);


        // firebase database
        db = FirebaseFirestore.getInstance();

        // functions for geolocation button
        geolocationButton.setOnClickListener(view -> {
            if (geolocationStatus) {
                geolocationStatus = false;
                geolocationButtonBackground.setBackgroundResource(R.drawable.login_user_type_button_unselected);
                geolocationButtonIcon.setBackgroundResource(R.drawable.x_mark);
            }
            else {
                geolocationStatus = true;
                geolocationButtonBackground.setBackgroundResource(R.drawable.login_user_type_button_selected);
                geolocationButtonIcon.setBackgroundResource(R.drawable.checkmark);
            }
        });

        // save data and store it in the database
        createEventButton.setOnClickListener(view -> {
            saveEventData();
        });

        // cancel the creation and exit intent
        cancelButton.setOnClickListener(view -> {
            finish();
        });

        // if time buttons are pressed run function
        dateMonthButton.setOnClickListener(view -> {
            monthButtonPressed(dateMonthButton, dateTimeButton, dateMonth, dateConfirmButton);
        });

        dateTimeButton.setOnClickListener(view -> {
            timeButtonPressed(dateTimeButton, dateMonthButton, dateTime, dateConfirmButton);
        });

        openDateMonthButton.setOnClickListener(view -> {
            monthButtonPressed(openDateMonthButton, openDateTimeButton, openDateMonth, openDateConfirmButton);
        });

        openDateTimeButton.setOnClickListener(view -> {
            timeButtonPressed(openDateTimeButton, openDateMonthButton, openDateTime, openDateConfirmButton);
        });

        closeDateMonthButton.setOnClickListener(view -> {
            monthButtonPressed(closeDateMonthButton, closeDateTimeButton, closeDateMonth, closeDateConfirmButton);
        });

        closeDateTimeButton.setOnClickListener(view -> {
            timeButtonPressed(closeDateTimeButton, closeDateMonthButton, closeDateTime, closeDateConfirmButton);
        });

    }

    /**
     * Given the following related buttons, maximizes the datePicker view and stores selection before minimizing
     * @param pressed the specific month button pressed
     * @param other the other button in the constraint view (time button)
     * @param selected the DatePicker to maximize
     * @param confirm the button which confirms the user has chosen their date
     */
    public void monthButtonPressed(Button pressed, Button other, DatePicker selected, Button confirm) {
        // make buttons invisible and calender view visible (confirm button as well)
        pressed.setVisibility(View.GONE);
        other.setVisibility(View.GONE);
        selected.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.VISIBLE);

        // store the results once the confirm button is pressed
        confirm.setOnClickListener(view -> {
            String date = selected.getDayOfMonth() + "/" + (selected.getMonth() + 1) + "/" + selected.getYear();
            pressed.setText(date);

            // revert visibilities
            pressed.setVisibility(View.VISIBLE);
            other.setVisibility(View.VISIBLE);
            selected.setVisibility(View.GONE);
            confirm.setVisibility(View.GONE);
        });
    }

    /**
     * Given the following related buttons, maximizes the TimePicker view and stores selection before minimizing
     * @param pressed the specific month button pressed
     * @param other the other button in the constraint view (date button)
     * @param selected the timePicker to maximize
     * @param confirm the button which confirms the user has chosen their date
     */
    public void timeButtonPressed(Button pressed, Button other, TimePicker selected, Button confirm) {
        // make buttons invisible and timePicker view visible (confirm button as well)
        pressed.setVisibility(View.GONE);
        other.setVisibility(View.GONE);
        selected.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.VISIBLE);

        // store the results once the confirm button is pressed
        confirm.setOnClickListener(view -> {
            String time = selected.getHour() + ":" + selected.getMinute();
            pressed.setText(time);

            // revert visibilities
            pressed.setVisibility(View.VISIBLE);
            other.setVisibility(View.VISIBLE);
            selected.setVisibility(View.GONE);
            confirm.setVisibility(View.GONE);
        });
    }

    /**
     * Takes a integer input and places a zero in front if necessary (converts to two digits)
     * @param input the integer to convert to two digits
     * @return a String output with two digits (still retaining the same value)
     */
    public String convertToSingleDigit(int input) {
        String fixed;
        if (Integer.toString(input).length() == 1) {
            fixed = "0" + input;
        }
        else {
            fixed = Integer.toString(input);
        }
        return fixed;
    }

    /**
     * Save the data gathered by the user and uploads it to the database
     */
    private void saveEventData() {
        // get text inputs
        String title = this.title.getText().toString();
        String participationLimit = this.participationLimit.getText().toString();
        String cost = this.cost.getText().toString();
        String description = this.description.getText().toString();
        String location = this.location.getText().toString();

        // combine the date values into 3 formatted date strings
        String dateString = dateMonth.getYear() + "-" + convertToSingleDigit(dateMonth.getMonth()) + "-" + convertToSingleDigit(dateMonth.getDayOfMonth()) + "-" + convertToSingleDigit(dateTime.getHour()) + "-" + convertToSingleDigit(dateTime.getMinute());
        String closeDateString = closeDateMonth.getYear() + "-" + convertToSingleDigit(closeDateMonth.getMonth()) + "-" + convertToSingleDigit(closeDateMonth.getDayOfMonth()) + "-" + convertToSingleDigit(closeDateTime.getHour()) + "-" + convertToSingleDigit(closeDateTime.getMinute());
        String openDateString = openDateMonth.getYear() + "-" + convertToSingleDigit(openDateMonth.getMonth()) + "-" + convertToSingleDigit(openDateMonth.getDayOfMonth()) + "-" + convertToSingleDigit(openDateTime.getHour()) + "-" + convertToSingleDigit(openDateTime.getMinute());


        // initialize variables needed for conversion to Timestamps
        Date date = null;
        Date openDate = null;
        Date closeDate = null;
        Timestamp stampDate = null;
        Timestamp stampOpenDate = null;
        Timestamp stampCloseDate = null;

        // parse the date times into date objects then convert to timestamps
        try {
            date = format.parse(dateString);
            long dateConverted = date.getTime()/1000; // epoch time is in ms so divide by 1000 to get s
            stampDate = new Timestamp(dateConverted, 0);

            openDate = format.parse(openDateString);
            long openDateConverted = openDate.getTime()/1000;
            stampOpenDate = new Timestamp(openDateConverted, 0);

            closeDate = format.parse(closeDateString);
            long closeDateConverted = closeDate.getTime()/1000;
            stampCloseDate = new Timestamp(closeDateConverted, 0);

        } catch (ParseException e) { // would only occur if the dates are in an improper format
            Toast.makeText(this, "Please fill in all required information", Toast.LENGTH_SHORT).show();
            return;
        }

        // make sure all the required fields have been filled out by the user
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(location) || TextUtils.isEmpty(dateMonthButton.getText()) ||
                TextUtils.isEmpty(dateTimeButton.getText()) || TextUtils.isEmpty(openDateMonthButton.getText()) ||
                TextUtils.isEmpty(openDateTimeButton.getText()) || TextUtils.isEmpty(closeDateMonthButton.getText()) || TextUtils.isEmpty(closeDateTimeButton.getText())) {
            Toast.makeText(this, "Please fill in all required information", Toast.LENGTH_SHORT).show();
            return;
        }

        //get organizer id
        String organizerID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // store values to a hash map
        HashMap<String, Object> eventData = new HashMap<>();
        HashMap<String, Object> eventWaitList = new HashMap<>();
        eventData.put("close", stampCloseDate);
        eventData.put("date", stampDate);
        eventData.put("description", description);
        eventData.put("image", "");
        eventData.put("location", location);
        eventData.put("name", title);
        eventData.put("open", stampOpenDate);
        eventData.put("qrcode", "");
        eventData.put("geolocation", geolocationStatus);
        eventData.put("availability", participationLimit); // titled "availability" in db
        eventData.put("price", cost); // titled "price" in db
        eventData.put("organizerID", organizerID);

        eventData.put("registrants", new ArrayList<>());
        eventData.put("selected", new ArrayList<>());
        eventData.put("accepted", new ArrayList<>());
        eventData.put("cancelled", new ArrayList<>());

        eventData.put("admin", false); // admin false by default

        // store the values in the database
        db.collection("events").add(eventData)
                .addOnSuccessListener(documentReference -> {
                    String eventID = documentReference.getId();

                    // generate QR code for the event and store it
                    QRCodeGenerator generator;
                    String qrCode;
                    generator = new QRCodeGenerator(eventID);
                    qrCode = generator.generate();
                    DocumentReference eventRef = db.collection("events").document(eventID);
                    eventRef
                            .update("qrcode", qrCode)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("AddEventFragment", "Error updating", e);
                                }
                            });
                    Intent completedIntent = new Intent();
                    completedIntent.putExtra("eventID", eventID);
                    setResult(RESULT_OK, completedIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save event data", Toast.LENGTH_SHORT).show();
                });
    }
}
