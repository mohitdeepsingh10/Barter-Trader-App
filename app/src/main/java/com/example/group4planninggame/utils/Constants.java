package com.example.group4planninggame.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class Constants {
    private Constants() {} // private constructor to prevent instantiation

    public static final String SESSION_FILE = "UserSession";
    public static final String FIREBASE_DATABASE = "https://group-4-planning-game-default-rtdb.firebaseio.com/";
    public static final String ROLE_RECEIVER = "Receiver";
    public static final String ROLE_PROVIDER = "Provider";

    /* The radius in meters within which items will be displayed in search */
    public static int ITEM_RADIUS = 10000;
    public static Location USER_LOCATION = null;
    public static String USER_EMAIL = null;
    public static String USER_ROLE = null;


    public static void loadSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_FILE, Context.MODE_PRIVATE);
        USER_EMAIL = sharedPreferences.getString("USER_EMAIL", null);
        USER_ROLE = sharedPreferences.getString("USER_ROLE", null);
    }

    public static void saveSession(Context context, String email, String role) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_EMAIL", email);
        editor.putString("USER_ROLE", role);
        editor.apply();
        USER_EMAIL = email;
        USER_ROLE = role;
    }

    public static void clearSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        USER_EMAIL = null;
        USER_ROLE = null;
    }
}
