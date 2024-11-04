package com.example.celery_sticks.ui.myprofile;

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

import com.example.celery_sticks.R;
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

                String initials = "";
                initials += user.getFirstName().charAt(0);
                initials += user.getLastName().charAt(0);
                TextView text_user_first_name = requireActivity().findViewById(R.id.text_user_first_name);
                text_user_first_name.setText(user.getFirstName());
                binding.iconInitials.setText(initials.toUpperCase());

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

    private boolean inputValidation(String firstName, String lastName, String email, String phoneNumber) {
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(lastName)) {
            return false;
        } else if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*")) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // From https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext by user1737884, Downloaded 2024-11-04
            return false;
        } else if (!phoneNumber.matches("\\d{10}")) {
            return false;
        }
        return true;
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

        if (!inputValidation(firstName, lastName, email, phoneNumber)) {
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

                    // Update sidebar details as well
                    TextView text_user_first_name = requireActivity().findViewById(R.id.text_user_first_name);
                    TextView sidebar_icon_initials = requireActivity().findViewById(R.id.sidebar_icon_initials);
                    String initials = "";
                    initials += firstName.charAt(0);
                    initials += lastName.charAt(0);
                    text_user_first_name.setText(firstName);
                    sidebar_icon_initials.setText(initials.toUpperCase());
                    binding.iconInitials.setText(initials.toUpperCase());
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