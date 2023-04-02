package com.example.myaquarium.service;

import android.content.Intent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myaquarium.Forum;
import com.example.myaquarium.Profile;
import com.example.myaquarium.R;
import com.example.myaquarium.Service;

import java.util.Objects;

public class Navigation {

    public static void setToolbar (
            AppCompatActivity activity,
            CharSequence title,
            Class<?> tClass
    ) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        if (title != null) {
            TextView textView = activity.findViewById(R.id.title);
            textView.setText(title);
        }

        if (tClass == null) {
            return;
        }

        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(
                view -> activity.startActivity(new Intent(activity, tClass))
        );
    }

    public static void setMenuNavigation (AppCompatActivity activity) {
        TextView calculator = activity.findViewById(R.id.service);
        TextView profile = activity.findViewById(R.id.profile);
        TextView forum = activity.findViewById(R.id.forum);

        calculator.setOnClickListener(view -> activity.startActivity(new Intent(activity, Service.class)));
        forum.setOnClickListener(view -> activity.startActivity(new Intent(activity, Forum.class)));
        profile.setOnClickListener(view -> activity.startActivity(new Intent(activity, Profile.class)));
    }
}
