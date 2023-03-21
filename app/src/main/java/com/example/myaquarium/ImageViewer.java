package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ImageViewer extends AppCompatActivity {
    private Intent intent;
    private Bundle arguments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        this.setToolbar();

        this.arguments = getIntent().getExtras();
        String uri = arguments.get("image").toString();
        String className = arguments.get("class").toString();

        this.generateClass(className);

        PhotoView image = findViewById(R.id.image);
        Picasso.get().load(uri).into(image);
    }

    private void generateClass(String className) {
        switch (className) {
            case "ProfileSettings":
                this.intent = new Intent(this, ProfileSettings.class);
                break;
            case "TipsPage":
                this.intent = new Intent(this, TipsPage.class);
                this.intent.putExtra("id", this.arguments.get("id").toString());
                break;
            case "Profile":
                this.intent = new Intent(this, Profile.class);
                break;
            case "ViewTheme":
                this.intent = new Intent(this, ViewTheme.class);
                this.intent.putExtra("theme", this.arguments.get("theme").toString());
                this.intent.putExtra("id", this.arguments.get("id").toString());
                break;
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> this.startActivity(intent));
    }
}