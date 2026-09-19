package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "NotesAppPreferences";
    private static final int PREF_MODE = Context.MODE_PRIVATE;

    private static SessionManager instance = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String KEY_REMEMBERED_EMAIL = "rememberedEmail";
    private static final String KEY_LOGGED_IN_EMAIL = "loggedInEmail";
    private static final String KEY_SORT_PREFERENCE = "sortPreference";

    private SessionManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, PREF_MODE);
        editor = sharedPreferences.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void setRememberedEmail(String email) {
        editor.putString(KEY_REMEMBERED_EMAIL, email);
        editor.commit();
    }

    public String getRememberedEmail() {
        return sharedPreferences.getString(KEY_REMEMBERED_EMAIL, "");
    }

    public void clearRememberedEmail() {
        editor.remove(KEY_REMEMBERED_EMAIL);
        editor.commit();
    }

    public void setLoggedInUser(String email) {
        editor.putString(KEY_LOGGED_IN_EMAIL, email);
        editor.commit();
    }

    public String getLoggedInUser() {
        return sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, null);
    }

    public void logout() {
        editor.remove(KEY_LOGGED_IN_EMAIL);
        editor.commit();
    }

    public void setSortPreference(String sortBy) {
        editor.putString(KEY_SORT_PREFERENCE, sortBy);
        editor.commit();
    }

    public String getSortPreference() {
        return sharedPreferences.getString(KEY_SORT_PREFERENCE, "date");
    }
}
