package com.project.makeagain;

import static android.content.Context.VIBRATOR_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Utils {
    public static Vibrator vibrator;

    public static void haptic(@NonNull Context context) {
        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50); // Required for older Android versions
            }
        }
    }

    public static void vibrate(@NonNull Context context, long duration) {
        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
//          long[] pattern = {0, 200, 100, 200}; // Wait, vibrate, pause, vibrate
//          vibrator.vibrate(pattern, -1); // '-1' means no repetition
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public static String capitalizeWords(String input) {
        if (TextUtils.isEmpty(input.trim())) return "Guest";
        return Arrays.stream(input.trim().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static String getFirstWord(String input) {
        if (TextUtils.isEmpty(input.trim())) return "guest"; // Trim first
        String[] words = input.trim().split("\\s+");
        return words.length > 0 ? words[0] : "guest";
    }

}
