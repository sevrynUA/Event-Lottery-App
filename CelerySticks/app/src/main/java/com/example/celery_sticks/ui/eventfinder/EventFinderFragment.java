package com.example.celery_sticks.ui.eventfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.example.celery_sticks.databinding.FragmentEventFinderBinding;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * This is a fragment that provides a UI for scanning QR codes using the device camera.
 * It manages camera permissions and initializes the barcode scanner.
 */
public class EventFinderFragment extends Fragment {

    private FragmentEventFinderBinding binding;
    private DecoratedBarcodeView barcodeScannerView;

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
     * Initializes the fragment view and sets up the QR code scanner.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * The root view of the fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventFinderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
                if (qrData != null) {
                    Toast.makeText(getContext(), "QR Code: " + qrData, Toast.LENGTH_SHORT).show();
                    // Pause the scanner to prevent further detections
                    barcodeScannerView.pause();
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