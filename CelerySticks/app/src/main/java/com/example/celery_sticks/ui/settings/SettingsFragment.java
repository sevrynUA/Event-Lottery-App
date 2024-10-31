package com.example.celery_sticks.ui.settings;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.celery_sticks.R;
import com.example.celery_sticks.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView text = binding.settingsNotificationsText;
        Button notifyWin = binding.settingsNotifyWinButton;
        Button notifyLose = binding.settingsNotifyLoseButton;
        Button notify = binding.settingsNotifyButton;

        boolean[] getNotification = {true,false,false};
;
        notifyWin.setOnClickListener(view ->{
            if (getNotification[0]) {
                getNotification[0] = false;
                notifyWin.setBackgroundColor(getResources().getColor(R.color.unSelectedRed));
                notifyWin.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x_mark, 0);
            } else{
                getNotification[0] = true;
                notifyWin.setBackgroundColor(getResources().getColor(R.color.vomitGreen));
                notifyWin.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmark, 0);
            }
        });

        notifyLose.setOnClickListener(view ->{
            if (getNotification[1]) {
                getNotification[1] = false;
                notifyLose.setBackgroundColor(getResources().getColor(R.color.unSelectedRed));
                notifyLose.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x_mark, 0);
            } else{
                getNotification[1] = true;
                notifyLose.setBackgroundColor(getResources().getColor(R.color.vomitGreen));
                notifyLose.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmark, 0);
            }
        });

        notify.setOnClickListener(view ->{
            if (getNotification[2]) {
                getNotification[2] = false;
                notify.setBackgroundColor(getResources().getColor(R.color.unSelectedRed));
                notify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x_mark, 0);
            } else{
                getNotification[2] = true;
                notify.setBackgroundColor(getResources().getColor(R.color.vomitGreen));
                notify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmark, 0);
            }
        });

        //final TextView textView = binding.textSettings;
        //settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}