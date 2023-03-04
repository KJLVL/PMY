package com.example.myaquarium;

import android.content.Intent;
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
    private ImageView image;
    private Button save;

    private FishListAdapter fishAdapter;
    private FishListViewAdapter fishListAdapter;

    private List<List<String>> fishListAll;
    public static Map<String, String> userInfo;
    private List<String> fishList;
    private static List<List<String>> fishListCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.setToolbar();

        requests = new Requests();

        listView = this.findViewById(R.id.listview);
        fishRecycler = this.findViewById(R.id.fishListItems);
        search = this.findViewById(R.id.search);
        nameField = this.findViewById(R.id.nameField);
        image = this.findViewById(R.id.image);
        volumeField = this.findViewById(R.id.volumeField);
        save = this.findViewById(R.id.save);

        userInfo = new HashMap<>();
//
        this.getUser();

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

    private void saveProfile() {
        Users user = Users.findById(Users.class, SignIn.user.get(0).getId());
        user.aquarium_volume = volumeField.getText().toString();
        user.save();
        SignIn.user.get(0).aquarium_volume = volumeField.getText().toString();
        Toast.makeText(
                getApplicationContext(),
                "Данные были успешно сохранены", Toast.LENGTH_SHORT
        ).show();

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
        userInfo.put("user_name", SignIn.user.get(0).user_name);
        userInfo.put("login", SignIn.user.get(0).login);

        userInfo.put("surname", SignIn.user.get(0).surname != null
                ? SignIn.user.get(0).surname : "");
        userInfo.put("aquarium_volume", SignIn.user.get(0).aquarium_volume != null
                ? SignIn.user.get(0).aquarium_volume : "");
        userInfo.put("avatar", SignIn.user.get(0).avatar != null
                ? SignIn.user.get(0).avatar : "");

        String name = userInfo.get("user_name") + " " + userInfo.get("surname");
        nameField.setText(name);
        volumeField.setText(userInfo.get("aquarium_volume"));
        Picasso.get()
                .load(R.drawable.noavatar)
                .into(image);
    }

    private void getUserFish() {
        Runnable runnable = () -> {
            try {
                JSONArray userFish = requests.setRequest(requests.urlRequest + "user/fish", new ArrayList<>());
                for (int i = 0; i < userFish.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(userFish.getJSONObject(i)));
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

    private void setSelectedFishList(List<String> items) {
        listView.post(() -> {
            RecyclerView fishRecycler = this.findViewById(R.id.listview);
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
                    = new FishListViewAdapter(this, items, onFishClickListener);
            fishRecycler.setAdapter(fishListAdapter);
        });
    }

    private void setFishList(List<List<String>> items) {
        fishRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    this,
                    RecyclerView.VERTICAL,
                    false
            );
            fishRecycler.setVisibility(View.VISIBLE);
            fishRecycler.setLayoutManager(layoutManager);

            fishAdapter = new FishListAdapter(this, items);
            fishRecycler.setAdapter(fishAdapter);
        });
    }

}