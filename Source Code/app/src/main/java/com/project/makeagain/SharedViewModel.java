package com.project.makeagain;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SharedViewModel extends AndroidViewModel {
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> gender = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("UserProfileData", Context.MODE_PRIVATE);
        name.setValue(sharedPreferences.getString("name", "Default Name"));
        username.setValue(sharedPreferences.getString("username", "guest"));
        gender.setValue(sharedPreferences.getString("gender", "Other"));
    }

    public LiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.postValue(name);  // postValue() ensures thread safety
        saveData("name", name);
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username.postValue(username);
        saveData("username", username);
    }

    public LiveData<String> getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.postValue(gender);
        saveData("gender", gender);
    }

    private void saveData(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }
}
