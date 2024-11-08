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
 * This is the class for the model which manages all the views for the user
 */
public class FacilityInformationViewModel extends ViewModel {

    private final MutableLiveData<Facility> facility;
    private FirebaseFirestore db;

    /**
     * constructor for the class
     */
    public FacilityInformationViewModel() {
        facility = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Loads the facility information given the ID of the owner
     * @param ownerID ID of the facility owner
     */
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

    /**
     * Updates the facility document in the database and refreshes the model
     * @param ownerID ID of the facility owner
     * @param updatedData data to update the document with
     * @return the firestore firebase collection
     */
    public Task<Void> updateFacilityData(String ownerID, Map<String, Object> updatedData) {
        return db.collection("facilities").document(ownerID).update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    refreshFacilityData(ownerID);
                });
    }

    /**
     *
     * @param ownerID
     * @param newData
     * @return
     */
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