package com.example.myaquarium.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.myaquarium.Forum;
import com.example.myaquarium.R;
import com.example.myaquarium.server.Requests;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.utkala.searchablespinner.SearchableSpinner;

public class FragmentForumNewTheme extends Fragment {
    private View inflatedView;

    private RadioGroup category;
    private TextView sectionText;
    private AppCompatCheckBox viewMyPhone;
    private RadioGroup section;
    private SearchableSpinner spinner;
    private TextView title;
    private TextView description;
    private LinearLayout theme;
    private Button create;
    private Button addPhoto;
    private LinearLayout linearLayout;

    private List<Map<String, String>> categoryList;
    private List<Map<String, String>> sectionsList;
    private JSONObject userInfo;

    private Requests requests;
    private String categoryId;
    private String sectionId;
    private List<String> photoNames;
    private List<String> photoList;
    private Bitmap bitmap;
    private int countPhoto = 0;

    public static FragmentForumNewTheme newInstance() {
        return new FragmentForumNewTheme();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_forum_new_theme,
                container,
                false
        );

        this.setToolbar();

        category = inflatedView.findViewById(R.id.category);
        viewMyPhone = inflatedView.findViewById(R.id.myPhone);
        spinner = inflatedView.findViewById(R.id.spinner);
        sectionText = inflatedView.findViewById(R.id.sectionText);
        section = inflatedView.findViewById(R.id.section);
        title = inflatedView.findViewById(R.id.title);
        description = inflatedView.findViewById(R.id.description);
        theme = inflatedView.findViewById(R.id.theme);
        create = inflatedView.findViewById(R.id.create);
        addPhoto = inflatedView.findViewById(R.id.addPhoto);
        linearLayout = inflatedView.findViewById(R.id.layout);

        this.getCities();

        requests = new Requests();
        photoNames = new ArrayList<>();
        photoList = new ArrayList<>();

        this.getCategoryList();
        this.setCategoryList();

        category.setOnCheckedChangeListener((group, checkedId) -> {
            this.categoryId = String.valueOf(checkedId);
            section.removeAllViews();
            this.getSections(checkedId);
            this.setSections();
        });

        section.setOnCheckedChangeListener((group, checkedId) -> {
            this.sectionId = String.valueOf(checkedId);
            theme.setVisibility(View.VISIBLE);
        });

        addPhoto.setOnClickListener(view -> this.addPhoto());

        create.setOnClickListener(view -> this.checkTheme());

