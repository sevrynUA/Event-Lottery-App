package com.example.celery_sticks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.databinding.ActivityMainBinding;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * MainActivity initializes main features / setup for the app
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private String userID;

    private ActivityResultLauncher<Intent> startUpActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database
        db = FirebaseFirestore.getInstance();

        // Get device ID
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Check if user exists in database
        checkUser();

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.my_events, R.id.event_finder, R.id.my_profile, R.id.settings, R.id.facility_information)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // explicitly pass userID to MyEventsFragment on app startup
        Bundle explicitBundle = new Bundle();
        explicitBundle.putString("userID", userID);
        navController.navigate(R.id.my_events, explicitBundle);

        // Use to pass string value to other Activities in sidebar menu
        binding.navView.setNavigationItemSelectedListener(item -> {
            Bundle idBundle = new Bundle();
            if (item.getItemId() == R.id.my_events) {
                idBundle.putString("userID", userID);
                navController.navigate(R.id.my_events, idBundle);
            } else if (item.getItemId() == R.id.event_finder) {
                idBundle.putString("userID", userID);
                navController.navigate(R.id.event_finder, idBundle);
            } else if (item.getItemId() == R.id.my_profile) {
                idBundle.putString("userID", userID);
                navController.navigate(R.id.my_profile, idBundle);
            } else if (item.getItemId() == R.id.settings) {
                idBundle.putString("userID", userID);
                navController.navigate(R.id.settings, idBundle);
            } else if (item.getItemId() == R.id.facility_information) {
                idBundle.putString("userID", userID);
                navController.navigate(R.id.facility_information, idBundle);
            }
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });


        // Set initials when new profile is created
        startUpActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                updateNameAndInitials(data.getStringExtra("firstName"), data.getStringExtra("lastName"), data.getStringExtra("userID"));
            }
        });
    }


    /**
     * Edit the default android studio options menu; used here to disable
     * @param menu is the menu object to be modified
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu); // commented out
        return false; // disable 3 dot menu
    }


    /**
     * Android studio functionality for navigating up within the app
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Updates displayed name and initials of user in the sidebar
     * @param firstName is the user's first name
     * @param lastName is the user's last name
     * @param userID is the userID associated with the user
     */
    private void updateNameAndInitials(String firstName, String lastName, String userID) {
        // Update sidebar details
        TextView textUserFirstName = findViewById(R.id.text_user_first_name);
        TextView sidebarIconInitials = findViewById(R.id.sidebar_icon_initials);

        String initials = "";
        initials += firstName.charAt(0);
        initials += lastName.charAt(0);

        textUserFirstName.setText(firstName);
        sidebarIconInitials.setText(initials.toUpperCase());
        updateUserImage(userID);
    }

    /**
     * updates the profile image in the nav sidebar
     * @param userID is user to get the profile image from
     */
    public void updateUserImage(String userID) {
        DocumentReference ref = db.collection("users").document(userID);

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    decodeImage(document.getString("encodedImage"));
                }
            }
        });
    }

    /**
     * decodes the image data into a usable asset
     * @param imageData data to decode into an image
     */
    private void decodeImage(String imageData) {
        System.out.println(imageData);
        ImageView image = findViewById(R.id.nav_profile_image);
        MaterialCardView rounder = findViewById(R.id.image_rounder_nav_profile);

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
     * Checks if the user exists in the database and facilitates the user to sign up if not.
     */
    private void checkUser() {
        db.collection("users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document != null) {
                                // User exists
                                Log.d("MainActivity", "User found: " + document.getData());
                                updateNameAndInitials(document.getString("firstName"), document.getString("lastName"), document.getString("userID"));
                            } else {
                                // User does not exist
                                Log.d("MainActivity", "User not found, starting StartUpActivity");
                                Intent intent = new Intent(getApplicationContext(), StartUpActivity.class);
                                startUpActivityLauncher.launch(intent);
                            }
                        } else {
                            Log.e("MainActivity", "Error getting user data", task.getException());
                        }
                    }
                });
    }
}