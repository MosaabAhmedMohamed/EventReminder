package com.example.eventreminder.refactoring.data.local;


import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesHelper {
    private final SharedPreferences preferences;

    @Inject
    public PreferencesHelper(SharedPreferences sharedPreferences) {
        preferences = sharedPreferences;
    }

    public void putString(@NonNull String key, @NonNull String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getString(@NonNull String key) {
        return preferences.getString(key, "");
    }

    public void putBoolean(@NonNull String key, @NonNull boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(@NonNull String key) {
        return preferences.getBoolean(key, false);
    }

    public void putInt(@NonNull String key, @NonNull boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public int getInt(@NonNull String key) {
        return preferences.getInt(key, -1);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public void removeKey(@NonNull String key) {
        preferences.edit().remove(key).apply();
    }

    public boolean containKey(String key) {
        if (preferences.contains(key))
            return true;
        return false;
    }

}
