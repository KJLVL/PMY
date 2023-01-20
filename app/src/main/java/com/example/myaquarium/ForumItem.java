package com.example.myaquarium;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ForumItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_item);
        TextView textView = findViewById(R.id.title);
        Bundle arguments = getIntent().getExtras();
        textView.setText(arguments.get("id").toString());

    }
}