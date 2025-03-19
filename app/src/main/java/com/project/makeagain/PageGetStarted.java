package com.project.makeagain;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PageGetStarted extends AppCompatActivity {
    TextView getStarted;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstVisit = sharedPreferences.getBoolean("isFirstVisit", true);

        if (!isFirstVisit) {
            startActivity(new Intent(PageGetStarted.this, MainActivity.class));
            finish(); // Finish GetStartedActivity so it doesn't stay in the back stack
        }

        setContentView(R.layout.activity_get_started);

        getStarted = findViewById(R.id.getStarted);
        getStarted.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            return false;
        });

        getStarted.setOnClickListener(view -> {
            Utils.haptic(this);
            startActivity(new Intent(PageGetStarted.this, PageGetData.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

}