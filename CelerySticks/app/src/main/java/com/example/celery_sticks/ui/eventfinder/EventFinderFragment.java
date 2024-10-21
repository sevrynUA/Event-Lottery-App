package com.example.celery_sticks.ui.eventfinder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.celery_sticks.databinding.FragmentEventFinderBinding;

public class EventFinderFragment extends Fragment {

    private FragmentEventFinderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventFinderViewModel eventFinderViewModel =
                new ViewModelProvider(this).get(EventFinderViewModel.class);

        binding = FragmentEventFinderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textEventFinder;
        eventFinderViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}