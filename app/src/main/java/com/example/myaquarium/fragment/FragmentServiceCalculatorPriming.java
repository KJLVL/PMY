package com.example.myaquarium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.myaquarium.R;
import com.example.myaquarium.Service;

public class FragmentServiceCalculatorPriming extends Fragment {
    private View inflatedView;

    private Button calcPriming;
    private TextView recPrimText;

    public static FragmentServiceCalculatorPriming newInstance() {
        return new FragmentServiceCalculatorPriming();
    }

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
        toolbar.setNavigationOnClickListener(view -> {
            this.startActivity(new Intent(inflatedView.getContext(), Service.class));
        });

        ActionBar actionBar = ((Service)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setMessage() {
        Button btnPr = inflatedView.findViewById(R.id.btnPr);
        btnPr.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(inflatedView.getContext());
            dialog.setTitle("Расчет грунта");
            dialog.setMessage("Перед покупкой грунта в аквариум необходимо определить его количество, чтобы не остался лишний или наоборот, не хватило. Калькулятор поможет вам в этом! Просто введите указанные данные!");
            dialog.setPositiveButton("Закрыть", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            dialog.show();
        });
    }

    private void calculatePriming() {
        calcPriming.setOnClickListener(view -> {
            EditText length = inflatedView.findViewById(R.id.length);
            EditText width = inflatedView.findViewById(R.id.width);
            EditText thickness = inflatedView.findViewById(R.id.thickness);
            TextView resultText = inflatedView.findViewById(R.id.priming);
            recPrimText = inflatedView.findViewById(R.id.recPrimText);

            if (length.getText().toString().equals("")
                    || width.getText().toString().equals("")
                    || thickness.getText().toString().equals("")
            ) {
                resultText.setVisibility(View.GONE);
                recPrimText.setVisibility(View.GONE);
                AlertDialog.Builder dialog = new AlertDialog.Builder(inflatedView.getContext());
                dialog.setTitle("Ошибка");
                dialog.setMessage("Заполните все поля");
                dialog.setPositiveButton(
                        "Закрыть",
                        (dialogInterface, i) -> dialogInterface.dismiss()
                );
                dialog.show();
                return;
            }

            recPrimText.setVisibility(View.VISIBLE);
            double result =
                    Integer.parseInt(length.getText().toString()) *
                            Integer.parseInt(width.getText().toString()) *
                            Integer.parseInt(thickness.getText().toString()) * 1.4 / 1000;


            String resultMessage = String.format("%.2f", result) + " кг.";
            resultText.setVisibility(View.VISIBLE);
            resultText.setText(resultMessage);

        });
    }
}