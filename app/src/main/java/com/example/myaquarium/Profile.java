package com.example.myaquarium;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.adapter.FishListAdapter;
import com.example.myaquarium.adapter.FishListViewAdapter;
import com.example.myaquarium.server.Requests;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private RecyclerView listView;
    private RecyclerView fishRecycler;

    private SearchView search;

    private Requests requests;

    private TextView nameField;
    private TextView volumeField;
    private TextView myFish;
    private ImageView image;
    private Button save;

    private FishListAdapter fishAdapter;
    private FishListViewAdapter fishListAdapter;

    private List<JSONObject> fishListAll;
    private Map<String, String> userInfo;
    private List<String> fishList;
    private static List<JSONObject> fishListCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.setToolbar();

        requests = new Requests();

        listView = this.findViewById(R.id.listview);
        fishRecycler = this.findViewById(R.id.fishListItems);
        search = this.findViewById(R.id.search);
        this.setColorSearch();

        nameField = this.findViewById(R.id.nameField);
        myFish = this.findViewById(R.id.myFish);
        image = this.findViewById(R.id.image);
        volumeField = this.findViewById(R.id.volumeField);
        save = this.findViewById(R.id.save);

        fishListCurrent = new ArrayList<>();
        userInfo = new HashMap<>();
        if (fishListCurrent.size() == 0) {
            myFish.setVisibility(View.VISIBLE);
        } else {
            myFish.setVisibility(View.GONE);
        }

        this.getUser();
        this.getUserFish();

        this.getFishList();
        this.searchAction();

        ImageButton settings = findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            this.startActivity(new Intent(this, ProfileSettings.class));
        });

        save.setOnClickListener(view -> this.saveProfile());

        TextView calculator = findViewById(R.id.service);
        TextView profile = findViewById(R.id.profile);
        TextView forum = findViewById(R.id.forum);

        calculator.setOnClickListener(view -> this.startActivity(new Intent(this, Service.class)));
        forum.setOnClickListener(view -> this.startActivity(new Intent(this, Forum.class)));
        profile.setOnClickListener(view -> this.startActivity(new Intent(this, Profile.class)));
    }

    private void setColorSearch() {
        int id = search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = search.findViewById(id);
        textView.setTextColor(Color.BLACK);
    }

    private void saveProfile() {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("aquarium_volume", volumeField.getText().toString()),
                new BasicNameValuePair("fish", String.valueOf(new JSONArray(fishListCurrent)))
            )
        );

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/profile", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                if (object.optString("success").equals("1")) {
                    this.runOnUiThread(() -> {
                        Toast.makeText(
                                getApplicationContext(),
                                "Данные были успешно сохранены", Toast.LENGTH_SHORT
                        ).show();
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView textView = findViewById(R.id.title);
        textView.setText(getApplicationContext().getString(R.string.profile_text));
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

    private void getUser() {
        Runnable runnable = () -> {
            try {
                JSONArray user = requests.setRequest(requests.urlRequest + "user", new ArrayList<>());
                JSONObject object = new JSONObject(user.getJSONObject(0).toString());
                requests.setUser(object);
                userInfo.put("name", object.getString("user_name"));
                userInfo.put("login", object.getString("login"));

                userInfo.put("surname", !object.getString("surname").equals("null")
                        ? object.getString("surname") : "");

                userInfo.put("aquarium_volume", !object.getString("aquarium_volume").equals("null")
                        ? object.getString("aquarium_volume") : "");

                userInfo.put("avatar", !object.getString("avatar").equals("null")
                        ? object.getString("avatar") : "");

                this.runOnUiThread(() -> {
                    String name = userInfo.get("name") + " " + userInfo.get("surname");
                    nameField.setText(name);
                    volumeField.setText(userInfo.get("aquarium_volume"));

                    Picasso.get()
                            .load(requests.urlRequestImg + userInfo.get("avatar"))
                            .resize(350, 0)
                            .into(image);

                });
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
                JSONArray userFish = requests.setRequest(requests.urlRequest + "user/fish", new ArrayList<>());
                for (int i = 0; i < userFish.length(); i++) {
                    JSONObject result = new JSONObject(String.valueOf(userFish.getJSONObject(i)));
                    if (result.optString("success").equals("0")) {
                        myFish.post(() -> {
                            myFish.setVisibility(View.VISIBLE);
                        });
                        return;
                    }
                    fishListCurrent.add(result);
                }
                setFishList(fishListCurrent);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getFishList() {
        fishListAll = new ArrayList<>();
        fishList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "fish/list", new ArrayList<>());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    fishList.add(object.getString("fish_name"));
                    fishListAll.add(object);
                }
                setSelectedFishList(fishList);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setSelectedFishList(List<String> items) {
        listView.post(() -> {
            RecyclerView fishRecycler = this.findViewById(R.id.listview);
            FishListViewAdapter.OnFishClickListener onFishClickListener = (fish) -> {
                for (JSONObject item : fishListCurrent) {
                    if (item.optString("fish").equals(fish)) {
                        return;
                    }
                }

                JSONObject curFish = new JSONObject();
                curFish.put("fish", fish);
                curFish.put("count", "1");
                fishListCurrent.add(curFish);
                setFishList(fishListCurrent);
            };
            fishListAdapter
                    = new FishListViewAdapter(this, items, onFishClickListener);
            fishRecycler.setAdapter(fishListAdapter);
        });
    }

    private void setFishList(List<JSONObject> items) {
        fishRecycler.post(() -> {
            if (fishListCurrent.size() == 0) {
                myFish.setVisibility(View.VISIBLE);
            } else {
                myFish.setVisibility(View.GONE);
            }
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    this,
                    RecyclerView.VERTICAL,
                    false
            );
            fishRecycler.setVisibility(View.VISIBLE);
            fishRecycler.setLayoutManager(layoutManager);

            fishAdapter = new FishListAdapter(this, items, myFish);
            fishRecycler.setAdapter(fishAdapter);
        });
    }

}