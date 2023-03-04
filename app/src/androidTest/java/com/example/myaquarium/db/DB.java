package com.example.myaquarium.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    public static final int version = 1;
    public static final String name = "aq";

    public int id;
    public String login;
    public String password;
    public String user_name;
    public String surname;
    public String avatar;
    public int aquarium_volume;
    public String city;
    public String phone;

    public DB(@Nullable Context context) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "login TEXT," +
                "password TEXT," +
                "user_name TEXT," +
                "surname TEXT," +
                "avatar TEXT," +
                "aquarium_volume INTEGER," +
                "city TEXT," +
                "phone TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
