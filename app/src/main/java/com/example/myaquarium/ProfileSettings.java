package com.example.myaquarium;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myaquarium.server.Requests;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileSettings extends AppCompatActivity {
    private EditText name;
    private EditText surname;
    private EditText city;
    private EditText phone;
    private EditText login;

    private TextView password;
    private ImageView image;
    private Button save;

    private RelativeLayout root;

    private Map<String, String> userInfo;

    private Requests requests;
    private Bitmap bitmap;
    private String newAvatar;
    private String newAvatarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        this.setToolbar();

        userInfo = new HashMap<>();
        requests = new Requests();
        root = findViewById(R.id.root);

        image = this.findViewById(R.id.image);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        city = findViewById(R.id.city);
        phone = findViewById(R.id.phone);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        save = findViewById(R.id.save);

        this.getUser();

        password.setOnClickListener(view -> this.changePassword());
        save.setOnClickListener(view -> this.save());

        Button download = findViewById(R.id.download);
        download.setOnClickListener(view -> downloadImage());

        TextView service = findViewById(R.id.service);
        TextView forum = findViewById(R.id.forum);
        TextView profile = findViewById(R.id.profile);

        service.setOnClickListener(view -> {
            this.startActivity(new Intent(this, Service.class));
        });

        forum.setOnClickListener(view -> {
            this.startActivity(new Intent(this, Forum.class));
        });

        profile.setOnClickListener(view -> {
            this.startActivity(new Intent(this, Profile.class));
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView = findViewById(R.id.title);
        textView.setText(getApplicationContext().getString(R.string.settings_text));

        toolbar.setNavigationOnClickListener(view -> {
            this.startActivity(new Intent(this, Profile.class));
        });
    }

    private void changePassword() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Изменение пароля");

        LayoutInflater inflater = LayoutInflater.from(this);
        View registrationWindow = inflater.inflate(R.layout.change_password_window, null);
        dialog.setView(registrationWindow);

        MaterialEditText oldPasswordField = registrationWindow.findViewById(R.id.oldPassword);
        MaterialEditText newPasswordField = registrationWindow.findViewById(R.id.newPassword);

        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Сохранить", (dialogInterface, i) -> {
            String oldPassword = oldPasswordField.getText().toString();
            String newPassword = newPasswordField.getText().toString();

            if (TextUtils.isEmpty(oldPassword)) {
                Snackbar.make(root, "Введите текущий пароль", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(newPassword)) {
                Snackbar.make(root, "Введите новый пароль", Snackbar.LENGTH_SHORT).show();
                return;
            }

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("oldPassword", oldPassword));
            params.add(new BasicNameValuePair("newPassword", newPassword));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost(requests.urlRequest + "user/password");

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
                        dialogInterface.dismiss();
                        getNotice("Пароль был успешно изменен!");
                    } else {
                        dialogInterface.dismiss();
                        getNotice("Неверно введен текущий пароль. Повторите попытку!");
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();

        });
        dialog.show();
    }

    private void getNotice(String message) {
        this.runOnUiThread(() -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileSettings.this);
            dialog.setMessage(message);
            dialog.setPositiveButton("Закрыть", (dialogInterface, i) -> dialogInterface.dismiss());
            dialog.show();
        });
    }

    private void getUser() {
        Runnable runnable = () -> {
            try {
                String[] tips = requests.setRequest(requests.urlRequest + "user");
                for (String item : tips) {
                    JSONObject object = new JSONObject(item);
                    userInfo.put("name", object.getString("user_name"));
                    userInfo.put("surname", !object.getString("surname").equals("null")
                            ? object.getString("surname") : "");
                    userInfo.put("city", !object.getString("city").equals("null")
                            ? object.getString("city") : "");
                    userInfo.put("phone", !object.getString("phone").equals("null")
                            ? object.getString("phone") : "");
                    userInfo.put("login", object.getString("login"));
                    userInfo.put("avatar", !object.getString("avatar").equals("null")
                            ? object.getString("avatar") : "");
                }
                this.runOnUiThread(() -> {
                    name.setText(userInfo.get("name"));
                    surname.setText(userInfo.get("surname"));
                    city.setText(userInfo.get("city"));
                    phone.setText(userInfo.get("phone"));
                    login.setText(userInfo.get("login"));

                    if (!Objects.equals(userInfo.get("avatar"), "")) {
                        Picasso.get()
                                .load(requests.urlRequestImg + userInfo.get("avatar"))
                                .into(image);
                    } else {
                        image.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void save() {
        if (!Objects.equals(userInfo.get("name"), name.getText().toString())
                || !Objects.equals(userInfo.get("surname"), name.getText().toString())
                || !Objects.equals(userInfo.get("login"), name.getText().toString())
        ) {
            this.updateUser();
        }
    }

    private void updateUser() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(requests.urlRequest + "user/update");

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("avatar", newAvatar));
        params.add(new BasicNameValuePair("avatarName", newAvatarName));
        params.add(new BasicNameValuePair("name", name.getText().toString()));
        params.add(new BasicNameValuePair("surname", surname.getText().toString()));
        params.add(new BasicNameValuePair("login", login.getText().toString()));

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
                    this.runOnUiThread(() -> {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileSettings.this);
                        dialog.setMessage("Данные были успешно сохранены!");
                        dialog.setPositiveButton("Закрыть", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });
                        dialog.show();
                    });

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void downloadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    newAvatarName = uri.getLastPathSegment();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        image.setImageBitmap(bitmap);
                        generateImage();
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
            newAvatar = Base64.encodeToString(bytes, Base64.DEFAULT);
        } else {
            newAvatar = "";
        }
    }

}