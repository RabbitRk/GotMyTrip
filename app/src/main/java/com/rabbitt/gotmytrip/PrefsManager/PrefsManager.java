package com.rabbitt.gotmytrip.PrefsManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    // Shared preferences file name
    private static final String PREF_NAME = "USER_PREFS";
    private static final String LOGIN = "IsFirstTimeLaunch";
    //user details
    public static final String USER_PREFS = "USER_DETAILS";
    public static final String TRAVEL_PREFS = "TRAVEL_PREFS";
    public static final String TRAVEL_ID = "TRAVEL_ID";

    public static final String ID_KEY = "ID_KEY";
    public static final String USER_NAME = "USER_KEY";
    private static final String USER_PHONE = "USER_PHONE";
    private static final String USER_EMAIL = "USER_EMAIL";
    private SharedPreferences pref, tra_prefs;
    private SharedPreferences.Editor editor, user_editor, tra_editor;

    @SuppressLint("CommitPrefEdits")
    public PrefsManager(Context context) {
        // shared pref mode
        int PRIVATE_MODE = 0;

        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        tra_prefs = context.getSharedPreferences(TRAVEL_PREFS, PRIVATE_MODE);
        tra_editor = tra_prefs.edit();

        SharedPreferences userpref = context.getSharedPreferences(USER_PREFS, PRIVATE_MODE);
        user_editor = userpref.edit();
    }

   public boolean isFirstTimeLaunch() {
        return pref.getBoolean(LOGIN, false);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(LOGIN, isFirstTime);
        editor.commit();
    }

    public String getTravel_type() {
        return tra_prefs.getString(TRAVEL_ID, "");
    }

    public void setTravel_type(String tra) {
        tra_editor.putString(TRAVEL_ID, tra);
        tra_editor.commit();
    }




    public void userPreferences(String id, String username, String phonenumber, String email) {
        user_editor.putString(ID_KEY,id);
        user_editor.putString(USER_NAME,username);
        user_editor.putString(USER_EMAIL,phonenumber);
        user_editor.putString(USER_PHONE,email);
        user_editor.commit();
    }
}