package com.project.makeagain;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load from SharedPreferences if Intent data is null
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String name = getIntent().getStringExtra("name");
        if (name == null) {
            name = sharedPreferences.getString("name", "Default Name");
        }

        String selectedGender = getIntent().getStringExtra("selectedGender");
        if (selectedGender == null) {
            selectedGender = sharedPreferences.getString("gender", "Other");
        }

        // Check if username already exists
        String username = sharedPreferences.getString("username", null);
        if (username == null) {
            // Generate a username for the first-time user
            username = Utils.getFirstWord(name).toLowerCase();
            editor.putString("username", username);
            editor.apply();  // Save the generated username
        }

        // Set values in SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setName(name);
        sharedViewModel.setGender(selectedGender);
        sharedViewModel.setUsername(username);

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.navigation_panel);
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        handleKeyboardVisibility();

    }

    private void handleKeyboardVisibility() {
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
            boolean isKeyboardShown = heightDiff > dpToPx(); // 200dp is a threshold

            BottomNavigationView nav = findViewById(R.id.navigation_panel);
            if (isKeyboardShown) {
                nav.setVisibility(View.GONE);
            } else {
                nav.setVisibility(View.VISIBLE);
            }
        });
    }

    private int dpToPx() {
        return (int) (200 * getResources().getDisplayMetrics().density);
    }


}