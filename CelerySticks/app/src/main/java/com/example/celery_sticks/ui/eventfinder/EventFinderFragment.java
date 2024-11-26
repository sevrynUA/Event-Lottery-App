package com.example.celery_sticks.ui.eventfinder;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.celery_sticks.MainActivity;
import com.example.celery_sticks.databinding.FragmentEventFinderBinding;
import com.example.celery_sticks.ui.myevents.EventDetailsViewModel;
import com.example.celery_sticks.ui.myevents.MyEventsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a fragment that provides a UI for scanning QR codes using the device camera.
 * It manages camera permissions and initializes the barcode scanner.
 */
public class EventFinderFragment extends Fragment {

    private FragmentEventFinderBinding binding;
    private DecoratedBarcodeView barcodeScannerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<Intent> eventDetailsLauncher;
    private String userID;
    private String category;

    /**
     * Handles the result of the camera permission request.
     * If granted, it starts the QR code scanner, otherwise it displays a warning message.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startScanner();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * This function will return the values associated with the specified event given an event ID (also passes these values to the viewModel)
     * @param eventID the id of the event to retrieve information about
     * @return A hashmap which contains all the event's details
     */
    public Map<String, Object> getEventData(String eventID) {
        DocumentReference ref = db.collection("events").document(eventID);
        Map<String, Object> eventData = new HashMap<>();
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        ArrayList<String> cancelled = (ArrayList<String>) document.get("cancelled");
                        ArrayList<String> accepted = (ArrayList<String>) document.get("accepted");
                        ArrayList<String> selected = (ArrayList<String>) document.get("selected");
                        ArrayList<String> registered = (ArrayList<String>) document.get("registrants");

                        if(cancelled.contains(userID)) {
                            category = "cancelled";
                        } else if(accepted.contains(userID)) {
                            category = "accepted";
                        } else if(registered.contains(userID)) {
                            category = "registered";
                        } else if(selected.contains(userID)) {
                            category = "invitation";
                        } else {
                            category = "notinarray";
                        }




                        // passes values to intent EventDetailsViewModel
                        Intent intent = new Intent(getContext(), EventDetailsViewModel.class);

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
                        eventData.put("eventID", document.getId());

                        // format the dates given Timestamps
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

                        // pass the event details to the model
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
                        intent.putExtra("category", category);
                        intent.putExtra("userID", userID);

                        startActivity(intent);
                    }
                }
            }
        });
        return eventData;
    }
    /**
     * Initializes the fragment view and sets up the QR code scanner.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view to be displayed
     * The root view of the fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventFinderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
        }

        barcodeScannerView = binding.barcodeScanner;
        barcodeScannerView.getStatusView().setVisibility(View.GONE);
        setupScanner();

        EventFinderViewModel viewModel = new ViewModelProvider(this).get(EventFinderViewModel.class);
        final TextView textView = binding.textEventFinder;
        viewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    /**
     * Checks for camera permissions and starts the scanner if granted.
     * If permission is not granted, requests camera permission.
     */
    private void setupScanner() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startScanner();
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Requests camera permission from the user to enable QR code scanning.
     */
    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    /**
     * Starts the QR code scanner.
     * Displays scanned QR code result as a preview Toast message.
     */
    private void startScanner() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String qrData = result.getText();
                if (qrData.length() == 20) {
                    getEventData(qrData);
                    // Pause the scanner to prevent further detections
                    barcodeScannerView.pause();
                }
                else {
                    Toast.makeText(getContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Resumes the barcode scanner when the fragment is visible to the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Resume the barcode scanner
        if (barcodeScannerView != null) {
            barcodeScannerView.resume();
        }
    }

    /**
     * Pauses the barcode scanner when the fragment is no longer visible to the user.
     */
    @Override
    public void onPause() {
        super.onPause();
        // Pause the barcode scanner
        if (barcodeScannerView != null) {
            barcodeScannerView.pause();
        }
    }

    /**
     * Cleans up references when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to prevent memory leaks
        barcodeScannerView = null;
        binding = null;
    }

    // Will need function to handle navigation to QR code details after scanning

}