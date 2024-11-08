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
 * Represents activity for creating events, initiated through the MyEvents activity
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

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

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

        db = FirebaseFirestore.getInstance();

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

        createEventButton.setOnClickListener(view -> {
            saveEventData();
        });

        cancelButton.setOnClickListener(view -> {
            finish();
        });

        // if time buttons are pressed
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
     * Changes UI visibility when date button is clicked (for setting date)
     * @param pressed represents pressed button
     * @param other represents other button
     * @param selected represents the datepicker corresponding to the selected button
     * @param confirm represents the confirm button which will save datepicker selection
     */
    public void monthButtonPressed(Button pressed, Button other, DatePicker selected, Button confirm) {
        pressed.setVisibility(View.GONE);
        other.setVisibility(View.GONE);
        selected.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.VISIBLE);

        confirm.setOnClickListener(view -> {
            String date = selected.getDayOfMonth() + "/" + (selected.getMonth() + 1) + "/" + selected.getYear();
            pressed.setText(date);


            pressed.setVisibility(View.VISIBLE);
            other.setVisibility(View.VISIBLE);
            selected.setVisibility(View.GONE);
            confirm.setVisibility(View.GONE);
        });
    }

    /**
     * Changes UI visibility when time button is clicked (for setting time)
     * @param pressed represents pressed button
     * @param other represents other button
     * @param selected represents the timepicker corresponding to the selected button
     * @param confirm represents the confirm button which will save timepicker selection
     */
    public void timeButtonPressed(Button pressed, Button other, TimePicker selected, Button confirm) {
        pressed.setVisibility(View.GONE);
        other.setVisibility(View.GONE);
        selected.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.VISIBLE);

        confirm.setOnClickListener(view -> {
            String time = selected.getHour() + ":" + selected.getMinute();
            pressed.setText(time);

            pressed.setVisibility(View.VISIBLE);
            other.setVisibility(View.VISIBLE);
            selected.setVisibility(View.GONE);
            confirm.setVisibility(View.GONE);
        });
    }

    /**
     * Ensures date has double digits (with a 0 in front)
     * @param input is the input to ensure has double digits
     * @return fixed input with double digits
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
     * Creates new event in database with data provided by the user
     */
    private void saveEventData() {
        String title = this.title.getText().toString();
        String participationLimit = this.participationLimit.getText().toString();
        String cost = this.cost.getText().toString();
        String description = this.description.getText().toString();
        String location = this.location.getText().toString();
        String dateString = dateMonth.getYear() + "-" + convertToSingleDigit(dateMonth.getMonth()) + "-" + convertToSingleDigit(dateMonth.getDayOfMonth()) + "-" + convertToSingleDigit(dateTime.getHour()) + "-" + convertToSingleDigit(dateTime.getMinute());
        String closeDateString = closeDateMonth.getYear() + "-" + convertToSingleDigit(closeDateMonth.getMonth()) + "-" + convertToSingleDigit(closeDateMonth.getDayOfMonth()) + "-" + convertToSingleDigit(closeDateTime.getHour()) + "-" + convertToSingleDigit(closeDateTime.getMinute());
        String openDateString = openDateMonth.getYear() + "-" + convertToSingleDigit(openDateMonth.getMonth()) + "-" + convertToSingleDigit(openDateMonth.getDayOfMonth()) + "-" + convertToSingleDigit(openDateTime.getHour()) + "-" + convertToSingleDigit(openDateTime.getMinute());


        // convert dates to timestamps
        Date date = null;
        Date openDate = null;
        Date closeDate = null;
        Timestamp stampDate = null;
        Timestamp stampOpenDate = null;
        Timestamp stampCloseDate = null;

        System.out.println(dateString);
        System.out.println(openDateString);
        System.out.println(closeDateString);

        // parse the date times into date objects then convert to timestamps
        try {
            date = format.parse(dateString);
            long dateConverted = date.getTime()/1000;
            stampDate = new Timestamp(dateConverted, 0);

            openDate = format.parse(openDateString);
            long openDateConverted = openDate.getTime()/1000;
            stampOpenDate = new Timestamp(openDateConverted, 0);

            closeDate = format.parse(closeDateString);
            long closeDateConverted = closeDate.getTime()/1000;
            stampCloseDate = new Timestamp(closeDateConverted, 0);

        } catch (ParseException e) {
            Toast.makeText(this, "Please fill in all required information", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(location) || TextUtils.isEmpty(dateMonthButton.getText()) ||
                TextUtils.isEmpty(dateTimeButton.getText()) || TextUtils.isEmpty(openDateMonthButton.getText()) ||
                TextUtils.isEmpty(openDateTimeButton.getText()) || TextUtils.isEmpty(closeDateMonthButton.getText()) || TextUtils.isEmpty(closeDateTimeButton.getText())) {
            Toast.makeText(this, "Please fill in all required information", Toast.LENGTH_SHORT).show();
            return;
        }


        //get organizer id
        String organizerID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        HashMap<String, Object> eventData = new HashMap<>();
        HashMap<String, Object> eventWaitList = new HashMap<>();
        //eventData.put("accepted", false);
        eventData.put("close", stampCloseDate);
        eventData.put("date", stampDate);
        eventData.put("description", description);
        eventData.put("image", "");
        eventData.put("location", location);
        eventData.put("name", title);
        eventData.put("open", stampOpenDate);
        eventData.put("qrcode", "");
        //eventData.put("registered", false);
        eventData.put("geolocation", geolocationStatus);
        eventData.put("availability", participationLimit); // titled "availability" in db
        eventData.put("price", cost); // titled "price" in db
        eventData.put("organizerID", organizerID);

        eventData.put("registrants", new ArrayList<>());
        eventData.put("selected", new ArrayList<>());
        eventData.put("accepted", new ArrayList<>());
        eventData.put("cancelled", new ArrayList<>());

        db.collection("events").add(eventData)
                .addOnSuccessListener(documentReference -> {
                    String eventID = documentReference.getId();
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
