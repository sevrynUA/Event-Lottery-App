package com.example.celery_sticks.ui.browseimages;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.celery_sticks.databinding.FragmentBrowseImagesBinding;
import com.example.celery_sticks.databinding.FragmentBrowseUsersBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class BrowseImagesFragment extends Fragment {
    private FragmentBrowseImagesBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBrowseImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
        }

        //initialize(root);

        return root;
    }
}
