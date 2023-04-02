package com.example.myaquarium.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.ViewTheme;
import com.example.myaquarium.adapter.ForumThemesAdapter;
import com.example.myaquarium.adapter.ForumThemesListAdapter;
import com.example.myaquarium.service.Requests;

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

public class FragmentForumSections extends Fragment {
    private View inflatedView;

    private SearchView search;
    private RecyclerView themesRecycler;
    private RecyclerView sectionsFilter;
    private CheckBox all;
    private LinearLayout filter;

    private List<JSONObject> themesList;
    private List<JSONObject> currentThemes;
    private List<String> themesListItems;
    private Map<String, Boolean> checked;

    private final int id;

    private Requests requests;

    private ForumThemesAdapter themesAdapter;
    private ForumThemesListAdapter themesListAdapter;

    public static FragmentForumSections newInstance(int id) {
        return new FragmentForumSections(id);
    }

    public FragmentForumSections(int id) {
        this.id = id;
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
        this.setColorSearch();

        sectionsFilter = inflatedView.findViewById(R.id.sectionsFilter);
        themesRecycler = inflatedView.findViewById(R.id.themesRecycler);
        Button filters = inflatedView.findViewById(R.id.filters);
        all = inflatedView.findViewById(R.id.all);
        filter = inflatedView.findViewById(R.id.filter);

        checked = new HashMap<>();
        themesList = new ArrayList<>();
        currentThemes = new ArrayList<>();

        requests = new Requests();

        this.getThemesList();

        this.setThemesList(themesList);
        this.setThemesListItems(themesListItems, true);

        filters.setOnClickListener(view -> {
            if (filter.getVisibility() == View.GONE) {
                filter.setVisibility(View.VISIBLE);
            } else if (filter.getVisibility() == View.VISIBLE) {
                filter.setVisibility(View.GONE);
            }
        });

        this.searchAction();

        all.setOnClickListener(view -> {
            if (all.isChecked()) {
                setThemesListItems(themesListItems, true);
                setThemesList(themesList);
            } else {
                setThemesListItems(themesListItems, false);
                setThemesList(new ArrayList<>());
            }
        });

        return inflatedView;
    }

    private void setColorSearch() {
        int id = search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = search.findViewById(id);
        textView.setTextColor(Color.BLACK);
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
            } else {
                for (String item: themesListItems) {
                    checked.put(item, false);
                }
            }

            ForumThemesListAdapter.OnThemeClickListener onThemeClickListener = (theme, checkBox) -> {
                List<JSONObject> oldThemes = new ArrayList<>();
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

                for (JSONObject item : themesList) {
                    for (Map.Entry<String, Boolean> entry: checked.entrySet()) {
                        if (item.optString("sections").equals(entry.getKey()) && !entry.getValue()) {
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

    private void setThemesList(List<JSONObject> themesList) {
        themesRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            themesRecycler.setLayoutManager(layoutManager);

            ForumThemesAdapter.OnThemeClickListener onThemeClickListener = (theme, category) -> {

                Intent intent = new Intent(inflatedView.getContext(), ViewTheme.class);
                intent.putExtra("theme", theme.toString());
                intent.putExtra("id", category);
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
        themesList = new ArrayList<>();

        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("category_id", String.valueOf(id))
            )
        );
        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "themes", params);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(result.getJSONObject(i)));
                    if (!themesListItems.contains(object.getString("sections"))) {
                        themesListItems.add(object.getString("sections"));
                    }
                    themesList.add(object);
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

    private void searchThemesByEditText(String search, List<JSONObject> themesList) {
        List<JSONObject> currentThemes = new ArrayList<>();
        List<JSONObject> startThemes = new ArrayList<>(themesList);
        String searchText = search.toLowerCase(Locale.ROOT);
        for (JSONObject item: themesList) {
            if(!item.optString("title").toLowerCase(Locale.ROOT).contains(searchText)) {
                currentThemes.add(item);
            }
        }
        startThemes.removeAll(currentThemes);
        setThemesList(startThemes);
    }
}