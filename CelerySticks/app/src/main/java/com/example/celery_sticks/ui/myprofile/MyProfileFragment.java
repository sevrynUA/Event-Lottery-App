package com.example.celery_sticks.ui.myprofile;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.celery_sticks.User;
import com.example.celery_sticks.databinding.FragmentMyProfileBinding;

import java.util.HashMap;

// TODO: Input validation, make EditText fields TextView until an edit button is pressed.
public class MyProfileFragment extends Fragment {

    private FragmentMyProfileBinding binding;
    private MyProfileViewModel myProfileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    /**
     * Initializes the view and sets up the layout and database connection.
     * @param view the view
     *  @param savedInstanceState the saved instance state
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get device ID
        String userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        myProfileViewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);
        // populate fields with user data
        myProfileViewModel.getUser(userID).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.editFirstName.setText(user.getFirstName());
                binding.editLastName.setText(user.getLastName());
                binding.editEmail.setText(user.getEmail());

                if (!TextUtils.isEmpty(user.getPhoneNumber())) {
                    binding.editPhoneNumber.setText(user.getPhoneNumber());
                }
            } else {
                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            }
        });
        // save changes to user data
        binding.saveButton.setOnClickListener(v -> saveChanges(userID));
    }

    /**
     * Saves changes to user data
     * @param userID the user ID
     */
    private void saveChanges(String userID) {
        String firstName = binding.editFirstName.getText().toString();
        String lastName = binding.editLastName.getText().toString();
        String email = binding.editEmail.getText().toString();
        String phoneNumber = binding.editPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please fill in all required information", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("email", email);
        if (!TextUtils.isEmpty(phoneNumber)) {
            userData.put("phoneNumber", phoneNumber);
        }

        myProfileViewModel.updateUserData(userID, userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Updated Profile", Toast.LENGTH_SHORT).show();
                    Log.d("MyProfileFragment", "Profile updated successfully for userID: " + userID);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                    Log.e("MyProfileFragment", "Error updating profile", e);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}