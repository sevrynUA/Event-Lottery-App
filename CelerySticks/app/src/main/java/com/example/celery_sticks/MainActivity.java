package com.example.celery_sticks;

import android.content.Intent;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private String userID;

    private ActivityResultLauncher<Intent> loginActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database
        db = FirebaseFirestore.getInstance();

        // Get device ID
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


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
            if (item.getItemId() == R.id.settings) {
                idBundle.putString("userID", userID);
                navController.navigate(R.id.settings, idBundle);
            }
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });


        // Set initials when new profile is created
        loginActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                updateNameAndInitials(data.getStringExtra("firstName"), data.getStringExtra("lastName"), data.getStringExtra("userID"));
            }
        });

        // Check if user exists in database
        checkUser();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu); // commented out
        return false; // disable 3 dot menu
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void updateNameAndInitials(String firstName, String lastName, String userID) {
        // Update sidebar details
        TextView textUserFirstName = findViewById(R.id.text_user_first_name);
        TextView sidebarIconInitials = findViewById(R.id.sidebar_icon_initials);

        String initials = "";
        initials += firstName.charAt(0);
        initials += lastName.charAt(0);

        textUserFirstName.setText(firstName);
        sidebarIconInitials.setText(initials.toUpperCase());
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
                                loginActivityLauncher.launch(intent);
                            }
                        } else {
                            Log.e("MainActivity", "Error getting user data", task.getException());
                        }
                    }
                });
    }
}