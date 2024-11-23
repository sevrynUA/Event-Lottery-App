package com.example.celery_sticks.ui.browseimages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ImageBrowseFragment extends Fragment {

    private FragmentAdminBrowseImagesBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String[]> images = new ArrayList<>();
    private ListView imagesListView;
    private ImageArrayAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminBrowseImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
                        String title = document.getString("firstname") + document.getString("lastname");
                        String userID = document.getId();
                        String userImage = document.getString("encodedImage");

                        if (!userImage.equals("")) {
                            String[] entry = {"user", title, userID, userImage};
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
                        String title = document.getString("name");
                        String eventID = document.getId();
                        String eventImage = document.getString("image");

                        if (!eventImage.equals("")) {
                            String[] entry = {"event", title, eventID, eventImage};
                            images.add(entry);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        return root;

    }
}

