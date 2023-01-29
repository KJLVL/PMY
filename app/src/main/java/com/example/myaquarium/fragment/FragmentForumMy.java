package com.example.myaquarium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.ForumItem;
import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.ForumUserThemesAdapter;
import com.example.myaquarium.server.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentForumMy extends Fragment {
    private View inflatedView;

    private Button newTheme;
    private RecyclerView themesRecycler;
    private TextView showMyThemes;

    private List<List<String>> themesList;

    private Requests requests;

    private ForumUserThemesAdapter themesAdapter;

    public static FragmentForumMy newInstance() {
        return new FragmentForumMy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_forum_my,
                container,
                false
        );
        requests = new Requests();

        newTheme = inflatedView.findViewById(R.id.newTheme);
        themesRecycler = inflatedView.findViewById(R.id.themesRecycler);
        showMyThemes = inflatedView.findViewById(R.id.showMyThemes);

        this.getUserThemes();
        this.setThemesList();

        showMyThemes.setOnClickListener(view -> {
            if (themesRecycler.getVisibility() == View.GONE) {
                themesRecycler.setVisibility(View.VISIBLE);
            } else if (themesRecycler.getVisibility() == View.VISIBLE) {
                themesRecycler.setVisibility(View.GONE);
            }
        });

        newTheme.setOnClickListener(view -> this.newTheme());

        return inflatedView;
    }

    private void getUserThemes() {
        themesList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                String[] list = requests.setRequest(requests.urlRequest + "user/forum/themes");
                for (String item : list) {
                    JSONObject object = new JSONObject(item);
                    List<String> sections = new ArrayList<>(List.of(
                            object.getString("id"),
                            object.getString("sections"),
                            object.getString("title"),
                            "автор: " + object.getString("author"),
                            "дата: " + object.getString("date"),
                            object.getString("city")
                    ));
                    themesList.add(sections);
                }
                this.inflatedView.post(() -> {
                    themesAdapter.notifyDataSetChanged();
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setThemesList() {
        themesRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            themesRecycler.setLayoutManager(layoutManager);

            ForumUserThemesAdapter.OnThemeClickListener onThemeClickListener = (themes) -> {

                Intent intent = new Intent(inflatedView.getContext(), ForumItem.class);
                intent.putExtra("id", themes);
                this.startActivity(intent);
            };

            themesAdapter = new ForumUserThemesAdapter(
                    inflatedView.getContext(),
                    this.themesList,
                    onThemeClickListener
            );
            themesRecycler.setAdapter(themesAdapter);
        });
    }

    private void newTheme() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.my, new FragmentForumNewTheme());
        transaction.addToBackStack(null);

        transaction.commit();
    }
}