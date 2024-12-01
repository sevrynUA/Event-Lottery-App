package com.example.celery_sticks.ui.browseimages;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.celery_sticks.ui.myevents.EventDetailsViewModel;
import com.example.celery_sticks.ui.myevents.EventQRCodeView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.celery_sticks.R;
import com.example.celery_sticks.databinding.FragmentAdminBrowseImagesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Represents the BrowseImages page of the app for admins only
 */
public class BrowseImagesFragment extends Fragment {

    private FragmentAdminBrowseImagesBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String[]> images = new ArrayList<>();
    private ListView imagesListView;
    private ImageArrayAdapter adapter;
    private ActivityResultLauncher<Intent> launcher;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminBrowseImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        @SuppressLint("HardwareIds") String userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                refreshList();
                updateUserImageNav(userID);
            }
        });

        imagesListView = root.findViewById(R.id.admin_image_browse_list_view);
        adapter = new ImageArrayAdapter(getContext(), images);
        imagesListView.setAdapter(adapter);


        // get all images from users
        CollectionReference users = db.collection("users");
        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        String title = "Origin: " + document.getString("firstName") + " " + document.getString("lastName");
                        String userID = document.getId();
                        String userImage = document.getString("encodedImage");

                        if (!userImage.equals("")) {
                            String[] entry = {"Type: user profile", title, userID, userImage};
                            images.add(entry);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // get all images from events
        CollectionReference events = db.collection("events");
        events.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        String title = "Origin: " + document.getString("name");
                        String eventID = document.getId();
                        String eventImage = document.getString("image");

                        if (!eventImage.equals("")) {
                            String[] entry = {"Type: event poster", title, eventID, eventImage};
                            images.add(entry);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        // when an item is clicked
        imagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent imageView;
                imageView = new Intent(getContext(), BrowseImageViewFragment.class);

                String[] imageChosen = (String[]) imagesListView.getItemAtPosition(i);

                imageView.putExtra("type", imageChosen[0]);
                imageView.putExtra("title", imageChosen[1]);
                imageView.putExtra("id", imageChosen[2]);

                launcher.launch(imageView);

            }
        });

        return root;
    }



    /**
     * Refreshes UI by calling .notifyDataSetChanged() on ArrayList
     */
    public void refreshList() {
        images.clear();

        CollectionReference users = db.collection("users");
        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        String title = "Origin: " + document.getString("firstName") + " " + document.getString("lastName");
                        String userID = document.getId();
                        String userImage = document.getString("encodedImage");

                        if (!userImage.equals("")) {
                            String[] entry = {"Type: user profile", title, userID, userImage};
                            images.add(entry);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // get all images from events
        CollectionReference events = db.collection("events");
        events.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        String title = "Origin: " + document.getString("name");
                        String eventID = document.getId();
                        String eventImage = document.getString("image");

                        if (!eventImage.equals("")) {
                            String[] entry = {"Type: event poster", title, eventID, eventImage};
                            images.add(entry);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    /**
     * Updates the profile image in the nav sidebar
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
     * Decodes the image data into a usable asset
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
            } else {
                rounder.setVisibility(View.INVISIBLE);
            }
        }
    }

}