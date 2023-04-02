package com.example.myaquarium.service;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class UserData {

    public static void setUserData(AppCompatActivity activity, String data) {
        SharedPreferences.Editor editor = getSharedPreferences(activity).edit();
        editor.putString("id", data);
        editor.apply();
    }

    public static String getUserData(AppCompatActivity activity) {
        return getSharedPreferences(activity).getString("id", null);
    }

    public static void clearUserData(AppCompatActivity activity) {
        SharedPreferences.Editor editor = getSharedPreferences(activity).edit();
        editor.clear();
        editor.apply();
    }

    public static SharedPreferences getSharedPreferences(AppCompatActivity activity) {
        return activity.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
    }
}
