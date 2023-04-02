package com.example.myaquarium;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.adapter.TipsAdapter;
import com.example.myaquarium.adapter.TipsMenuAdapter;
import com.example.myaquarium.service.Navigation;
import com.example.myaquarium.service.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tips extends AppCompatActivity {
    private List<String> tipsMenuList;

    @SuppressLint("StaticFieldLeak")
    static TipsAdapter tipsAdapter;
    private TipsMenuAdapter tipsMenuAdapter;

    static List<JSONObject> tipsList;
    static List<JSONObject> fullTipsList;

    private Requests requests;

    private RecyclerView tipsMenuRecycler;
    private RecyclerView tipsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        Navigation.setToolbar(
                this,
                getApplicationContext().getString(R.string.tips_text),
                Service.class
        );
        Navigation.setMenuNavigation(this);

        fullTipsList = new ArrayList<>();
        tipsMenuRecycler = findViewById(R.id.tipsMenu);
        tipsRecycler = findViewById(R.id.tipsRecycler);

        requests = new Requests();
        tipsMenuList = new ArrayList<>();
        tipsList = new ArrayList<>();

        this.setCategoryRecycler();
        this.setTipsRecycler();

        this.getTipsMenu();
        this.getTips();
    }

    private void getTipsMenu() {
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "tips/menu", new ArrayList<>());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    tipsMenuList.add(object.getString("title"));
                }
                this.runOnUiThread(() -> tipsMenuAdapter.notifyDataSetChanged());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getTips() {
        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "tips/tips", new ArrayList<>());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(result.getJSONObject(i)));
                    tipsList.add(object);
                    fullTipsList.add(object);
                }
                this.runOnUiThread(() -> tipsAdapter.notifyDataSetChanged());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setTipsRecycler() {
        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        tipsRecycler.setLayoutManager(layoutManager);
        tipsAdapter = new TipsAdapter(this, tipsList);
        tipsRecycler.setAdapter(tipsAdapter);
    }

    private void setCategoryRecycler() {
        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        tipsMenuRecycler.setLayoutManager(layoutManager);
        tipsMenuAdapter = new TipsMenuAdapter(this, tipsMenuList);
        tipsMenuRecycler.setAdapter(tipsMenuAdapter);
    }

    public static void showTipsByCategory(int tipsMenuId) {
        tipsList.clear();
        tipsList.addAll(fullTipsList);

        List<JSONObject> filterTips = new ArrayList<>();
        if (tipsMenuId != 0) {
            for (JSONObject category: tipsList) {
                if (Integer.parseInt(category.optString("tips_title_id")) == tipsMenuId + 1) {
                    filterTips.add(category);
                }
            }
        }
        else {
            filterTips.addAll(tipsList);
        }

        tipsList.clear();
        tipsList.addAll(filterTips);
        tipsAdapter.notifyDataSetChanged();
    }

}