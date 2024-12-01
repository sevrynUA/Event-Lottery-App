package com.example.celery_sticks.ui.myprofile;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.celery_sticks.R;
import com.example.celery_sticks.databinding.FragmentMyProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;


/**
 * Represents the MyProfile activity, used to change information about the user
 */
public class MyProfileFragment extends Fragment {

    private FragmentMyProfileBinding binding;
    private MyProfileViewModel myProfileViewModel;
    private String encodedUserImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    /**
     * Initializes the view and sets up the layout and database connection.
     * @param view the view
     *  @param savedInstanceState the saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        encodedUserImage = "";

        // get device ID
        @SuppressLint("HardwareIds") String userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        myProfileViewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);
        // populate fields with user data
        myProfileViewModel.getUser(userID).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.editFirstName.setText(user.getFirstName());
                binding.editLastName.setText(user.getLastName());
                binding.editEmail.setText(user.getEmail());
                encodedUserImage = user.getEncodedImage();
                loadUserImage(encodedUserImage);

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

        binding.deleteUserImageButton.setOnClickListener(v ->  {
            encodedUserImage = "";
            loadUserImage(encodedUserImage);
        });

        binding.uploadUserImageButton.setOnClickListener(v -> {
            getPicture();
        });
    }

    /**
     * encodes image to a base 64 string
     */
    private String encodeImage(Uri imageUri) {

        try {
            InputStream stream = getContext().getContentResolver().openInputStream(imageUri);

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
     * @param path to image
     */
    private void loadUserImage(String path) {
        byte[] decodedImage = Base64.decode(encodedUserImage, Base64.DEFAULT);

        Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        // set qrImage to decoded bitmap
        ImageView userImage = binding.userImageProfileScreen;
        userImage.setImageBitmap(qrBitmap);
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
            encodedUserImage = encodeImage(userImageUri);
            loadUserImage(encodedUserImage);


        }
    }

    /**
     * updates the profile image in the nav sidebar
     * @param userID is user to get the profile image from
     */
    public void updateUserImageNav(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("users").document(userID);

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    decodeImageNav(document.getString("encodedImage"));
                }
            }
        });
    }

    /**
     * decodes the image data into a usable asset
     * @param imageData data to decode into an image
     */
    private void decodeImageNav(String imageData) {
        System.out.println(imageData);
        ImageView image = requireActivity().findViewById(R.id.nav_profile_image);
        MaterialCardView rounder = requireActivity().findViewById(R.id.image_rounder_nav_profile);

        if (imageData != null) {
            if (!imageData.equals("")) {
                rounder.setVisibility(View.VISIBLE);
                byte[] decodedImage = Base64.decode(imageData, Base64.DEFAULT);

                Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                // set qrImage to decoded bitmap
                image.setImageBitmap(qrBitmap);
            }
            else {
                rounder.setVisibility(View.INVISIBLE);
            }
        }
    }


    /**
     * Validates input given by the user
     * @param firstName is the firstName provided by user
     * @param lastName is the lastName provided by user
     * @param email is the email address provided by user
     * @param phoneNumber is the phone number (if any) provided by the user
     * @returns false if invalid input, or true if input is valid
     */
    private boolean inputValidation(String firstName, String lastName, String email, String phoneNumber) {
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(lastName)) {
            return false;
        } else if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*")) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // From https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext by user1737884, Downloaded 2024-11-04
            return false;
        } else if (!phoneNumber.matches("\\d{10}") && !TextUtils.isEmpty(phoneNumber)) {
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
        userData.put("encodedImage", encodedUserImage);
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
                    updateUserImageNav(userID);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                    Log.e("MyProfileFragment", "Profile update failed for userID (max image size is 1 Mb): " + userID, e);
                });
    }

    /**
     * Function runs when activity is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}