package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myaquarium.service.Requests;
import com.example.myaquarium.service.UserData;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignIn extends AppCompatActivity {
    private RelativeLayout root;
    private final Requests requests = new Requests();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnRegistration = findViewById(R.id.btnRegistration);
        root = findViewById(R.id.root);

        if (UserData.getUserData(this) != null) {
            startActivity(new Intent(SignIn.this, Profile.class));
        }

        btnRegistration.setOnClickListener(view -> showRegistrationWindow());
        btnSignIn.setOnClickListener(view -> showSignInWindow());
    }

    private void showRegistrationWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        dialog.setTitle("Регистрация");

        LayoutInflater inflater = LayoutInflater.from(this);
        View registrationWindow = inflater.inflate(R.layout.registration_window, null);
        registrationWindow.setBackgroundColor(getResources().getColor(R.color.ripple));
        dialog.setView(registrationWindow);

        MaterialEditText loginField = registrationWindow.findViewById(R.id.loginField);
        MaterialEditText passwordField = registrationWindow.findViewById(R.id.passwordField);
        MaterialEditText nameField = registrationWindow.findViewById(R.id.nameField);

        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Регистрация", (dialogInterface, i) -> {
            String login = loginField.getText().toString();
            String password = passwordField.getText().toString();
            String name = nameField.getText().toString();

            List<NameValuePair> params = new ArrayList<>(List.of(
                    new BasicNameValuePair("login", login),
                    new BasicNameValuePair("password", password),
                    new BasicNameValuePair("name", name)
                )
            );

            Runnable runnable = () -> {
                try {
                    JSONArray message = requests.setRequest(requests.urlRequest + "user/registration", params);
                    JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                    dialogInterface.dismiss();
                    if (object.optString("success").equals("1")) {
                        runOnUiThread(() -> Toast.makeText(
                                this,
                                "Вы были успешно зарегистрированы", Toast.LENGTH_SHORT
                        ).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                this,
                                "Пользователь с таким email уже зарегистрирован", Toast.LENGTH_SHORT
                        ).show());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();

        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        this.validateEmail(loginField, alertDialog);
        this.validatePassword(passwordField, alertDialog);
        this.validateName(nameField, alertDialog);
    }

    private void validateEmail(MaterialEditText loginField, AlertDialog dialog) {
        loginField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (loginField.getText().toString().equals("")) {
                    loginField.setError("введите email");
                    notValid(dialog);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginField.getText().toString()).matches()) {
                    loginField.setError("введите коррекртный email");
                    notValid(dialog);
                } else {
                    valid(dialog);
                }
            }
        });
    }

    private void validatePassword(MaterialEditText passwordField, AlertDialog dialog) {
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (passwordField.getText().toString().equals("")) {
                    passwordField.setError("введите пароль");
                    notValid(dialog);
                } else if (passwordField.getText().toString().length() < 5) {
                    passwordField.setError("введите пароль более 4 символов");
                    notValid(dialog);
                } else {
                    valid(dialog);
                }
            }
        });
    }

    private void validateName(MaterialEditText nameField, AlertDialog dialog) {
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (nameField.getText().toString().equals("")) {
                    nameField.setError("введите ваше имя");
                    notValid(dialog);
                } else {
                    valid(dialog);
                }
            }
        });
    }

    private void notValid(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.fieldColor));
    }

    private void valid(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
    }

    private void showSignInWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        dialog.setTitle("Вход");

        LayoutInflater inflater = LayoutInflater.from(this);
        View signInWindow = inflater.inflate(R.layout.sign_in_window, null);
        signInWindow.setBackgroundColor(getResources().getColor(R.color.ripple));
        dialog.setView(signInWindow);

        MaterialEditText loginField = signInWindow.findViewById(R.id.loginField);
        MaterialEditText passwordField = signInWindow.findViewById(R.id.passwordField);
        TextView forgot = signInWindow.findViewById(R.id.forgot);

        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Войти", (dialogInterface, i) -> {
            String login = loginField.getText().toString();
            String password = passwordField.getText().toString();
            if (TextUtils.isEmpty(login)) {
                Snackbar.make(root, "Введите email", Snackbar.LENGTH_SHORT).show();
                return;
            }

            List<NameValuePair> params = new ArrayList<>(List.of(
                    new BasicNameValuePair("login", login),
                    new BasicNameValuePair("password", password)));

            Runnable runnable = () -> {
                try {
                    JSONArray message = requests.setRequest(requests.urlRequest + "user/login", params);
                    JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));

                    if (!object.optString("success").equals("0")) {
                        UserData.setUserData(this, object.optString("success"));
                        startActivity(new Intent(SignIn.this, Profile.class));
                    } else {
                        dialogInterface.dismiss();
                        runOnUiThread(() -> Toast.makeText(
                                this,
                                "Не верно введен email или пароль", Toast.LENGTH_SHORT
                        ).show());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        forgot.setOnClickListener(view -> {
            passwordField.setVisibility(View.GONE);
            alertDialog.setTitle("Введите ваш Email для восстановления пароля");
            forgot.setVisibility(View.GONE);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("Отправить");
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    view1 -> this.passwordRecovery(loginField.getText().toString(), alertDialog)
            );

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText("Назад");
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view1 -> {
                alertDialog.dismiss();
                this.showSignInWindow();
            });
        });
        this.validateEmail(loginField, alertDialog);
    }

    private void passwordRecovery(String login, AlertDialog alertDialog) {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("login", login)
            )
        );

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/recovery", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                alertDialog.dismiss();

                if (object.optString("success").equals("1")) {
                    runOnUiThread(() -> Toast.makeText(
                            this,
                            "Новый пароль был отправлен вам на почту", Toast.LENGTH_SHORT
                    ).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(
                            this,
                            "Пользователя с таким email не существует", Toast.LENGTH_SHORT
                    ).show());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

}