        return inflatedView;
    }

    private void getCities() {
        List<String> cities = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "city", new ArrayList<>());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(result.getJSONObject(i)));
                    cities.add(object.optString("city"));
                }

                JSONArray user = requests.setRequest(requests.urlRequest + "user", new ArrayList<>());
                userInfo = new JSONObject(user.getJSONObject(0).toString());

                inflatedView.post(() -> {
                    android.widget.ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            inflatedView.getContext(),
                            android.R.layout.simple_spinner_item,
                            cities
                    );
                    this.spinner.setAdapter(adapter);
                    if (!userInfo.optString("city").equals("null"))
                        this.spinner.setSelection(adapter.getPosition(userInfo.optString("city")));
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            this.startActivity(new Intent(inflatedView.getContext(), Forum.class));
        });

        ActionBar actionBar = ((Forum)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void getCategoryList() {
        categoryList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "themes/category", new ArrayList<>());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    Map<String, String> map = Map.of(
                            object.getString("id"),
                            object.getString("title")
                    );
                    categoryList.add(map);
                }
                this.setCategoryList();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setCategoryList() {
        this.category.post(() -> {
            for (Map<String, String> item : categoryList) {
                for (Map.Entry<String, String> entry : item.entrySet()) {
                    RadioButton radioButton = new RadioButton(inflatedView.getContext());
                    radioButton.setId(Integer.parseInt(entry.getKey()));
                    radioButton.setText(entry.getValue());
                    radioButton.setTextColor(getResources().getColor(R.color.black));
                    category.addView(radioButton);
                }
            }
        });

    }

    private void getSections(int id) {
        sectionText.setVisibility(View.VISIBLE);
        section.setVisibility(View.VISIBLE);
        this.sectionsList = new ArrayList<>();

        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("category_id", String.valueOf(id))
            )
        );
        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "themes/sections", params);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(result.getJSONObject(i)));
                    Map<String, String> sections = Map.of(
                            object.getString("id"),
                            object.getString("title")
                    );
                    sectionsList.add(sections);
                }
                this.setSections();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setSections() {
        this.section.post(() -> {
            for (Map<String, String> item : sectionsList) {
                for (Map.Entry<String, String> entry : item.entrySet()) {
                    RadioButton radioButton = new RadioButton(inflatedView.getContext());
                    radioButton.setId(Integer.parseInt(entry.getKey()));
                    radioButton.setText(entry.getValue());
                    radioButton.setTextColor(getResources().getColor(R.color.black));
                    section.addView(radioButton);
                }
            }
        });
    }

    private void addPhoto() {
        if (countPhoto < 3) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(intent);
        } else {
            Toast.makeText(
                    inflatedView.getContext(),
                    "Вы не можете добавить более 3-х фотографий", Toast.LENGTH_SHORT
            ).show();
        }
    }

    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    countPhoto++;
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    photoNames.add(uri.getLastPathSegment());

                    ImageView img = new ImageView(inflatedView.getContext());
                    img.setImageURI(uri);

                    BitmapDrawable bd = (BitmapDrawable) img.getDrawable();
                    Runnable runnable = () -> {
                        this.bitmap = bd.getBitmap();
                        img.post(() -> this.generateImage(bitmap));

                    };
                    Thread thread = new Thread(runnable);
                    thread.start();

                    Picasso.get().load(uri).into(img);
                    img.setRotation(90);

                    LinearLayout layout = new LinearLayout(inflatedView.getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                    );

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            200,
                            200
                    );

                    Button button = new Button(inflatedView.getContext());
                    LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            70
                    );

                    lpBtn.setMargins(30, 0, 0, 0);
                    button.setText("удалить");
                    button.setTextSize(10);
                    button.setTextColor(Color.WHITE);
                    button.setLayoutParams(lpBtn);
                    button.setPadding(0,0,0,0);
                    button.setBackgroundResource(R.color.bthAll);
                    button.setOnClickListener(view -> {
                        layout.removeAllViews();
                        int index = photoNames.indexOf(uri.getLastPathSegment());
                        photoNames.remove(uri.getLastPathSegment());
                        photoList.remove(index);
                        countPhoto--;
                    });

                    img.setLayoutParams(lp);
                    layout.setGravity(Gravity.CENTER_VERTICAL);
                    layout.addView(img);
                    layout.addView(button);
                    linearLayout.addView(layout);
                }
            });

    private void generateImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            photoList.add(Base64.encodeToString(bytes, Base64.DEFAULT));
        } else {
            photoList.add("");
        }
    }

    private void checkTheme() {
        if (title.getText().toString().equals("") || description.getText().toString().equals("")) {
            Toast.makeText(
                    inflatedView.getContext(),
                    "Введите название и описание!", Toast.LENGTH_SHORT
            ).show();
            return;
        }

        this.createTheme();
    }

    private void createTheme() {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("category_id", this.categoryId),
                new BasicNameValuePair("sections_id", this.sectionId),
                new BasicNameValuePair("title", title.getText().toString()),
                new BasicNameValuePair("city", spinner.getSelectedItem().toString()),
                new BasicNameValuePair("phone", viewMyPhone.isChecked() ? userInfo.optString("phone") : ""),
                new BasicNameValuePair("content", description.getText().toString())
        ));

        if (!photoNames.isEmpty()) {
            params.add(new BasicNameValuePair("photoNames", photoNames.toString()));
            params.add(new BasicNameValuePair("photo", photoList.toString()));
        }

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/forum", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                if (object.optString("success").equals("1")) {
                    inflatedView.post(() -> {
                        Toast.makeText(
                                inflatedView.getContext(),
                                "Тема была успешно создана!", Toast.LENGTH_SHORT
                        ).show();
                        this.startActivity(new Intent(getContext(), Forum.class));
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}