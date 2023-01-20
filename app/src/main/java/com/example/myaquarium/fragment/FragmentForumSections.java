package com.example.myaquarium.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.ForumItem;
import com.example.myaquarium.R;
import com.example.myaquarium.adapter.ForumThemesAdapter;
import com.example.myaquarium.adapter.ForumThemesListAdapter;
import com.example.myaquarium.server.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentForumSections extends Fragment {
    private View inflatedView;
    private SearchView search;
    private RecyclerView themesRecycler;
    private RecyclerView sectionsFilter;
    private Button filters;
    private CheckBox all;

    private List<List<String>> themesList;
    private List<List<String>> currentThemes;
    private List<String> themesListItems;
    private Map<String, Boolean> checked;

    private boolean check;

    private Requests requests;

    private ForumThemesAdapter themesAdapter;
    private ForumThemesListAdapter themesListAdapter;

    public static FragmentForumSections newInstance() {
        return new FragmentForumSections();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_forum_sections,
                container,
                false
        );
        search = inflatedView.findViewById(R.id.search);
        sectionsFilter = inflatedView.findViewById(R.id.sectionsFilter);
        themesRecycler = inflatedView.findViewById(R.id.themesRecycler);
        filters = inflatedView.findViewById(R.id.filters);
        all = inflatedView.findViewById(R.id.all);

        checked = new HashMap<>();
        themesList = new ArrayList<>();
        currentThemes = new ArrayList<>();

        requests = new Requests();

        this.setStyleSearchView();
        this.getThemesList();

        this.setThemesList(themesList);
        this.setThemesListItems(themesListItems, true);

        filters.setOnClickListener(view -> {
            all.setVisibility(View.VISIBLE);
            if (sectionsFilter.getVisibility() == View.GONE) {
                all.setVisibility(View.VISIBLE);
                sectionsFilter.setVisibility(View.VISIBLE);
            } else if (sectionsFilter.getVisibility() == View.VISIBLE) {
                sectionsFilter.setVisibility(View.GONE);
                all.setVisibility(View.GONE);
            }
        });

        this.searchAction();

        all.setOnClickListener(view -> {
            if (all.isChecked()) {
                check = true;
                for (String item: themesListItems) {
                    checked.put(item, true);
                }
                setThemesListItems(themesListItems, true);
                setThemesList(themesList);
            } else {
                check = false;
                for (String item: themesListItems) {
                    checked.put(item, false);
                }
                setThemesListItems(themesListItems, false);
                setThemesList(new ArrayList<>());
            }
        });

        return inflatedView;
    }


    private void setThemesListItems(List<String> themesListItems, boolean check) {
        sectionsFilter.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            sectionsFilter.setLayoutManager(layoutManager);
            if (check) {
                for (String item: themesListItems) {
                    checked.put(item, true);
                }
            }

            ForumThemesListAdapter.OnThemeClickListener onThemeClickListener = (theme, checkBox) -> {
                List<List<String>> oldThemes = new ArrayList<>();
                currentThemes = new ArrayList<>(themesList);

                if (checkBox.isChecked()) {
                    checked.put(theme, true);
                } else {
                    checked.put(theme, false);
                    all.setChecked(false);
                }
                int count = 0;
                if (checked.size() == themesListItems.size() && !all.isChecked()) {
                    for (Map.Entry<String, Boolean> entry : checked.entrySet()) {
                        if (entry.getValue()) count++;
                    }
                    if (count == checked.size() && check) all.setChecked(true);
                }

                for (List<String> item : themesList) {
                    for (Map.Entry<String, Boolean> entry: checked.entrySet()) {
                        if (item.get(1).equals(entry.getKey()) && !entry.getValue()) {
                            oldThemes.add(item);
                        }
                    }
                }
                currentThemes.removeAll(oldThemes);
                setThemesList(currentThemes);

            };

            themesListAdapter = new ForumThemesListAdapter(
                    inflatedView.getContext(),
                    themesListItems,
                    onThemeClickListener
            );
            sectionsFilter.setAdapter(themesListAdapter);
        });
    }

    private void setThemesList(List<List<String>> themesList) {
        themesRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            themesRecycler.setLayoutManager(layoutManager);

            ForumThemesAdapter.OnThemeClickListener onThemeClickListener = (themes) -> {

                Intent intent = new Intent(inflatedView.getContext(), ForumItem.class);
                intent.putExtra("id", themes);
                this.startActivity(intent);
            };

            themesAdapter = new ForumThemesAdapter(
                    inflatedView.getContext(),
                    themesList,
                    onThemeClickListener
            );
            themesRecycler.setAdapter(themesAdapter);
        });
    }

    private void getThemesList() {
        themesListItems = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                String[] list = requests.setRequest(requests.urlSectionsTitle);
                for (String item : list) {
                    JSONObject object = new JSONObject(item);
                    List<String> sections = new ArrayList<>(List.of(
                            object.getString("id"),
                            object.getString("sections"),
                            object.getString("themes_title"),
                            "автор: " + object.getString("author")
                    ));

                    if (!themesListItems.contains(object.getString("sections"))) {
                        themesListItems.add(object.getString("sections"));
                    }
                    themesList.add(sections);
                }
                this.inflatedView.post(() -> {
                    themesAdapter.notifyDataSetChanged();
                    themesListAdapter.notifyDataSetChanged();
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void searchAction() {
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchThemesByEditText(s, currentThemes.isEmpty() ? themesList : currentThemes);
                return true;
            }
        });
    }

    private void searchThemesByEditText(String search, List<List<String>> themesList) {
        List<List<String>> currentThemes = new ArrayList<>();
        List<List<String>> startThemes = new ArrayList<>(themesList);
        String searchText = search.toLowerCase(Locale.ROOT);
        for (List<String> item: themesList) {
            if(!item.get(2).toLowerCase(Locale.ROOT).contains(searchText)) {
                currentThemes.add(item);
            }
        }
        startThemes.removeAll(currentThemes);
        setThemesList(startThemes);
    }

    private void setStyleSearchView() {
        int id = search.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);

        TextView textView = search.findViewById(id);
        textView.setTextColor(Color.WHITE);
    }


}