package com.example.myaquarium.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.Service;
import com.example.myaquarium.adapter.FishListAdapter;
import com.example.myaquarium.service.CalculateMessages;
import com.example.myaquarium.service.Requests;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.utkala.searchablespinner.SearchableSpinner;

public class FragmentServiceCalculatorVolume extends Fragment {
    private View inflatedView;

    private Button calcVolume;

    private RecyclerView fishRecycler;

    private SearchableSpinner fishSpinner;

    private CheckBox useMyFish;

    private LinearLayout resultView;
    private TextView result;
    private TextView message;

    private Requests requests;

    private FishListAdapter fishAdapter;

    private List<JSONObject> fishListAll;
    private List<String> fishList;
    private static List<JSONObject> fishListCurrent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_service_calculator_volume,
                container,
                false
        );
        requests = new Requests();

        this.setToolbar();
        this.setMessage();
        fishListCurrent = new ArrayList<>();

        fishSpinner = inflatedView.findViewById(R.id.fishSpinner);
        message = inflatedView.findViewById(R.id.message);
        fishRecycler = inflatedView.findViewById(R.id.fishListItems);
        result = inflatedView.findViewById(R.id.volume);
        resultView = inflatedView.findViewById(R.id.resultView);
        useMyFish = inflatedView.findViewById(R.id.useMyFish);

        this.getFishList();

        if (fishListCurrent.size() != 0) {
            setFishList(fishListCurrent);
        }
        calcVolume = inflatedView.findViewById(R.id.calculationV);
        this.calculateVolume();

        useMyFish.setOnClickListener(view -> {
            if (useMyFish.isChecked()) {
                this.getUserFish();
            } else if (!useMyFish.isChecked()) {
                fishListCurrent.clear();
                resultView.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                this.setFishList(fishListCurrent);
            }
        });


        return inflatedView;
    }

    private void setToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(
                view -> this.startActivity(new Intent(inflatedView.getContext(), Service.class))
        );

        ActionBar actionBar = ((Service) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setMessage() {
        TextView btnVol = inflatedView.findViewById(R.id.btnVol);
        btnVol.setOnClickListener(view -> {
            CalculateMessages.setMessage(inflatedView, R.string.service_title_volume, R.string.service_msg_volume);
        });
    }

    private void calculateVolume() {
        calcVolume.setOnClickListener(view -> {
            if (calculateFishByList(fishListCurrent) == 0) {
                resultView.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                Toast.makeText(
                        inflatedView.getContext(),
                        "Выберите хотя бы 1 рыбку", Toast.LENGTH_SHORT
                ).show();
                return;
            }
            result.setText(calculateFishByList(fishListCurrent) + " л.");
            resultView.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            message.requestFocus();
            resultView.requestFocus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                message.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            }
        });
    }

    private int calculateFishByList(List<JSONObject> fishListCurrent) {
        int volume = 0;

        for (JSONObject currentFish : fishListCurrent) {
            String fishName = currentFish.optString("fish");
            int fishCount = Integer.parseInt(currentFish.optString("count"));

            for (JSONObject fish : fishListAll) {
                if (fish.optString("fish_name").equals(fishName)) {
                    volume += fishCount * Integer.parseInt(fish.optString("liter"));
                    break;
                }
            }
        }

        return volume;
    }

    private void getFishList() {
        fishListAll = new ArrayList<>();
        fishList = new ArrayList<>();
        fishList.add("");
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "fish/list", new ArrayList<>());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    fishList.add(object.getString("fish_name"));
                    fishListAll.add(object);
                }
                inflatedView.post(() -> this.setSpinnerActions(fishList));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setSpinnerActions(List<String> fishList) {
        android.widget.ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this.getContext(),
                android.R.layout.simple_spinner_item,
                fishList
        );
        this.fishSpinner.setAdapter(adapter);
        this.fishSpinner.setSelection(adapter.getPosition(""));

        this.fishSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                try {
                    if (adapterView.getSelectedItem() == "") {
                        return;
                    }

                    for (JSONObject item : fishListCurrent) {
                        if (item.optString("fish").equals(adapterView.getSelectedItem())) {
                            return;
                        }
                    }

                    JSONObject curFish = new JSONObject();
                    curFish.put("fish", adapterView.getSelectedItem());
                    curFish.put("count", "1");
                    fishListCurrent.add(curFish);
                    setFishList(fishListCurrent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void getUserFish() {
        resultView.setVisibility(View.GONE);
        message.setVisibility(View.GONE);

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("id", sharedpreferences.getString("id", null))
            )
        );
        Runnable runnable = () -> {
            try {
                fishListCurrent = new ArrayList<>();

                JSONArray list = requests.setRequest(requests.urlRequest + "user/fish", params);
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    if (object.toString().contains("success")) {
                        fishRecycler.post(() -> {
                            useMyFish.setChecked(false);
                            useMyFish.setClickable(false);
                            Toast.makeText(
                                    inflatedView.getContext(),
                                    "Данные о ваших рыбках не заполнены", Toast.LENGTH_SHORT
                            ).show();
                        });
                        return;
                    }

                    fishListCurrent.add(object);
                }
                setFishList(fishListCurrent);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setFishList(List<JSONObject> items) {
        fishRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            fishRecycler.setVisibility(View.VISIBLE);
            fishRecycler.setLayoutManager(layoutManager);

            fishAdapter = new FishListAdapter(inflatedView.getContext(), items, new TextView(this.getContext()));
            fishRecycler.setAdapter(fishAdapter);
        });
    }


}