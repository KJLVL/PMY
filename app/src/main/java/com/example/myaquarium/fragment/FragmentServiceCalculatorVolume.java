package com.example.myaquarium.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.Service;
import com.example.myaquarium.adapter.FishListAdapter;
import com.example.myaquarium.adapter.FishListViewAdapter;
import com.example.myaquarium.server.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentServiceCalculatorVolume extends Fragment {
    private View inflatedView;

    private Button calcVolume;

    private RecyclerView listView;
    private RecyclerView fishRecycler;

    private SearchView search;

    private CheckBox useMyFish;

    private TextView volumeText;
    private TextView result;
    private TextView message;

    private Requests requests;

    private FishListAdapter fishAdapter;
    private FishListViewAdapter fishListAdapter;

    private List<List<String>> fishListAll;
    private List<String> fishList;
    private static List<List<String>> fishListCurrent;

    public static FragmentServiceCalculatorVolume newInstance() {
        return new FragmentServiceCalculatorVolume();
    }

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

        listView = inflatedView.findViewById(R.id.listview);
        fishRecycler = inflatedView.findViewById(R.id.fishListItems);
        result = inflatedView.findViewById(R.id.volume);
        volumeText = inflatedView.findViewById(R.id.volumeText);
        message = inflatedView.findViewById(R.id.message);
        useMyFish = inflatedView.findViewById(R.id.useMyFish);
        search = inflatedView.findViewById(R.id.search);
        this.setColorSearch();

        this.getFishList();
        this.searchAction();

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
                this.setFishList(fishListCurrent);
            }
        });

        return inflatedView;
    }

    private void setColorSearch() {
        int id = search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = search.findViewById(id);
        textView.setTextColor(Color.BLACK);
    }

    private void setToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            this.startActivity(new Intent(inflatedView.getContext(), Service.class));
        });

        ActionBar actionBar = ((Service) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setMessage() {
        TextView btnVol = inflatedView.findViewById(R.id.btnVol);
        btnVol.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(inflatedView.getContext());
            dialog.setTitle("Расчет объема аквариума");
            dialog.setMessage("При создании своего собственного водного мира недостаточно просто закупить планируемые составляющие. Необходимо точно рассчитать их количество, чтобы аквариум функционировал долго и стабильно, а обитающие в нем рыбки чувствовали себя комфортно. Калькулятор поможет вам в этом! Просто введите указанные данные!");
            dialog.setPositiveButton("Закрыть", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            dialog.show();
        });
    }

    private void searchAction() {
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchFishByEditText(s);
                return true;
            }
        });
    }

    private void calculateVolume() {
        calcVolume.setOnClickListener(view -> {
            if (calculateFishByList(fishListCurrent) == 0) {
                result.setVisibility(View.GONE);
                volumeText.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                Toast.makeText(
                        inflatedView.getContext(),
                        "Выберите хотя бы 1 рыбку", Toast.LENGTH_SHORT
                ).show();
                return;
            }
            result.setText(calculateFishByList(fishListCurrent) + " л.");
            result.setVisibility(View.VISIBLE);
            volumeText.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
        });
    }

    private int calculateFishByList(List<List<String>> fishListCurrent) {
        int volume = 0;

        for (List<String> currentFish : fishListCurrent) {
            String fishName = currentFish.get(0);
            int fishCount = Integer.parseInt(currentFish.get(1));

            for (List<String> fish : fishListAll) {
                if (fish.get(0).equals(fishName)) {
                    volume += fishCount * Integer.parseInt(fish.get(1));
                    break;
                }
            }
        }

        return volume;
    }

    private void searchFishByEditText(String search) {
        List<String> currentFish = new ArrayList<>();
        List<String> startFish = new ArrayList<>(fishList);
        String searchText = search.toLowerCase(Locale.ROOT);
        for (String item : fishList) {
            if (!item.toLowerCase(Locale.ROOT).contains(searchText)) {
                currentFish.add(item);
            }
        }
        startFish.removeAll(currentFish);
        setSelectedFishList(startFish);
    }

    private void setSelectedFishList(List<String> items) {
        listView.post(() -> {
            RecyclerView fishRecycler = inflatedView.findViewById(R.id.listview);
            FishListViewAdapter.OnFishClickListener onFishClickListener = (fish) -> {
                for (List<String> item : fishListCurrent) {
                    if (item.get(0).equals(fish)) {
                        return;
                    }
                }
                List<String> curFish = new ArrayList<>(List.of(fish, "1"));
                fishListCurrent.add(curFish);
                setFishList(fishListCurrent);
            };
            fishListAdapter
                    = new FishListViewAdapter(inflatedView.getContext(), items, onFishClickListener);
            fishRecycler.setAdapter(fishListAdapter);
        });
    }

    private void getFishList() {
        fishListAll = new ArrayList<>();
        fishList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "fish/list", new ArrayList<>());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    List<String> fish = new ArrayList<>(List.of(
                            object.getString("fish_name"),
                            object.getString("liter")

                    ));
                    fishList.add(object.getString("fish_name"));
                    fishListAll.add(fish);
                }
                setSelectedFishList(fishList);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getUserFish() {
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "user/fish", new ArrayList<>());
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

                    List<String> fish = new ArrayList<>(List.of(
                            object.getString("fish"),
                            object.getString("count")

                    ));
                    fishListCurrent.add(fish);
                }
                setFishList(fishListCurrent);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setFishList(List<List<String>> items) {
        fishRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            fishRecycler.setVisibility(View.VISIBLE);
            fishRecycler.setLayoutManager(layoutManager);

            fishAdapter = new FishListAdapter(inflatedView.getContext(), items);
            fishRecycler.setAdapter(fishAdapter);
        });
    }


}