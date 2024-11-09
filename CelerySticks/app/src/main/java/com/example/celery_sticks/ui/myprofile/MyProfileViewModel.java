package com.example.celery_sticks.ui.myprofile;

import android.os.Build;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.celery_sticks.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * Represents the ViewModel for the MyProfile activity, opened through the sidebar
 */
public class MyProfileViewModel extends ViewModel {

    private final MutableLiveData<User> user;
    private FirebaseFirestore db;

    /**
     * Initializes the view model for the user's profile.
     */
    public MyProfileViewModel() {
        user = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Loads user data from the database.
     * @param userID the user's ID
     */
    private void loadUserData(String userID) {
        db.collection("users").document(userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String email = document.getString("email");
                            String phoneNumber = document.getString("phoneNumber");
                            String role = document.getString("role");
                            String encodedImage = document.getString("encodedImage");

                            if (!TextUtils.isEmpty(phoneNumber)) {
                                User buffer = new User(firstName, lastName, email, phoneNumber, role, userID);
                                buffer.setEncodedImage(encodedImage);
                                user.setValue(buffer);
                            } else {
                                User buffer = new User(firstName, lastName, email, role, userID);
                                buffer.setEncodedImage(encodedImage);
                                user.setValue(buffer);
                            }
                        }
                    }
                });
    }

    /**
     * Updates user data in the database.
     * @param userID the user's ID
     * @param updatedData the updated user data
     * @return the task to update the user data
     */
    public Task<Void> updateUserData(String userID, Map<String, Object> updatedData) {
        return db.collection("users").document(userID).update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    refreshUserData(userID);
                });
    }

    /**
     * Refreshes the user data so LiveData shows changes.
     * @param userID the user's ID
     */
    public void refreshUserData(String userID) {
        loadUserData(userID);
    }

    /**
     * Gets the user data from the database.
     * @param userID the user's ID
     * @return the user data
     */
    public LiveData<User> getUser(String userID) {
        if (user.getValue() == null) {
            loadUserData(userID);
        }
        return user;
    }


}