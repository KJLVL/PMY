package com.example.myaquarium;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.adapter.FishListAdapter;
import com.example.myaquarium.model.User;
import com.example.myaquarium.service.ImageEditor;
import com.example.myaquarium.service.Navigation;
import com.example.myaquarium.service.Requests;
import com.example.myaquarium.service.UserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.utkala.searchablespinner.SearchableSpinner;

public class Profile extends AppCompatActivity {
    private RecyclerView fishRecycler;
    private SearchableSpinner fishSpinner;
    private TextView downloadDefine;

    private Requests requests;
    private User user;

    private TextView result;
    private TextView volumeField;
    private TextView myFish;
    private ImageView image;
    private LinearLayout linearLayout;
    private LinearLayout layoutResult;
    private Button sendFish;
    private Button clearResults;
    private ProgressBar progressBar;

    private FishListAdapter fishAdapter;

    private List<String> fishList;
    private static List<JSONObject> fishListCurrent;
    private List<String> photoNames;
    private List<String> photoList;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Navigation.setToolbar(
                this,
                getApplicationContext().getString(R.string.profile_text),
                null
        );
        Navigation.setMenuNavigation(this);

        requests = new Requests();
        fishRecycler = this.findViewById(R.id.fishListItems);

        fishSpinner = this.findViewById(R.id.fishSpinner);

        progressBar = this.findViewById(R.id.progressBar);
        result = this.findViewById(R.id.result);

        linearLayout = this.findViewById(R.id.layout);
        layoutResult = this.findViewById(R.id.layoutResult);

        myFish = this.findViewById(R.id.myFish);
        image = this.findViewById(R.id.image);
        volumeField = this.findViewById(R.id.volumeField);
        Button save = this.findViewById(R.id.save);

        fishListCurrent = new ArrayList<>();
        if (fishListCurrent.size() == 0) {
            myFish.setVisibility(View.VISIBLE);
        } else {
            myFish.setVisibility(View.GONE);
        }

        this.getUser();
        this.getUserFish();

        this.getFishList();

        ImageButton settings = findViewById(R.id.settings);
        settings.setOnClickListener(
                view -> this.startActivity(new Intent(this, ProfileSettings.class))
        );

        photoNames = new ArrayList<>();
        photoList = new ArrayList<>();
        downloadDefine = this.findViewById(R.id.downloadDefine);
        sendFish = this.findViewById(R.id.sendFish);
        clearResults = this.findViewById(R.id.clearResults);
        clearResults.setOnClickListener(view -> {
            this.clearResults.setVisibility(View.GONE);
            this.layoutResult.setVisibility(View.GONE);
            this.layoutResult.removeAllViews();
            this.result.setVisibility(View.GONE);
            this.downloadDefine.setVisibility(View.VISIBLE);
        });
        downloadDefine.setOnClickListener(view -> this.setRecognition());
        sendFish.setOnClickListener(view -> this.getRecognitionResult());

