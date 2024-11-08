package com.example.celery_sticks.ui.settings;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.celery_sticks.R;
import com.example.celery_sticks.databinding.FragmentSettingsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Represents the Settings activity, used to change settings for the user
 */
public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference users = db.collection("users");
    private String userID;

    private Boolean notificationToggle = true;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView text = binding.settingsNotificationsText;
        Button notify = binding.settingsNotifyButton;



        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
        }


        users.document(userID).get().addOnSuccessListener(user -> {
            if (user.exists()) {
                if (user.getBoolean("notificationSetting") == true) {
                    setNotifSettingTrue(notify);
                } else {
                    setNotifSettingFalse(notify);
                }
            }
        });

        notify.setOnClickListener(view ->{
            if (notificationToggle) {
                setNotifSettingFalse(notify);
            } else {
                setNotifSettingTrue(notify);
            }
        });

        //final TextView textView = binding.textSettings;
        //settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    /**
     * Toggles notification setting to true
     * @param button the button clicked to toggle setting
     */
    public void setNotifSettingTrue(Button button) {
        notificationToggle = true;
        button.setBackgroundColor(getResources().getColor(R.color.vomitGreen));
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmark, 0);
        users.document(userID).update("notificationSetting", true);
    }

    /**
     * Toggles notification setting to false
     * @param button the button clicked to toggle setting
     */
    public void setNotifSettingFalse(Button button) {
        notificationToggle = false;
        button.setBackgroundColor(getResources().getColor(R.color.unSelectedRed));
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x_mark, 0);
        users.document(userID).update("notificationSetting", false);

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