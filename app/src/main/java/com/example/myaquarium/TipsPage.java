package com.example.myaquarium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TipsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_page);
        ImageView image = findViewById(R.id.image);
        TextView text = findViewById(R.id.title);

        image.setImageResource(getIntent().getIntExtra("image", 0));
        text.setText(getIntent().getStringExtra("title"));
    }
}