        save.setOnClickListener(view -> this.saveProfile());
    }

    private void saveProfile() {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("aquarium_volume", volumeField.getText().toString()),
                new BasicNameValuePair("fish", String.valueOf(new JSONArray(fishListCurrent))),
                new BasicNameValuePair("id", UserData.getUserData(this))
        )
        );

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/profile", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                if (object.optString("success").equals("1")) {
                    this.runOnUiThread(() -> Toast.makeText(
                            getApplicationContext(),
                            "Данные были успешно сохранены", Toast.LENGTH_SHORT
                    ).show());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getUser() {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("id", UserData.getUserData(this))
            )
        );
        Runnable runnable = () -> {
            try {
                this.user = requests.getUser(requests.setRequest(requests.urlRequest + "user", params));

                this.runOnUiThread(() -> {
                    String name = this.user.getUserName() + " " + this.user.getSurname();
                    TextView nameField = this.findViewById(R.id.nameField);
                    nameField.setText(name);
                    volumeField.setText(this.user.getAquariumVolume());

                    Picasso.get()
                            .load(requests.urlRequestImg + this.user.getAvatar())
                            .resize(350, 0)
                            .into(image);
                    image.setOnClickListener(view -> {
                        Intent intent = new Intent(this, ImageViewer.class);
                        intent.putExtra("image", requests.urlRequestImg + this.user.getAvatar());
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
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("id", UserData.getUserData(this))
        )
        );
        Runnable runnable = () -> {
            try {
                JSONArray userFish = requests.setRequest(requests.urlRequest + "user/fish", params);
                for (int i = 0; i < userFish.length(); i++) {
                    JSONObject result = new JSONObject(String.valueOf(userFish.getJSONObject(i)));
                    if (result.optString("success").equals("0")) {
                        myFish.post(() -> myFish.setVisibility(View.VISIBLE));
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
        fishList.add("не выбрано");
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
        this.fishSpinner.setSelection(adapter.getPosition("не выбрано"));

        this.fishSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                try {
                    if (adapterView.getSelectedItem() == "не выбрано") {
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

    private void setRecognition() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    }

    private void getRecognitionResult() {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("images", this.photoList.toString())
            )
        );

        Runnable runnable = () -> {
            try {
                runOnUiThread(() -> {
                    this.progressBar.setVisibility(View.VISIBLE);
                    this.photoList.clear();
                    this.sendFish.setVisibility(View.GONE);
                    this.downloadDefine.setVisibility(View.GONE);
                    this.linearLayout.removeAllViews();
                });

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost http = new HttpPost(this.requests.urlRequest + "recognize");
                http.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                httpclient.execute(http);

                ObjectMapper jsonMapper = new ObjectMapper();
                JsonNode node = jsonMapper.readTree(new URL(this.requests.urlRequest + "report.json"));
                JSONArray fish = new JSONArray("[" + node.get(1) + "]");

                JSONObject object = new JSONObject(String.valueOf(fish.get(0)));
                Map<String, JSONObject> map = new HashMap<>();
                JSONArray names = object.names();
                for (int i = 0; i < (names != null ? names.length() : 0); i++) {
                    JSONObject item = object.getJSONObject(String.valueOf(names.get(i)));
                    map.put(String.valueOf(names.get(i)), item);
                }

                List<String> fishIds = new ArrayList<>();
                for (String key : map.keySet()) {
                    if (String.valueOf(map.get(key)).contains("\":1")) {
                        fishIds.add(key);
                    }
                }

                List<String> fishTextList = new ArrayList<>();
                List<NameValuePair> parameters = new ArrayList<>(List.of(
                        new BasicNameValuePair("fish", fishIds.toString())
                    )
                );
                JSONArray resFish = requests.setRequest(requests.urlRequest + "user/fish/recognize", parameters);
                runOnUiThread(() -> this.progressBar.setVisibility(View.GONE));
                for (int i = 0; i < resFish.length(); i++) {
                    JSONObject result = new JSONObject(String.valueOf(resFish.getJSONObject(i)));
                    if (result.optString("success").equals("0")) {
                        runOnUiThread(() -> {
                            this.clearResults.setVisibility(View.VISIBLE);
                            this.result.setVisibility(View.VISIBLE);
                            this.result.setText(R.string.settings_not_result);
                        });
                        return;
                    }
                    fishTextList.add(result.optString("fish_name"));
                }
                this.setResults(fishTextList);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setResults(List<String> fishTextList) {
        runOnUiThread(() -> {
            this.clearResults.setVisibility(View.VISIBLE);
            this.layoutResult.setVisibility(View.VISIBLE);
            this.result.setVisibility(View.VISIBLE);
            this.result.setText(R.string.settings_yes_result);
            String fish = String.valueOf(fishTextList);
            fish = fish.substring(0, fish.length() - 1).substring(1);
            List<String> fishList = List.of(fish.split(", "));

            for (String item: fishList) {
                TextView fishText = new TextView(this);
                fishText.setText(item);
                fishText.setTextSize(16);
                fishText.setTextColor(getResources().getColor(R.color.black));

                Button button = new Button(this);
                LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
                        70,
                        70
                );
                lpBtn.setMargins(30, 0, 0, 0);
                button.setText("+");
                button.setTextColor(Color.WHITE);
                button.setLayoutParams(lpBtn);
                button.setPadding(0,0,0,0);
                button.setBackgroundResource(R.color.bthAll);

                button.setOnClickListener(view -> {
                    for (int i = 0; i < fishListCurrent.size(); i++) {
                        JSONObject curFish = new JSONObject();

                        if (fishListCurrent.get(i).optString("fish").equals(fishText.getText().toString())) {
                            try {
                                curFish.put("fish", fishText.getText().toString());
                                curFish.put(
                                        "count",
                                        fishListCurrent.get(i).optInt("count") + 1
                                );
                                fishListCurrent.set(i, curFish);
                                setFishList(fishListCurrent);
                                return;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    JSONObject curFish = new JSONObject();
                    try {
                        curFish.put("fish", fishText.getText().toString());
                        curFish.put("count", "1");
                        fishListCurrent.add(curFish);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    setFishList(fishListCurrent);
                });

                LinearLayout layout = new LinearLayout(this);

                layout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                layoutParams.setMargins(0,0,0, 10);
                layout.setLayoutParams(layoutParams);
                layout.setGravity(Gravity.CENTER_VERTICAL);
                layout.addView(fishText);
                layout.addView(button);

                layoutResult.addView(layout);
            }
        });
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK) return;

                Intent data = result.getData();
                Uri uri = data.getData();
                photoNames.add(uri.getLastPathSegment());

                ImageView img = new ImageView(this);
                img.setImageURI(uri);

                BitmapDrawable bd = (BitmapDrawable) img.getDrawable();
                Runnable runnable = () -> {
                    this.bitmap = bd.getBitmap();
                    img.post(() -> this.generateImage(bitmap));

                };
                Thread thread = new Thread(runnable);
                thread.start();

                Button button = ImageEditor.editAddedImage(uri, this, img);
                LinearLayout layout = ImageEditor.editLayoutImage(this, button, img);

                button.setOnClickListener(view -> {
                    layout.removeAllViews();
                    int index = photoNames.indexOf(uri.getLastPathSegment());
                    photoNames.remove(uri.getLastPathSegment());
                    photoList.remove(index);
                    sendFish.setVisibility(this.photoList.size() != 0 ? View.VISIBLE : View.GONE);
                });

                linearLayout.addView(layout);
            });

    private void generateImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            photoList.add(Base64.encodeToString(bytes, Base64.DEFAULT));
            sendFish.setVisibility(this.photoList.size() != 0 ? View.VISIBLE : View.GONE);
        } else {
            photoList.add("");
        }
    }

}