package com.example.myaquarium;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.adapter.TipsAdapter;
import com.example.myaquarium.adapter.TipsMenuAdapter;
import com.example.myaquarium.server.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tips extends AppCompatActivity {
    private List<String> tipsMenuList;

    @SuppressLint("StaticFieldLeak")
    static TipsAdapter tipsAdapter;
    private TipsMenuAdapter tipsMenuAdapter;

    static List<List<String>> tipsList;
    static List<List<String>> fullTipsList;

    private Requests requests;

    private RecyclerView tipsMenuRecycler;
    private RecyclerView tipsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        this.setToolbar();

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

        TextView service = findViewById(R.id.service);
        TextView forum = findViewById(R.id.forum);
        TextView profile = findViewById(R.id.profile);

        service.setOnClickListener(view -> {
            this.startActivity(new Intent(this, Service.class));
        });

        forum.setOnClickListener(view -> {
            this.startActivity(new Intent(this, Forum.class));
        });

        profile.setOnClickListener(view -> {
            this.startActivity(new Intent(this, Profile.class));
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView = findViewById(R.id.title);
        textView.setText(getApplicationContext().getString(R.string.tips_text));

        toolbar.setNavigationOnClickListener(view -> {
            this.startActivity(new Intent(this, Service.class));
        });
    }

    private void getTipsMenu() {
        Runnable runnable = () -> {
            try {
                String[] menu = requests.setRequest(requests.urlRequest + "tips/menu");
                for (String item: menu) {
                    JSONObject object = new JSONObject(item);
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
                String[] tips = requests.setRequest(requests.urlRequest + "tips/tips");
                for (String item: tips) {
                    JSONObject object = new JSONObject(item);
                    List<String> itemTips = new ArrayList<>(List.of(
                            object.getString("tips_title_id"),
                            object.getString("title"),
                            object.getString("content"),
                            object.getString("image")
                    ));
                    tipsList.add(itemTips);
                    fullTipsList.add(itemTips);
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

        List<List<String>> filterTips = new ArrayList<>();
        if (tipsMenuId != 0) {
            for (List<String> category: tipsList) {
                if (Integer.parseInt(category.get(0)) == tipsMenuId + 1) {
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