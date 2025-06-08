package com.project.makeagain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageEditProfile extends AppCompatActivity {

    EditText editName, editUsername;
    CircleImageView profileImage;
    Spinner spinnerGender;
    LinearLayout selectGenderLayout;
    TextView updateProfile;
    SharedViewModel sharedViewModel;
    String[] genderOptions = {"Male", "Female", "Other"};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.editName);
        editUsername = findViewById(R.id.editUserName);
        profileImage = findViewById(R.id.profile_image);
        spinnerGender = findViewById(R.id.spinnerGender);
        selectGenderLayout = findViewById(R.id.selectGender);
        updateProfile = findViewById(R.id.updateProfile);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,  // Custom layout for spinner items
                genderOptions
        );
        adapter.setDropDownViewResource(R.layout.spinner_item); // Use the same layout for dropdown items

        spinnerGender.setAdapter(adapter);

        // Prefill the fields
        prefill();

        spinnerGender.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (imm != null && view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return false;
        });


        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profileImage.setImageResource(spinnerGender.getSelectedItem().toString().equalsIgnoreCase("female") ? R.drawable.profile_icon_female : R.drawable.profile_icon_male);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        selectGenderLayout.setOnClickListener(v -> spinnerGender.performClick());

        updateProfile.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            return false;
        });
        updateProfile.setOnClickListener(v -> {
            // Create an Intent to send updated data back to the calling activity
            Utils.haptic(this);
            String updatedName = editName.getText().toString();
            String updatedUsername = editUsername.getText().toString();
            String updatedGender = spinnerGender.getSelectedItem().toString();

            // Save updated data to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserProfileData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", updatedName);
            editor.putString("username", updatedUsername);
            editor.putString("gender", updatedGender);
            editor.apply();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", updatedName);
            resultIntent.putExtra("username", updatedUsername);
            resultIntent.putExtra("gender", updatedGender);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    public void prefill() {
        // Get data from the Intent
        Intent intent = getIntent();
        String name = Objects.requireNonNullElse(intent.getStringExtra("name"), "");
        String username = Objects.requireNonNullElse(intent.getStringExtra("username"), "");
        String gender = Objects.requireNonNullElse(intent.getStringExtra("gender"), "");

        editName.setText(name);
        editUsername.setText(username);
        profileImage.setImageResource(gender.equalsIgnoreCase("female") ? R.drawable.profile_icon_female : R.drawable.profile_icon_male);

        for (int i = 0; i < genderOptions.length; i++) {
            if (genderOptions[i].equals(gender)) {
                spinnerGender.setSelection(i);
                break;
            }
        }
    }

}