package com.example.myaquarium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.myaquarium.R;
import com.example.myaquarium.Service;
import com.example.myaquarium.service.CalculateMessages;

public class FragmentServiceCalculatorPriming extends Fragment {
    private View inflatedView;
    private Button calcPriming;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_service_calculator_priming,
                container,
                false
        );
        this.setToolbar();
        this.setMessage();

        calcPriming = inflatedView.findViewById(R.id.calculationP);
        this.calculatePriming();

        return inflatedView;
    }

    private void setToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(
                view -> this.startActivity(new Intent(inflatedView.getContext(), Service.class))
        );

        ActionBar actionBar = ((Service)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setMessage() {
        TextView btnPr = inflatedView.findViewById(R.id.btnPr);
        btnPr.setOnClickListener(view -> {
            CalculateMessages.setMessage(inflatedView, R.string.service_title_priming, R.string.service_msg_priming);
        });
    }

    private void calculatePriming() {
        calcPriming.setOnClickListener(view -> {
            EditText length = inflatedView.findViewById(R.id.length);
            EditText width = inflatedView.findViewById(R.id.width);
            EditText thickness = inflatedView.findViewById(R.id.thickness);

            TextView resultText = inflatedView.findViewById(R.id.priming);

            LinearLayout resultLayout = inflatedView.findViewById(R.id.result);

            if (length.getText().toString().equals("")
                    || width.getText().toString().equals("")
                    || thickness.getText().toString().equals("")
            ) {
                resultLayout.setVisibility(View.GONE);
                Toast.makeText(
                        inflatedView.getContext(),
                        "Заполните все поля", Toast.LENGTH_SHORT
                ).show();
                return;
            }

            double result =
                    Double.parseDouble(length.getText().toString()) *
                            Double.parseDouble(width.getText().toString()) *
                            Double.parseDouble(thickness.getText().toString()) * 1.4 / 1000;


            String resultMessage = String.format("%.2f", result) + " кг.";
            resultText.setText(resultMessage);
            resultLayout.setVisibility(View.VISIBLE);
        });
    }
}