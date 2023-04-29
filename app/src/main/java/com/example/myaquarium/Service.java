package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.myaquarium.fragment.FragmentServiceCalculatorFish;
import com.example.myaquarium.fragment.FragmentServiceCalculatorPriming;
import com.example.myaquarium.fragment.FragmentServiceCalculatorVolume;
import com.example.myaquarium.service.Navigation;

import java.util.Objects;

public class Service extends AppCompatActivity {
    private LinearLayout layoutService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        this.setToolbar();

        Navigation.setMenuNavigation(this);
        this.setNavigationByImage();
    }

    private void setNavigationByImage() {
        ImageView fish = findViewById(R.id.fish);
        ImageView volume = findViewById(R.id.volume);
        ImageView priming = findViewById(R.id.priming);
        ImageView tips = findViewById(R.id.tips);
        ImageView calendar = findViewById(R.id.calendar);
        layoutService = findViewById(R.id.scrollService);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);

        fish.setOnClickListener(view -> {
            layoutService.removeAllViews();
            transaction.add(R.id.scrollService, new FragmentServiceCalculatorFish());
            transaction.commit();
        });

        volume.setOnClickListener(view -> {
            layoutService.removeAllViews();
            transaction.replace(R.id.scrollService, new FragmentServiceCalculatorVolume());
            transaction.commit();
        });

        priming.setOnClickListener(view -> {
            layoutService.removeAllViews();
            transaction.replace(R.id.scrollService, new FragmentServiceCalculatorPriming());
            transaction.commit();
        });

        tips.setOnClickListener(
                view -> startActivity(new Intent(Service.this, Tips.class))
        );

        calendar.setOnClickListener(
                view -> startActivity(new Intent(Service.this, Calendar.class))
        );
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        TextView textView = findViewById(R.id.title);
        textView.setText(getApplicationContext().getString(R.string.service_text));
    }
}