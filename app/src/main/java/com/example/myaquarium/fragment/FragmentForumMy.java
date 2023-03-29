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

import com.example.myaquarium.R;
import com.example.myaquarium.ViewTheme;
import com.example.myaquarium.adapter.ForumThemesAdapter;
import com.example.myaquarium.adapter.ForumUserThemesAdapter;
import com.example.myaquarium.server.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentForumMy extends Fragment {
    private View inflatedView;

    private RecyclerView themesRecycler;
    private RecyclerView likedRecycler;

    private List<JSONObject> themesList;
    private List<JSONObject> likedList;

    private Requests requests;

    private ForumUserThemesAdapter themesAdapter;
    private ForumThemesAdapter likedAdapter;

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

        Button newTheme = inflatedView.findViewById(R.id.newTheme);
        themesRecycler = inflatedView.findViewById(R.id.themesRecycler);
        likedRecycler = inflatedView.findViewById(R.id.likedRecycler);
        TextView showMyThemes = inflatedView.findViewById(R.id.showMyThemes);
        TextView showMyLiked = inflatedView.findViewById(R.id.showMyLiked);

        this.getUserThemes();
        this.setThemesList();

        this.getUserLiked();
        this.setLikedList();

        showMyThemes.setOnClickListener(view -> {
            if (themesRecycler.getVisibility() == View.GONE) {
                themesRecycler.setVisibility(View.VISIBLE);
            } else if (themesRecycler.getVisibility() == View.VISIBLE) {
                themesRecycler.setVisibility(View.GONE);
            }
        });
        showMyLiked.setOnClickListener(view -> {
            if (likedRecycler.getVisibility() == View.GONE) {
                likedRecycler.setVisibility(View.VISIBLE);
            } else if (likedRecycler.getVisibility() == View.VISIBLE) {
                likedRecycler.setVisibility(View.GONE);
            }
        });
        newTheme.setOnClickListener(view -> this.newTheme());

        return inflatedView;
    }

    private void getUserThemes() {
        themesList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "user/forum/themes", new ArrayList<>());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    themesList.add(object);
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

            ForumUserThemesAdapter.OnThemeClickListener onThemeClickListener = (theme, category) -> {
                Intent intent = new Intent(inflatedView.getContext(), ViewTheme.class);
                intent.putExtra("theme", theme.toString());
                intent.putExtra("id", category);
                this.startActivity(intent);
            };

            ForumUserThemesAdapter.OnEditClickListener onEditClickListener = (theme) -> {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.my, FragmentForumMyEditTheme.newInstance(theme));
                ft.commit();
            };

            themesAdapter = new ForumUserThemesAdapter(
                    inflatedView.getContext(),
                    this.themesList,
                    onThemeClickListener,
                    onEditClickListener
            );
            themesRecycler.setAdapter(themesAdapter);
        });
    }

    private void getUserLiked() {
        likedList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                JSONArray list = requests.setRequest(requests.urlRequest + "user/forum/liked", new ArrayList<>());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(list.getJSONObject(i)));
                    likedList.add(object);
                }
                this.inflatedView.post(() -> {
                    likedAdapter.notifyDataSetChanged();
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    private void setLikedList() {
        likedRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            likedRecycler.setLayoutManager(layoutManager);

            ForumThemesAdapter.OnThemeClickListener onThemeClickListener = (theme, category) -> {
                Intent intent = new Intent(inflatedView.getContext(), ViewTheme.class);
                intent.putExtra("theme", theme.toString());
                intent.putExtra("id", category);
                this.startActivity(intent);
            };

            likedAdapter = new ForumThemesAdapter(
                    inflatedView.getContext(),
                    this.likedList,
                    onThemeClickListener
            );
            likedRecycler.setAdapter(likedAdapter);
        });
    }

    private void newTheme() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.my, new FragmentForumNewTheme());
        transaction.addToBackStack(null);

        transaction.commit();
    }
}