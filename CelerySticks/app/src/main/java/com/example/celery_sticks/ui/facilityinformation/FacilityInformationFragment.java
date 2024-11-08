package com.example.celery_sticks.ui.facilityinformation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.celery_sticks.databinding.FragmentFacilityInformationBinding;
import com.example.celery_sticks.ui.facilityinformation.FacilityInformationViewModel;


import java.util.HashMap;


public class FacilityInformationFragment extends Fragment {

    private FragmentFacilityInformationBinding binding;
    private FacilityInformationViewModel facilityInformationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFacilityInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get device ID to match with facility owner ID
        @SuppressLint("HardwareIds") String ownerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        facilityInformationViewModel = new ViewModelProvider(this).get(FacilityInformationViewModel.class);

        // populate fields with facility data
        facilityInformationViewModel.getFacility(ownerID).observe(getViewLifecycleOwner(), facility -> {
            if (facility != null) {
                binding.editFacilityName.setText(facility.getFacilityName());
                binding.editEmail.setText(facility.getFacilityEmail());
                binding.editPhoneNumber.setText(facility.getFacilityPhoneNumber());
                binding.facilityButton.setText("Save Changes");
                if (!TextUtils.isEmpty(facility.getFacilityPhoneNumber())) {
                    binding.editPhoneNumber.setText(facility.getFacilityPhoneNumber());
                }
            } else {
                Toast.makeText(getContext(), "Create a facility profile to organize events", Toast.LENGTH_SHORT).show();
            }
        });
        binding.facilityButton.setOnClickListener(v -> saveFacilityChanges(ownerID));
    }

    private boolean inputValidation(String facilityName, String email, String phoneNumber) {
        if (TextUtils.isEmpty(facilityName) || TextUtils.isEmpty(email)) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // From https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext by user1737884, Downloaded 2024-11-04
            return false;
        } else if (!phoneNumber.matches("\\d{10}") && !TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        return true;
    }

    private void saveFacilityChanges(String ownerID) {
        String facilityName = binding.editFacilityName.getText().toString();
        String email = binding.editEmail.getText().toString();
        String phoneNumber = binding.editPhoneNumber.getText().toString();

        if (!inputValidation(facilityName, email, phoneNumber)) {
            Toast.makeText(getContext(), "Valid name and email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", facilityName);
        facilityData.put("email", email);
        facilityData.put("phoneNumber", phoneNumber);
        if (TextUtils.isEmpty(phoneNumber)) {
            facilityData.remove("phoneNumber");
        }

        if (facilityInformationViewModel.getFacility(ownerID).getValue() == null) {
            // Create new facility if it doesn't exist
            facilityInformationViewModel.createFacilityData(ownerID, facilityData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Facility created", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to create facility", Toast.LENGTH_SHORT).show();
                        Log.e("FacilityInformationFragment", "Error creating facility", e);
                    });
        } else {
            // Update existing facility
            facilityInformationViewModel.updateFacilityData(ownerID, facilityData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Facility updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update facility", Toast.LENGTH_SHORT).show();
                        Log.e("FacilityInformationFragment", "Error updating facility", e);
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}