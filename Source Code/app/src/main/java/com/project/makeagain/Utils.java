package com.project.makeagain;

import static android.content.Context.VIBRATOR_SERVICE;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    public static String getRandomKeyword() {
        String[] keywords = {"adventures", "science", "technology", "love", "success", "fiction", "learning"};
        return keywords[new Random().nextInt(keywords.length)];
    }

    public static List<Integer> getDistinctColors(int count) {
        List<Integer> baseColors = new ArrayList<>();

        baseColors.addAll(toList(ColorTemplate.MATERIAL_COLORS));
        baseColors.addAll(toList(ColorTemplate.VORDIPLOM_COLORS));
        baseColors.addAll(toList(ColorTemplate.COLORFUL_COLORS));
        baseColors.addAll(toList(ColorTemplate.JOYFUL_COLORS));
        baseColors.addAll(toList(ColorTemplate.LIBERTY_COLORS));
        baseColors.addAll(toList(ColorTemplate.PASTEL_COLORS));

        // Add random colors if needed
        while (baseColors.size() < count) {
            baseColors.add(Color.rgb(
                    new Random().nextInt(256),
                    new Random().nextInt(256),
                    new Random().nextInt(256)
            ));
        }

        return baseColors.subList(0, count);
    }

    private static List<Integer> toList(int[] arr) {
        List<Integer> list = new ArrayList<>();
        for (int val : arr) list.add(val);
        return list;
    }



}
