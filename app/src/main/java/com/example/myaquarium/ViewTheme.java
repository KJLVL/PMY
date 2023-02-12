package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.myaquarium.fragment.FragmentForumSections;
import com.example.myaquarium.fragment.FragmentForumViewDiscussions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ViewTheme extends AppCompatActivity {
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_theme);

        this.setToolbar();

        Bundle arguments = getIntent().getExtras();
        id = arguments.get("id").toString();
        JSONObject theme = new JSONObject();
        try {
            theme = new JSONObject(arguments.getString("theme"));
        } catch (JSONException ignored) {
            ignored.printStackTrace();
        }


        switch (id) {
            case "1":
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.scrollViewTheme, FragmentForumViewDiscussions.newInstance(theme));
                ft.commit();
                break;
            case "2":

                break;
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView = findViewById(R.id.title);
        textView.setText(getApplicationContext().getString(R.string.forum_text));

        toolbar.setNavigationOnClickListener(view -> {
            switch (id) {
                case "1":
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.view, FragmentForumSections.newInstance(1));
                    ft.commit();
                    break;
                case "2":

                    break;
            }
            this.startActivity(new Intent(this, Forum.class));
        });
    }
}