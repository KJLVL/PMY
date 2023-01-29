package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myaquarium.fragment.FragmentForumMy;
import com.example.myaquarium.fragment.FragmentForumSections;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Forum extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private  Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        this.setToolbar();

        loadFragment(FragmentForumSections.newInstance(1));
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_sections:
                    loadFragment(FragmentForumSections.newInstance(1));
                    return true;
                case R.id.navigation_swap:
                    loadFragment(FragmentForumSections.newInstance(2));
                    return true;
                case R.id.navigation_my:
                    loadFragment(FragmentForumMy.newInstance());
                    return true;
            }
            return false;
        });


        TextView calculator = findViewById(R.id.service);
        TextView profile = findViewById(R.id.profile);


        calculator.setOnClickListener(view -> {
            this.startActivity(new Intent(this, Service.class));
        });

        profile.setOnClickListener(view -> {
            this.startActivity(new Intent(this, Profile.class));
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView textView = findViewById(R.id.title);
        textView.setText(getApplicationContext().getString(R.string.forum_text));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.scrollSections, fragment);
        ft.commit();
    }
}