package com.example.myaquarium.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
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

public class FragmentForumMyEditTheme extends Fragment {
    private View inflatedView;

    private TextView description;
    private TextView title;
    private Button edit;
    private Button addPhoto;
    private LinearLayout linearLayout;

    private Requests requests;
    private List<String> photoNames;
    private List<String> photoList;
    private Bitmap bitmap;
    private int countPhoto = 0;
    private JSONObject theme;

    public FragmentForumMyEditTheme(JSONObject theme) {
        this.theme = theme;
    }

    public static FragmentForumMyEditTheme newInstance(JSONObject theme) {
        return new FragmentForumMyEditTheme(theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_forum_my_edit_theme,
                container,
                false
        );

        this.setToolbar();

        description = inflatedView.findViewById(R.id.description);
        description.setText(theme.optString("content"));
        title = inflatedView.findViewById(R.id.title);
        title.setText(theme.optString("title"));
        edit = inflatedView.findViewById(R.id.edit);
        addPhoto = inflatedView.findViewById(R.id.addPhoto);
        linearLayout = inflatedView.findViewById(R.id.layout);

        requests = new Requests();
        photoNames = new ArrayList<>();
        photoList = new ArrayList<>();

        if (!theme.optString("images").equals("null") && !theme.optString("images").equals("")) {
            for (String image : theme.optString("images").split(";")) {
                this.getImage(image);
            }
        }

        addPhoto.setOnClickListener(view -> this.addPhoto());

        edit.setOnClickListener(view -> this.checkTheme());

        return inflatedView;
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

    private void getImage(String image) {
        if (image.equals("null")) {
            return;
        }
        countPhoto++;
        LinearLayout layout = new LinearLayout(inflatedView.getContext());
        layout.setId(countPhoto);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        photoNames.add(image);
        photoList.add(image);

        ImageView newImage = new ImageView(inflatedView.getContext());
        Picasso.get()
                .load(requests.urlRequestImg + image)
                .into(newImage);

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
            int index = photoNames.indexOf(image);
            photoNames.remove(image);
            photoList.remove(index);
            countPhoto--;
        });

        newImage.setLayoutParams(lp);
        newImage.setOnClickListener(view -> {

        });
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(newImage);
        layout.addView(button);
        linearLayout.addView(layout);
    }

    private void addPhoto() {
        if (countPhoto < 3) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(intent);
        } else {
            Toast.makeText(
                    inflatedView.getContext(),
                    "Вы не можете добавить более 3-х фотографий", Toast.LENGTH_LONG
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
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(inflatedView.getContext().getApplicationContext().getContentResolver(), uri);

                        LinearLayout layout = new LinearLayout(inflatedView.getContext());
                        layout.setId(countPhoto);

                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT)
                        );

                        ImageView newImage = new ImageView(inflatedView.getContext());
                        newImage.setImageBitmap(bitmap);
                        generateImage();

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

                        newImage.setLayoutParams(lp);
                        newImage.setOnClickListener(view -> {

                        });
                        layout.setGravity(Gravity.CENTER_VERTICAL);
                        layout.addView(newImage);
                        layout.addView(button);
                        linearLayout.addView(layout);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    private void generateImage() {
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

        this.updateTheme();
    }

    private void updateTheme() {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("title", title.getText().toString()),
                new BasicNameValuePair("id", theme.optString("id")),
                new BasicNameValuePair("content", description.getText().toString())
        ));

        if (!photoNames.isEmpty()) {
            params.add(new BasicNameValuePair("photoNames", photoNames.toString()));
            params.add(new BasicNameValuePair("photo", photoList.toString()));
        }

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/forum/update", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                if (object.optString("success").equals("1")) {
                    inflatedView.post(() -> {
                        Toast.makeText(
                                inflatedView.getContext(),
                                "Тема была обновлена", Toast.LENGTH_SHORT
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