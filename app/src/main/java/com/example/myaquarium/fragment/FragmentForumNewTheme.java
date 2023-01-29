package com.example.myaquarium.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myaquarium.R;
import com.example.myaquarium.server.Requests;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentForumNewTheme extends Fragment {
    private View inflatedView;

    private RadioGroup category;
    private TextView sectionText;
    private RadioGroup section;
    private TextView title;
    private TextView description;
    private LinearLayout theme;
    private Button create;

    private List<Map<String, String>> categoryList;
    private List<Map<String, String>> sectionsList;

    private Requests requests;
    private String categoryId;
    private String sectionId;

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

        category = inflatedView.findViewById(R.id.category);
        sectionText = inflatedView.findViewById(R.id.sectionText);
        section = inflatedView.findViewById(R.id.section);
        title = inflatedView.findViewById(R.id.title);
        description = inflatedView.findViewById(R.id.description);
        theme = inflatedView.findViewById(R.id.theme);
        create = inflatedView.findViewById(R.id.create);

        requests = new Requests();

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

        create.setOnClickListener(view -> this.checkTheme());

        return inflatedView;
    }

    private void getCategoryList() {
        categoryList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                String[] list = requests.setRequest(requests.urlRequest + "themes/category");
                for (String item : list) {
                    JSONObject object = new JSONObject(item);
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
                    category.addView(radioButton);
                }
            }
        });

    }

    private void getSections(int id) {
        sectionText.setVisibility(View.VISIBLE);
        section.setVisibility(View.VISIBLE);
        this.sectionsList = new ArrayList<>();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(requests.urlRequest + "themes/sections");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("category_id", String.valueOf(id)));

        Runnable runnable = () -> {
            try {
                http.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                HttpResponse httpResponse = httpclient.execute(http);
                HttpEntity httpEntity = httpResponse.getEntity();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpEntity.getContent(), StandardCharsets.UTF_8),
                        8
                );
                StringBuilder stringBuilder = new StringBuilder();
                while (bufferedReader.readLine() != null) {
                    stringBuilder.append(bufferedReader.readLine());
                }

                String result = stringBuilder.toString().replaceAll("\\[", "");
                result = result.replaceAll("]", "");

                String[] list = result.split(",(?![\" ])");
                for (String item : list) {
                    JSONObject object = new JSONObject(item);
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
                    section.addView(radioButton);
                }
            }
        });
    }

    private void checkTheme() {
        if (title.getText().toString().equals("") || description.getText().toString().equals("")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(inflatedView.getContext());
            dialog.setTitle("Ошибка");
            dialog.setMessage("Введите название и описание!");

            dialog.setPositiveButton(
                    "Закрыть",
                    (dialogInterface, i) -> dialogInterface.dismiss()
            );
            dialog.show();
            return;
        }

        this.createTheme();
    }

    private void createTheme() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(requests.urlRequest + "user/forum");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("category_id", this.categoryId));
        params.add(new BasicNameValuePair("sections_id", this.sectionId));
        params.add(new BasicNameValuePair("title", title.getText().toString()));
        params.add(new BasicNameValuePair("content", description.getText().toString()));

        Runnable runnable = () -> {
            try {
                http.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                HttpResponse httpResponse = httpclient.execute(http);
                HttpEntity httpEntity = httpResponse.getEntity();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpEntity.getContent(), StandardCharsets.UTF_8),
                        8
                );
                StringBuilder stringBuilder = new StringBuilder();
                while (bufferedReader.readLine() != null) {
                    stringBuilder.append(bufferedReader.readLine());
                }

                JSONObject object = new JSONObject(stringBuilder.toString());
                String success = object.getString("success");
                if (success.equals("1")) {
                    inflatedView.post(() -> {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(inflatedView.getContext());
                        dialog.setMessage("Тема была успешно создана!");
                        dialog.setPositiveButton(
                                "Закрыть",
                                (dialogInterface, i) -> dialogInterface.dismiss()
                        );
                        dialog.show();
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.new_theme, new FragmentForumMy());
                        transaction.commit();
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