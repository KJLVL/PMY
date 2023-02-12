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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FragmentForumMyEditTheme extends Fragment {
    private View inflatedView;

    private TextView description;
    private TextView title;
    private LinearLayout theme;
    private Button edit;
    private Button addPhoto;
    private LinearLayout linearLayout;

    private Requests requests;
    private List<String> photoNames;
    private List<String> photoList;
    private Bitmap bitmap;
    private int countPhoto = 0;

    public static FragmentForumMyEditTheme newInstance() {
        return new FragmentForumMyEditTheme();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_forum_new_theme,
                container,
                false
        );

        description = inflatedView.findViewById(R.id.description);
        title = inflatedView.findViewById(R.id.title);
        theme = inflatedView.findViewById(R.id.theme);
        edit = inflatedView.findViewById(R.id.edit);
        addPhoto = inflatedView.findViewById(R.id.addPhoto);
        linearLayout = inflatedView.findViewById(R.id.layout);

        requests = new Requests();
        photoNames = new ArrayList<>();
        photoList = new ArrayList<>();

        addPhoto.setOnClickListener(view -> this.addPhoto());

        edit.setOnClickListener(view -> this.checkTheme());

        return inflatedView;
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

        this.createTheme();
    }

    private void createTheme() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(requests.urlRequest + "user/forum");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("title", title.getText().toString()));
        params.add(new BasicNameValuePair("content", description.getText().toString()));
        params.add(new BasicNameValuePair("photoNames", photoNames.toString()));
        params.add(new BasicNameValuePair("photo", photoList.toString()));

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