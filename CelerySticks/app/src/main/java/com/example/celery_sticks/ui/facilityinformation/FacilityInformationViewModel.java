package com.example.celery_sticks.ui.facilityinformation;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.celery_sticks.Facility;
import com.example.celery_sticks.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/**
 * Represents the FacilityInformation activity; mostly default AndroidStudio implementation from sidebar
 */
public class FacilityInformationViewModel extends ViewModel {

    private final MutableLiveData<Facility> facility;
    private FirebaseFirestore db;

    public FacilityInformationViewModel() {
        facility = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
    }

    private void loadFacilityData(String ownerID) {
        db.collection("facilities").document(ownerID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String facilityName = document.getString("facilityName");
                            String email = document.getString("email");
                            String phoneNumber = document.getString("phoneNumber");

                            if (!TextUtils.isEmpty(phoneNumber)) {
                                facility.setValue(new Facility(facilityName, email, phoneNumber, ownerID));
                            } else {
                                facility.setValue(new Facility(facilityName, email, ownerID));
                            }
                        }
                    }
                });
    }

    public Task<Void> updateFacilityData(String ownerID, Map<String, Object> updatedData) {
        return db.collection("facilities").document(ownerID).update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    refreshFacilityData(ownerID);
                });
    }

    public Task<Void> createFacilityData(String ownerID, Map<String, Object> newData) {
        return db.collection("facilities").document(ownerID).set(newData)
                .addOnSuccessListener(aVoid -> {
                    loadFacilityData(ownerID);
                    db.collection("users").document(ownerID)
                            .update("role", "organizer")
                            .addOnSuccessListener(aVoidUser -> {
                                Log.d("FacilityInformationViewModel", "User role updated to organizer");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FacilityInformationViewModel", "Failed to update user role", e);
                            });
                });
    }

    public void refreshFacilityData(String ownerID) { loadFacilityData(ownerID); }

    public LiveData<Facility> getFacility(String ownerID) {
        if (facility.getValue() == null) {
            loadFacilityData(ownerID);
        }
        return facility;
    }
}