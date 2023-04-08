package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.myaquarium.fragment.FragmentForumViewTheme;
import com.example.myaquarium.model.Theme;
import com.example.myaquarium.service.Navigation;
import com.example.myaquarium.service.Requests;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewTheme extends AppCompatActivity {
    private Theme theme;
    private Requests requests;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_theme);
        Navigation.setMenuNavigation(this);

        this.setToolbar();
        requests = new Requests();

        Bundle arguments = getIntent().getExtras();
        this.id = arguments.get("id").toString();
        this.getThemeById(arguments.getString("themeId"));
    }

    private void getThemeById(String themeId) {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("theme_id", themeId)
            )
        );
        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "themes/getTheme", params);
                this.theme = requests.getTheme(result.getJSONObject(0));
                runOnUiThread(() -> {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.scrollViewTheme, FragmentForumViewTheme.newInstance(theme, id));
                    ft.commit();
                });
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

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView = findViewById(R.id.title);
        textView.setText(getApplicationContext().getString(R.string.forum_text));

        toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(this, Forum.class);
            intent.putExtra("id", id);
            this.startActivity(intent);
        });
    }
}