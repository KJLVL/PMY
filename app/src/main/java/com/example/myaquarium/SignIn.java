package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

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

public class SignIn extends AppCompatActivity {
    private static String urlSignIn = "http://192.168.0.106/user/login";
    private static String urlRegistration = "http://192.168.0.106/user/registration";
    private Button btnSignIn;
    private Button btnRegistration;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegistration = findViewById(R.id.btnRegistration);
        root = findViewById(R.id.root);

        btnRegistration.setOnClickListener(view -> showRegistrationWindow());
        btnSignIn.setOnClickListener(view -> showSignInWindow());
    }

    private void showRegistrationWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Регистрация");

        LayoutInflater inflater = LayoutInflater.from(this);
        View registrationWindow = inflater.inflate(R.layout.registration_window, null);
        dialog.setView(registrationWindow);

        MaterialEditText loginField = registrationWindow.findViewById(R.id.loginField);
        MaterialEditText passwordField = registrationWindow.findViewById(R.id.passwordField);
        MaterialEditText nameField = registrationWindow.findViewById(R.id.nameField);

        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Зарегистрироваться", (dialogInterface, i) -> {
            String login = loginField.getText().toString();
            String password = passwordField.getText().toString();
            String name = nameField.getText().toString();

            if (TextUtils.isEmpty(login)) {
                Snackbar.make(root, "Введите логин", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(name)) {
                Snackbar.make(root, "Введите ваше имя", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 5) {
                Snackbar.make(
                        root,
                        "Введите пароль более 5 символов", Snackbar.LENGTH_SHORT
                ).show();
                return;
            }

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("login", login));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("name", name));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost(urlRegistration);

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

                    JSONObject object = new JSONObject(stringBuilder.toString());
                    String success = object.getString("success");

                    if (success.equals("1")) {
                        dialogInterface.dismiss();
                        getNotice("Вы были успешно зарегистрированы");
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

    private void showSignInWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Вход");

        LayoutInflater inflater = LayoutInflater.from(this);
        View signInWindow = inflater.inflate(R.layout.sign_in_window, null);
        dialog.setView(signInWindow);

        MaterialEditText loginField = signInWindow.findViewById(R.id.loginField);
        MaterialEditText passwordField = signInWindow.findViewById(R.id.passwordField);

        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Войти", (dialogInterface, i) -> {
            String login = loginField.getText().toString();
            String password = passwordField.getText().toString();
            if (TextUtils.isEmpty(login)) {
                Snackbar.make(root, "Введите логин", Snackbar.LENGTH_SHORT).show();
                return;
            }

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("login", login));
            params.add(new BasicNameValuePair("password", password));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost(urlSignIn);

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

                    String result = stringBuilder.toString();
                    JSONObject object = new JSONObject(result);
                    String success = object.getString("success");

                    if (success.equals("1")) {
                        startActivity(new Intent(SignIn.this, Profile.class));
                    } else {
                        dialogInterface.dismiss();
                        getNotice("Не верно введен логин или пароль");
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SignIn.this);
            dialog.setMessage(message);
            dialog.setNegativeButton("Закрыть", (dialogInterface, i) -> dialogInterface.dismiss());
            dialog.show();
        });
    }

}