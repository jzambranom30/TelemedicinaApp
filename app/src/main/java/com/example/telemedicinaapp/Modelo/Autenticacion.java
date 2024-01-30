package com.example.telemedicinaapp.Modelo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Autenticacion {
    private static final String PREF_NAME = "AuthPrefsec";
    private static final String KEY_TOKEN = "token";

    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, "");
    }
}

