package com.project.makeagain;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecentlyViewedManager {
    private static final String PREF_NAME = "recently_viewed_books";
    private static final String KEY_BOOKS = "book_list";

    public static void addBook(Context context, String bookJson) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String existing = prefs.getString(KEY_BOOKS, "[]");

        try {
            JSONArray jsonArray = new JSONArray(existing);
            JSONObject newBook = new JSONObject(bookJson);

            // Check for duplicate (based on title, or use id if available)
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject book = jsonArray.getJSONObject(i);
                if (book.getJSONObject("volumeInfo").getString("title")
                        .equalsIgnoreCase(newBook.getJSONObject("volumeInfo").getString("title"))) {
                    return; // already exists
                }
            }

            jsonArray.put(newBook);

            // ✅ Keep only latest 5
            while (jsonArray.length() > 5) {
                jsonArray.remove(0);
            }

            prefs.edit().putString(KEY_BOOKS, jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static JSONArray getBooks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String saved = prefs.getString(KEY_BOOKS, "[]");

        try {
            return new JSONArray(saved);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

}
