package com.example.myaquarium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.myaquarium.model.Theme;
import com.example.myaquarium.service.Requests;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.utkala.searchablespinner.SearchableSpinner;

public class FragmentForumSections extends Fragment {
    private View inflatedView;

    private SearchView search;
    private RecyclerView themesRecycler;
    private RecyclerView sectionsFilter;
    private CheckBox all;
    private LinearLayout filter;
    private SearchableSpinner citySpinner;

    private List<Theme> themesList;
    private List<Theme> currentThemes;
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

        TextView cityText = inflatedView.findViewById(R.id.cityText);
        LinearLayout citySelect = inflatedView.findViewById(R.id.citySelect);
        if (this.id != 2) {
            cityText.setVisibility(View.GONE);
            citySelect.setVisibility(View.GONE);
        }

        search = inflatedView.findViewById(R.id.search);
        citySpinner = inflatedView.findViewById(R.id.citySpinner);

        sectionsFilter = inflatedView.findViewById(R.id.sectionsFilter);
        themesRecycler = inflatedView.findViewById(R.id.themesRecycler);
        Button filters = inflatedView.findViewById(R.id.filters);
        all = inflatedView.findViewById(R.id.all);
        filter = inflatedView.findViewById(R.id.filter);

        checked = new LinkedHashMap<>();
        themesList = new ArrayList<>();
        currentThemes = new ArrayList<>();

        requests = new Requests();

        this.getCities();
        this.getThemesList();

        this.setThemesList(themesList);
        this.setThemesListItems(themesListItems);

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
                for (String item: themesListItems) {
                    checked.put(item, true);
                }
                this.citySpinner.setSelection(0);
                setThemesListItems(themesListItems);
                setThemesList(themesList);
            } else {
                for (String item: themesListItems) {
                    checked.put(item, false);
                }
                this.citySpinner.setSelection(0);
                setThemesListItems(themesListItems);
                setThemesList(new ArrayList<>());
            }
        });

        this.citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                currentThemes = new ArrayList<>(themesList);

                if (adapterView.getSelectedItem() == "") {
                    setThemesList(currentThemes);
                    all.setChecked(true);
                    for (String item: themesListItems) {
                        checked.put(item, true);
                    }
                    return;
                }

                List<Theme> oldThemes = new ArrayList<>();
                for (Theme item : themesList) {
                    if (!item.getCity().equals(adapterView.getSelectedItem())) {
                        oldThemes.add(item);
                        checked.put(item.getSections(), false);
                    }
                }
                currentThemes.removeAll(oldThemes);
                for (Theme item : currentThemes) {
                    checked.put(item.getSections(), true);
                }
                checkAllSectionsItems();
                setThemesListItems(themesListItems);
                setThemesList(currentThemes);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return inflatedView;
    }

    private void getCities() {
        List<String> cities = new ArrayList<>();
        cities.add("");
        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "city", new ArrayList<>());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(result.getJSONObject(i)));
                    cities.add(object.optString("city"));
                }

                inflatedView.post(() -> {
                    android.widget.ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            inflatedView.getContext(),
                            android.R.layout.simple_spinner_item,
                            cities
                    );
                    this.citySpinner.setAdapter(adapter);
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setThemesListItems(List<String> themesListItems) {
        sectionsFilter.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            sectionsFilter.setLayoutManager(layoutManager);

            ForumThemesListAdapter.OnThemeClickListener onThemeClickListener = (theme, checkBox) -> {
                List<Theme> oldThemes = new ArrayList<>();
                currentThemes = new ArrayList<>(themesList);

                if (checkBox.isChecked()) {
                    checked.put(theme, true);
                } else {
                    checked.put(theme, false);
                    all.setChecked(false);
                }
                checkAllSectionsItems();

                for (Theme item : themesList) {
                    for (Map.Entry<String, Boolean> entry: checked.entrySet()) {
                        if (item.getSections().equals(entry.getKey()) && !entry.getValue()) {
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
                    checked,
                    onThemeClickListener
            );
            sectionsFilter.setAdapter(themesListAdapter);
        });
    }

    private void checkAllSectionsItems() {
        int count = 0;
        if (!checked.isEmpty()) {
            for (Map.Entry<String, Boolean> entry : checked.entrySet()) {
                if (entry.getValue()) count++;
            }
            all.setChecked(count == checked.size());
        }
    }

    private void setThemesList(List<Theme> themesList) {
        themesRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            themesRecycler.setLayoutManager(layoutManager);

            ForumThemesAdapter.OnThemeClickListener onThemeClickListener = (themeId, category) -> {
                Intent intent = new Intent(inflatedView.getContext(), ViewTheme.class);
                intent.putExtra("themeId", themeId);
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
                    JSONObject theme = result.getJSONObject(i);
                    this.themesList.add(requests.getTheme(theme));

                    if (!themesListItems.contains(theme.getString("sections"))) {
                        checked.put(theme.getString("sections"), true);
                        themesListItems.add(theme.getString("sections"));
                    }
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

    private void searchThemesByEditText(String search, List<Theme> themesList) {
        List<Theme> currentThemes = new ArrayList<>();
        List<Theme> startThemes = new ArrayList<>(themesList);
        String searchText = search.toLowerCase(Locale.ROOT);
        for (Theme item: themesList) {
            if(!item.getTitle().toLowerCase(Locale.ROOT).contains(searchText)) {
                currentThemes.add(item);
            }
        }
        startThemes.removeAll(currentThemes);
        setThemesList(startThemes);
    }
}