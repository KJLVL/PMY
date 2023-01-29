package com.example.myaquarium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.Service;
import com.example.myaquarium.adapter.FishListWithChoiceAdapter;
import com.example.myaquarium.adapter.ResultCompatibilityAdapter;
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
import java.util.Objects;

public class FragmentServiceCalculatorFish extends Fragment {
    private View inflatedView;
    private RecyclerView listview;
    private RecyclerView listviewResult;
    private Button calculationFish;

    private Requests requests;

    private FishListWithChoiceAdapter fishAdapter;
    private ResultCompatibilityAdapter compatibilityAdapter;

    private List<String> fishList;
    private List<String> currentFishList;
    private List<List<String>> resultComp;

    public static FragmentServiceCalculatorFish newInstance() {
        return new FragmentServiceCalculatorFish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_service_calculator_fish,
                container,
                false
        );

        resultComp = new ArrayList<>();
        currentFishList = new ArrayList<>();
        requests = new Requests();

        listview = inflatedView.findViewById(R.id.listview);
        listviewResult = inflatedView.findViewById(R.id.listviewResult);
        calculationFish = inflatedView.findViewById(R.id.calculationFish);

        this.setToolbar();
        this.setMessage();

        this.getFishList();
        this.setFishList(fishList);

        this.calculateFish();

        return inflatedView;
    }

    private void setCompatibilityList(List<List<String>> compList) {
        listviewResult.post(() -> {
            if (compList.size() != 0) {
                LinearLayout textResult = inflatedView.findViewById(R.id.textResult);
                textResult.setVisibility(View.VISIBLE);
                listviewResult.setVisibility(View.VISIBLE);
                Button btnRes = inflatedView.findViewById(R.id.btnRes);
                btnRes.setOnClickListener(view -> {
                    LinearLayout layout = inflatedView.findViewById(R.id.result);
                    if (layout.getVisibility() == View.GONE) {
                        layout.setVisibility(View.VISIBLE);
                    } else {
                        layout.setVisibility(View.GONE);
                    }
                });

                compatibilityAdapter = new ResultCompatibilityAdapter(inflatedView.getContext(), compList);
                listviewResult.setAdapter(compatibilityAdapter);
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            this.startActivity(new Intent(inflatedView.getContext(), Service.class));
        });

        ActionBar actionBar = ((Service)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setMessage() {
        Button btnFish = inflatedView.findViewById(R.id.btnFish);
        btnFish.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(inflatedView.getContext());
            dialog.setTitle("Расчет осовместимости рыбок");
            dialog.setMessage("Аквариумных рыбок - великое множество. И далеко не все рыбки совместимы между собой, даже если на глаз они кажутся мирными. Под совместимостью мы понимаем не только отсутствие взаимной агресси у разных видов, но и близкие параметры воды, обустройства, рациона питания и прочих условий содержания.");
            dialog.setPositiveButton("Закрыть", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            dialog.show();
        });
    }

    private void getFishList() {
        fishList = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                String[] list = requests.setRequest(requests.urlRequest + "fish/list");
                for (String item: list) {
                    JSONObject object = new JSONObject(item);
                    fishList.add(object.getString("fish_name"));
                }
                this.inflatedView.post(() -> {
                    fishAdapter.notifyDataSetChanged();
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setFishList(List<String> items) {
        listview.post(() -> {
            FishListWithChoiceAdapter.OnFishClickListener onFishClickListener = (fish) -> {
                if (fish.isChecked() && !currentFishList.contains(fish.getText().toString())) {
                    currentFishList.add(fish.getText().toString());
                }
                else {
                    currentFishList.remove(fish.getText().toString());
                    fishAdapter.notifyDataSetChanged();
                }
            };

            fishAdapter = new FishListWithChoiceAdapter(inflatedView.getContext(), items, onFishClickListener);
            listview.setAdapter(fishAdapter);
        });
    }

    private void calculateFish() {
        calculationFish.setOnClickListener(view -> {
            if (currentFishList.size() < 2) {
                resultComp.clear();
                setCompatibilityList(resultComp);
                AlertDialog.Builder dialog = new AlertDialog.Builder(inflatedView.getContext());
                dialog.setTitle("Ошибка");
                dialog.setMessage("Выберите хотя-бы двух рыбок!");

                dialog.setPositiveButton(
                        "Закрыть",
                        (dialogInterface, i) -> dialogInterface.dismiss()
                );
                dialog.show();
            }
            this.getComp();
        });
    }

    private void getComp() {
        resultComp = new ArrayList<>();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(requests.urlRequest + "calculate");

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("fish", String.join(",", currentFishList)));
        Runnable runnable = () -> {
            try {
                http.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
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
                String[] list = result.split(",");
                for (String item: list) {
                    JSONObject object = new JSONObject(item);
                    String name = Objects.requireNonNull(object.names()).getString(0);
                    List<String> fish = new ArrayList<>(List.of(
                            name,
                            object.getString(name)

                    ));
                    resultComp.add(fish);
                }
                this.setCompatibilityList(resultComp);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}