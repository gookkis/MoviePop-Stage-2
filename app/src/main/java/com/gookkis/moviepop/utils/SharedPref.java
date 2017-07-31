package com.gookkis.moviepop.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static final String PREFERENCE_NAME_FAV_MOVIES = "fav_movies";
    private static SharedPref instance = null;
    private final SharedPreferences sharedPreferences;

    private SharedPref(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFERENCE_NAME_FAV_MOVIES, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPref getInstance(Context context) {
        if (instance == null)
            instance = new SharedPref(context);
        return instance;
    }

    public boolean getBoolean(long key) {
        return sharedPreferences.getBoolean(String.valueOf(key), false);
    }

    public void putBoolean(long key, boolean value) {
        sharedPreferences.edit().putBoolean(String.valueOf(key), value).apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
