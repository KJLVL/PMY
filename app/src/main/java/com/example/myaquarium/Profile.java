package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.adapter.FishListAdapter;
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
import java.util.Map;

import pl.utkala.searchablespinner.SearchableSpinner;

public class Profile extends AppCompatActivity {
    private RecyclerView fishRecycler;
    private SearchableSpinner fishSpinner;

    private Requests requests;

    private TextView nameField;
    private TextView volumeField;
    private TextView myFish;
    private ImageView image;

    private FishListAdapter fishAdapter;

    private Map<String, String> userInfo;
    private List<String> fishList;
    private static List<JSONObject> fishListCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.setToolbar();

        requests = new Requests();
        fishRecycler = this.findViewById(R.id.fishListItems);

        fishSpinner = this.findViewById(R.id.fishSpinner);

        nameField = this.findViewById(R.id.nameField);
        myFish = this.findViewById(R.id.myFish);
        image = this.findViewById(R.id.image);
        volumeField = this.findViewById(R.id.volumeField);
        Button save = this.findViewById(R.id.save);

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
                    image.setOnClickListener(view -> {
                        Intent intent = new Intent(this, ImageViewer.class);
                        intent.putExtra("image",  requests.urlRequestImg + userInfo.get("avatar"));
                        intent.putExtra("class", "Profile");
                        startActivity(intent);
                    });

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
        fishList = new ArrayList<>();
        fishList.add("");
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "fish/list", new ArrayList<>());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    fishList.add(object.getString("fish_name"));
                }
                runOnUiThread(() -> this.setSpinnerActions(fishList));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setSpinnerActions(List<String> fishList) {
        android.widget.ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
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