package com.example.myaquarium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myaquarium.Calendar;
import com.example.myaquarium.R;
import com.example.myaquarium.Tips;

public class FragmentService extends Fragment {
    private View inflatedView;
    private ImageView fish;
    private ImageView volume;
    private ImageView priming;
    private ImageView tips;
    private ImageView calendar;

    public static FragmentService newInstance() {
        return new FragmentService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_service,
                container,
                false
        );
        fish = inflatedView.findViewById(R.id.fish);
        volume = inflatedView.findViewById(R.id.volume);
        priming = inflatedView.findViewById(R.id.priming);
        tips = inflatedView.findViewById(R.id.tips);
        calendar = inflatedView.findViewById(R.id.calendar);

        this.setNavigationByImage();


        return inflatedView;
    }

    private void setNavigationByImage() {
        fish.setOnClickListener(view -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.service, new FragmentServiceCalculatorFish());
            transaction.addToBackStack(null);

            transaction.commit();
        });

        volume.setOnClickListener(view -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.service, new FragmentServiceCalculatorVolume());
            transaction.addToBackStack(null);

            transaction.commit();
        });

        priming.setOnClickListener(view -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.service, new FragmentServiceCalculatorPriming());
            transaction.addToBackStack(null);

            transaction.commit();
        });

        tips.setOnClickListener(view -> {
            startActivity(new Intent(inflatedView.getContext(), Tips.class));
        });

        calendar.setOnClickListener(view -> {
            startActivity(new Intent(inflatedView.getContext(), Calendar.class));
        });
    }
}