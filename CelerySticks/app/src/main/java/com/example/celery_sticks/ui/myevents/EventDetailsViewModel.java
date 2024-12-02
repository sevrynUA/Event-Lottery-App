package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.celery_sticks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents the event details activity displayed by clicking on events in the MyEvents menu
 */
public class EventDetailsViewModel extends AppCompatActivity implements GeolocationWarningFragment.GeolocationDialogueListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String userID = null;
    public String eventID = null;

    public Boolean geolocation = false;
    private String encodedEventImage;
    private ImageView eventImage;
    private final MutableLiveData<String> loadedImageData = new MutableLiveData<>();

    public Boolean invitationDecisionDebounce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        eventID = intent.getStringExtra("eventID");


        Button uploadButton = findViewById(R.id.upload_event_image_button);
        Button deleteButton = findViewById(R.id.delete_event_image_button);
        FrameLayout imageButtons = findViewById(R.id.image_buttons_event_details);
        LinearLayout invitationLinearLayout = findViewById(R.id.invitation_linear_layout);
        TextView invitationDecidedText = findViewById(R.id.invitation_decided_text);

        Button registerButton = findViewById(R.id.register_button);
        Button manageEntrantsButton = findViewById(R.id.manage_entrants_button);
        Button acceptInvitationButton = findViewById(R.id.accept_invitation_button);
        Button declineInvitationButton = findViewById(R.id.decline_invitation_button);
        Button deleteEventButton = findViewById(R.id.delete_event_button);
        String eventCategory = intent.getStringExtra("category");

        deleteEventButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);
        invitationLinearLayout.setVisibility(View.GONE);
        manageEntrantsButton.setVisibility(View.GONE);
        imageButtons.setVisibility(View.GONE);
        invitationDecidedText.setVisibility(View.GONE);

        if (Objects.equals(eventCategory, "admin")) {
            deleteEventButton.setVisibility(View.VISIBLE);
        } else if (Objects.equals(eventCategory, "created")) {
            imageButtons.setVisibility(View.VISIBLE);
            manageEntrantsButton.setVisibility(View.VISIBLE);
        } else if (Objects.equals(eventCategory, "registered")) {
            registerButton.setVisibility(View.VISIBLE);
            registerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.unSelectedRed)));
            registerButton.setText("Unregister");
        } else if (Objects.equals(eventCategory, "invitation")) {
            invitationLinearLayout.setVisibility(View.VISIBLE);
        } else if (Objects.equals(eventCategory, "accepted")) {
            invitationDecidedText.setVisibility(View.VISIBLE);
            invitationDecidedText.setText("You have accepted an invitation for this event.");
        } else if (Objects.equals(eventCategory, "cancelled")) {
            invitationDecidedText.setVisibility(View.VISIBLE);
            invitationDecidedText.setText("You have declined an invitation for this event.");
        } else {
            registerButton.setVisibility(View.VISIBLE);
            registerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.vomitGreen)));
            registerButton.setText("Register");
        }



        TextView eventTitleText = findViewById(R.id.event_title_text);
        TextView eventDescriptionText = findViewById(R.id.event_description_text);
        TextView eventTimeText = findViewById(R.id.event_time_text);
        TextView eventLocationText = findViewById(R.id.event_location_text);
        TextView eventAvailabilityText = findViewById(R.id.event_availability_text);
        TextView eventPriceText = findViewById(R.id.event_price_text);
        eventImage = findViewById(R.id.event_image_view);

        eventTitleText.setText(intent.getStringExtra("name"));
        eventDescriptionText.setText(intent.getStringExtra("description"));
        eventTimeText.setText(intent.getStringExtra("date"));
        eventLocationText.setText(intent.getStringExtra("location"));

        //System.out.println(intent.getStringExtra("image"));


        if (intent.getStringExtra("availability") == null || Objects.equals(intent.getStringExtra("availability"), "")) {
            eventAvailabilityText.setText("Availability - No Limit");
        } else {
            eventAvailabilityText.setText(String.format("Availability - %s", intent.getStringExtra("availability")));
        }
        if (intent.getStringExtra("price") == null || Objects.equals(intent.getStringExtra("price"), "")) {
            eventPriceText.setText("Free");
        } else {
            eventPriceText.setText(intent.getStringExtra("price"));
        }


        // change event_image_view to the image passed with intent.getStringExtra("image") here
        getEventImageData(eventID);



        Button backButton = findViewById(R.id.event_details_back);
        backButton.setOnClickListener(view -> {
            Intent completedIntent = new Intent();
            setResult(RESULT_OK, completedIntent);
            finish();
        });

        Button qrButton = findViewById(R.id.button3);
        qrButton.setOnClickListener(view -> {
            Intent qrView = new Intent(EventDetailsViewModel.this, EventQRCodeView.class);
            qrView.putExtra("eventID", eventID);
            qrView.putExtra("category", eventCategory);
            startActivity(qrView);
        });

        registerButton.setOnClickListener(view -> {
            checkIfUserRegistered(isUserRegistered -> {
                if (isUserRegistered) { // user is unregistering
                    unregister();
                } else { // user is registering
                    if (geolocation) {
                        new GeolocationWarningFragment().show(getSupportFragmentManager(), "Warning");
                    } else {
                        register();
                    }
                }
            });
        });

        manageEntrantsButton.setOnClickListener(view -> {
            Intent manageEntrantsIntent = new Intent(EventDetailsViewModel.this, ManageEntrantsFragment.class);
            manageEntrantsIntent.putExtra("eventID", eventID);
            manageEntrantsIntent.putExtra("availability", intent.getStringExtra("availability"));
            startActivity(manageEntrantsIntent);
        });

        deleteEventButton.setOnClickListener(view -> {
            db.collection("events").document(eventID).delete()
                    .addOnSuccessListener(success -> {
                        Intent completedIntent = new Intent();
                        setResult(RESULT_OK, completedIntent);
                        finish();
                    });
        });


        uploadButton.setOnClickListener(view -> {
            getPicture();
        });

        deleteButton.setOnClickListener(view -> {
            encodedEventImage = "";
            updateEventImage();
        });

        acceptInvitationButton.setOnClickListener(view -> {
            inviteDecision("accepted");
        });

        declineInvitationButton.setOnClickListener(view -> {
            inviteDecision("cancelled");
        });


    }

    /**
     * Handles a user clicking on "Accept" or "Decline" for their invitation
     * @param decisionArray holds whether they accepted or declined
     */
    private void inviteDecision(String decisionArray) {
        if (invitationDecisionDebounce == false) {
            invitationDecisionDebounce = true;
            db.collection("events").document(eventID).update(
                    "selected", FieldValue.arrayRemove(userID),
                    decisionArray, FieldValue.arrayUnion(userID))
                    .addOnSuccessListener(success -> {
                        if (decisionArray.equals("accepted")) {
                            Toast.makeText(this, "Invitation Accepted!", Toast.LENGTH_SHORT).show();
                        } else if (decisionArray.equals("cancelled")) {
                            Toast.makeText(this, "Invitation Declined!", Toast.LENGTH_SHORT).show();
                        }
                        Intent completedIntent = new Intent();
                        setResult(RESULT_OK, completedIntent);
                        finish();
                    });
        }
    }

    /**
     * encodes image to a base 64 string
     */
    private String encodeImage(Uri imageUri) {

        try {
            InputStream stream = getApplicationContext().getContentResolver().openInputStream(imageUri);

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = stream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            stream.close();
            byteBuffer.close();

            String image = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
            return image;


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * decodes image to a base 64 string
     */
    private void loadEventImage(String data) {
        if (data != null) {
            if (data.equals("")) {
                Drawable image = getResources().getDrawable(R.drawable.landscape_event_placeholder_image, getTheme());
                eventImage.setImageDrawable(image);
            }
            else {
                byte[] decodedImage = Base64.decode(data, Base64.DEFAULT);

                Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                // set qrImage to decoded bitmap
                eventImage.setImageBitmap(qrBitmap);
            }
        }
    }

    /**
     * User popup to access their gallery
     */
    private void getPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    /**
     * Once the user has selected their image, this function invokes to set the attributes and update the image view
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            Uri userImageUri = data.getData();
            encodedEventImage = encodeImage(userImageUri);
            updateEventImage();
        }
    }


    /**
     * Interface used for asynchronously accessing data for event details
     */
    public interface EventDetailsCallback {
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
     * Registers the user in the current event
     */
    public void register() {
        db.collection("events").document(eventID)
                .update("registrants", FieldValue.arrayUnion(userID))
                .addOnSuccessListener(success -> {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                });
        Intent completedIntent = new Intent();
        setResult(RESULT_OK, completedIntent);
        finish();
    }

    /**
     * updates the image in the database
     */
    public void updateEventImage() {
        db.collection("events").document(eventID)
                .update("image", encodedEventImage)
                .addOnSuccessListener(success -> {
                    Toast.makeText(this, "upload successful", Toast.LENGTH_SHORT).show();
                });
        loadEventImage(encodedEventImage);
    }

    /**
     * gets the event image from the database
     */
    public void getEventImageData(String eventID) {
        DocumentReference ref = db.collection("events").document(eventID);

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    loadEventImage(document.getString("image"));
                }
            }
        });
    }



    /**
     * Unregisters the user from the current event
     */
    public void unregister() {
        db.collection("events").document(eventID)
                .update("registrants", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(success -> {
                    Toast.makeText(this, "Unregistration Successful!", Toast.LENGTH_SHORT).show();
                });
        Intent completedIntent = new Intent() ;
        setResult(RESULT_OK, completedIntent);
        finish();
    }

    /**
     * Checks if user is already registered in the current event
     * @param callback is used to get result of asynchronous data access
     */
    public Boolean checkIfUserRegistered(RegistrationWaitCallback callback) {
        getRegistrants(eventID, new EventDetailsCallback() {
            @Override
            public void onDataRecieved(ArrayList<String> eventData) {
                Boolean isUserRegistered = eventData.contains(userID);
                callback.onDataReturned(isUserRegistered);
            }
        });
        return null;
    }


    /**
     * Gets the userIDs of the entrants that have registered in the current event
     * @param eventID is the ID of the current event
     * @param callback is used to get results of asynchronous data access
     * @return an ArrayList containing userIDs of the entrants registered in the current event
     */
    public ArrayList<String> getRegistrants(String eventID, EventDetailsCallback callback) {
        DocumentReference ref = db.collection("events").document(eventID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> registrants = null;
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        geolocation = (Boolean) document.getBoolean("geolocation"); // get geolocation while checking db
                        registrants = (ArrayList<String>) document.get("registrants");
                    }
                    callback.onDataRecieved(registrants);
                }
            }
        });
        return null;
    }
}
