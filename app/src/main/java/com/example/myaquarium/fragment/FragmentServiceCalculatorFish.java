package com.example.myaquarium.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.Service;
import com.example.myaquarium.adapter.CalculatorFishAdapter;
import com.example.myaquarium.adapter.ResultCompatibilityAdapter;
import com.example.myaquarium.service.CalculateMessages;
import com.example.myaquarium.service.Requests;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FragmentServiceCalculatorFish extends Fragment {
    private View inflatedView;
    private RecyclerView listview;
    private RecyclerView listviewResult;
    private Button calculationFish;
    private CheckBox useMyFish;
    private LinearLayout textResult;

    private Requests requests;

    private CalculatorFishAdapter fishAdapter;
    private ResultCompatibilityAdapter compatibilityAdapter;

    private boolean[] checked;
    private List<String> fishList;
    private List<String> currentFishList;
    private List<List<String>> resultComp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_service_calculator_fish,
                container,
                false
        );

        resultComp = new ArrayList<>();
        currentFishList = new ArrayList<>();
        requests = new Requests();

        listview = inflatedView.findViewById(R.id.listview);
        listviewResult = inflatedView.findViewById(R.id.listviewResult);
        calculationFish = inflatedView.findViewById(R.id.calculationFish);
        useMyFish = inflatedView.findViewById(R.id.useMyFish);
        textResult = inflatedView.findViewById(R.id.textResult);

        this.setToolbar();
        this.setMessage();

        this.getFishList();

        useMyFish.setOnClickListener(view -> {
            if (useMyFish.isChecked()) {
                this.getUserFish();
            } else {
                currentFishList.clear();
                Arrays.fill(checked, false);
                setFishList(fishList);
            }
        });

        this.calculateFish();

        return inflatedView;
    }

    private void getUserFish() {

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("id", sharedpreferences.getString("id", null))
        )
        );
        Runnable runnable = () -> {
            try {
                currentFishList.clear();

                JSONArray list = requests.setRequest(requests.urlRequest + "user/fish", params);
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    if (object.toString().contains("success")) {
                        listview.post(() -> {
                            useMyFish.setChecked(false);
                            useMyFish.setClickable(false);
                            Toast.makeText(
                                    inflatedView.getContext(),
                                    "Данные о ваших рыбках не заполнены", Toast.LENGTH_SHORT
                            ).show();
                        });
                        return;
                    }

                    currentFishList.add(object.getString("fish"));
                }

                for (int i = 0; i < fishList.size(); i++) {
                    for (String fish: currentFishList) {
                        if (fish.equals(fishList.get(i))) {
                            checked[i] = true;
                            break;
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setCompatibilityList(List<List<String>> compList) {
        listviewResult.post(() -> {
            if (compList.size() != 0) {
                textResult.setVisibility(View.VISIBLE);
                listviewResult.setVisibility(View.VISIBLE);
                TextView btnRes = inflatedView.findViewById(R.id.btnRes);
                btnRes.setOnClickListener(view -> {
                    LinearLayout layout = inflatedView.findViewById(R.id.result);
                    if (layout.getVisibility() == View.GONE) {
                        layout.setVisibility(View.VISIBLE);
                    } else {
                        layout.setVisibility(View.GONE);
                    }
                });

                compatibilityAdapter = new ResultCompatibilityAdapter(inflatedView.getContext(), compList);
                listviewResult.setAdapter(compatibilityAdapter);
            }
        });
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
        TextView btnFish = inflatedView.findViewById(R.id.btnFish);
        btnFish.setOnClickListener(view -> {
            CalculateMessages.setMessage(inflatedView, R.string.service_title_fish, R.string.service_msg_fish);
        });
    }

    private void getFishList() {
        fishList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "fish/list", new ArrayList<>());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(result.getJSONObject(i)));
                    fishList.add(object.getString("fish_name"));
                }
                this.setFishList(fishList);
                this.inflatedView.post(() -> fishAdapter.notifyDataSetChanged());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setFishList(List<String> items) {
        listview.post(() -> {
            fishAdapter = new CalculatorFishAdapter(inflatedView.getContext(), items);
            listview.setAdapter(fishAdapter);
            checked = fishAdapter.getChecked();
        });
    }

    private void calculateFish() {
        calculationFish.setOnClickListener(view -> {
            currentFishList.clear();
            for (int i = 0; i < fishList.size(); i++) {
                if (checked[i]) {
                    currentFishList.add(fishList.get(i));
                }
            }
            if (currentFishList.size() < 2) {
                resultComp.clear();
                setCompatibilityList(resultComp);
                Toast.makeText(
                        inflatedView.getContext(),
                        "Выберите хотя-бы двух рыбок", Toast.LENGTH_SHORT
                ).show();
                return;
            }
            this.getCompatibility();
        });
    }

    private void getCompatibility() {
        resultComp = new ArrayList<>();

        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("fish", String.join(",", currentFishList))
            )
        );
        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "calculate", params);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(result.getJSONObject(i)));
                    String name = Objects.requireNonNull(object.names()).getString(0);
                    List<String> fish = new ArrayList<>(List.of(
                            name,
                            object.getString(name)

                    ));
                    resultComp.add(fish);
                }
                this.setCompatibilityList(resultComp);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}