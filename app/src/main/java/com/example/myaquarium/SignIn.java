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

import com.example.myaquarium.server.Requests;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

public class SignIn extends AppCompatActivity {
    private RelativeLayout root;
    private final Requests requests = new Requests();
    public static List<Users> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnRegistration = findViewById(R.id.btnRegistration);
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
            Users user = new Users(login, password, name, "D:\\university\\vkr\\img\\noavatar.jpg");
            user.save();

            dialogInterface.dismiss();
            getNotice("Вы были успешно зарегистрированы");
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
           user = Users.find(Users.class, "login =?", login);

            if (user.get(0).login.equals(login) && user.get(0).password.equals(password)) {
                startActivity(new Intent(SignIn.this, Profile.class));
            } else {
                dialogInterface.dismiss();
                getNotice("Не верно введен логин или пароль");
            }
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