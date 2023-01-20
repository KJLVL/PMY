package com.example.myaquarium.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.myaquarium.R;

public class FragmentServiceCalendar extends Fragment {

    public static FragmentServiceCalendar newInstance() {
        return new FragmentServiceCalendar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(
                R.layout.fragment_service_calculator_fish,
                container,
                false
        );



        return inflatedView;
    }



}