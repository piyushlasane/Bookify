package com.project.makeagain;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PageGetData extends AppCompatActivity {

    private boolean isNameFilled = false;
    private boolean isGenderSelected = false;
    FrameLayout submit;
    Spinner spinnerGender;
    LinearLayout selectGenderLayout;
    EditText enterName;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);

        // Initialize the Spinner and LinearLayout
        spinnerGender = findViewById(R.id.spinnerGender);
        selectGenderLayout = findViewById(R.id.selectGender);
        submit = findViewById(R.id.submit);
        enterName = findViewById(R.id.enterName);

        // Create an ArrayAdapter with gender options
        String[] genderOptions = {"Choose Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,  // Custom layout for spinner items
                genderOptions
        );
        adapter.setDropDownViewResource(R.layout.spinner_item); // Use the same layout for dropdown items
        spinnerGender.setAdapter(adapter);

        enterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isNameFilled = !TextUtils.isEmpty(s.toString().trim());
                updateSubmitButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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
                isGenderSelected = position != 0; // First position is placeholder
                updateSubmitButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectGenderLayout.setOnClickListener(v -> spinnerGender.performClick());


        submit.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            return false;
        });

        submit.setOnClickListener(view -> {
            String name = enterName.getText().toString().trim();
            String selectedGender = spinnerGender.getSelectedItem().toString();
            if (!isNameFilled) {
                Utils.vibrate(this, 200);
                shakeButton(submit);
                Toast.makeText(PageGetData.this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            } else if (!name.matches("[a-zA-Z ]+")) {
                Utils.vibrate(this, 200);
                shakeButton(submit);
                enterName.setError("Name can't contain special symbols or numbers");
            } else if (!isGenderSelected) {
                Utils.vibrate(this, 200);
                shakeButton(submit);
                Toast.makeText(PageGetData.this, "Please select a valid gender", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstVisit", false); // Mark as NOT first time
                editor.apply();
                Utils.haptic(this);
                Intent intent = new Intent(PageGetData.this, MainActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("selectedGender", selectedGender);
                startActivity(intent);
                finish();
            }
        });

    }

    private void updateSubmitButton() {
        int buttonDrawable = isNameFilled && isGenderSelected ? R.drawable.xicon_loaded : R.drawable.icon_load;
        submit.setBackground(ContextCompat.getDrawable(this, buttonDrawable));
    }

    public void shakeButton(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 20, -20, 10, -10, 0);
        animator.setDuration(500);
        animator.start();
    }

}