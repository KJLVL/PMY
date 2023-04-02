package com.example.myaquarium;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myaquarium.service.Navigation;
import com.example.myaquarium.service.Requests;
import com.example.myaquarium.service.UserData;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;
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
import java.util.Objects;

import pl.utkala.searchablespinner.SearchableSpinner;

public class ProfileSettings extends AppCompatActivity {
    private EditText name;
    private EditText surname;
    private SearchableSpinner spinner;
    private EditText phone;
    private EditText login;

    private TextView password;
    private ImageView image;
    private Button save;
    private TextView exit;

    private RelativeLayout root;

    private JSONObject userInfo;

    private Requests requests;
    private Bitmap bitmap;
    private String newAvatar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        Navigation.setToolbar(
                this,
                getApplicationContext().getString(R.string.settings_text),
                Profile.class
        );
        Navigation.setMenuNavigation(this);

        requests = new Requests();
        root = findViewById(R.id.root);

        image = this.findViewById(R.id.image);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        spinner = findViewById(R.id.spinner);
        phone = findViewById(R.id.phone);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        save = findViewById(R.id.save);
        exit = findViewById(R.id.exit);

        this.getCities();
        this.getUser();

        password.setOnClickListener(view -> this.changePassword());
        save.setOnClickListener(view -> this.updateUser());
        exit.setOnClickListener(view -> {
            UserData.clearUserData(this);
            startActivity(new Intent(ProfileSettings.this, SignIn.class));
        });

        TextView download = findViewById(R.id.download);
        download.setOnClickListener(view -> downloadImage());
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

                this.runOnUiThread(() -> {
                    android.widget.ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            cities
                    );
                    this.spinner.setAdapter(adapter);
                    if (!userInfo.optString("city").equals("null"))
                        this.spinner.setSelection(adapter.getPosition(userInfo.optString("city")));
                    else this.spinner.setSelection(adapter.getPosition(""));
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
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

            List<NameValuePair> params = new ArrayList<>(List.of(
                    new BasicNameValuePair("oldPassword", oldPassword),
                    new BasicNameValuePair("oldPassword", oldPassword),
                    new BasicNameValuePair("id", UserData.getUserData(this))

                )
            );

            Runnable runnable = () -> {
                try {
                    JSONArray message = requests.setRequest(requests.urlRequest + "user/password", params);
                    JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                    if (object.optString("success").equals("1")) {
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
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("id", UserData.getUserData(this))
        )
        );
        Runnable runnable = () -> {
            try {
                JSONArray user = requests.setRequest(requests.urlRequest + "user", params);
                userInfo = new JSONObject(user.getJSONObject(0).toString());

                this.runOnUiThread(() -> {
                    name.setText(userInfo.optString("user_name"));
                    if (!userInfo.optString("surname").equals("null"))
                        surname.setText(userInfo.optString("surname"));
                    if (!userInfo.optString("phone").equals("null"))
                        phone.setText(userInfo.optString("phone"));
                    login.setText(userInfo.optString("login"));

                    if (!Objects.equals(userInfo.optString("avatar"), "")) {
                        Picasso.get()
                                .load(requests.urlRequestImg + userInfo.optString("avatar"))
                                .into(image);
                    } else {
                        image.setImageResource(R.drawable.ic_launcher_foreground);
                    }

                    image.setOnClickListener(view -> {
                        Intent intent = new Intent(this, ImageViewer.class);
                        intent.putExtra("image", requests.urlRequestImg + userInfo.optString("avatar"));
                        intent.putExtra("class", "ProfileSettings");
                        startActivity(intent);
                    });
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        this.validateEmail(login);
    }

    private void validateEmail(EditText loginField) {
        loginField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (loginField.getText().toString().equals("")) {
                    loginField.setError("введите email");
                    notValid();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginField.getText().toString()).matches()) {
                    loginField.setError("введите коррекртный email");
                    notValid();
                } else {
                    valid();
                }
            }
        });
    }

    private void notValid() {
        this.save.setEnabled(false);
    }

    private void valid() {
        this.save.setEnabled(true);
    }

    private void updateUser() {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("avatar", newAvatar),
                new BasicNameValuePair("name", name.getText().toString()),
                new BasicNameValuePair("surname", surname.getText().toString()),
                new BasicNameValuePair("login", login.getText().toString()),
                new BasicNameValuePair("city", spinner.getSelectedItem().toString()),
                new BasicNameValuePair("phone", phone.getText().toString()),
                new BasicNameValuePair("id", UserData.getUserData(this))
            )
        );

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/update", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                if (object.optString("success").equals("1")) {
                    root.post(() -> {
                        Toast.makeText(
                                this,
                                "Данные были успешно сохранены!", Toast.LENGTH_SHORT
                        ).show();
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
                    ImageView img = new ImageView(this);
                    img.setImageURI(uri);
                    img.setRotation(90);

                    BitmapDrawable bd = (BitmapDrawable) img.getDrawable();

                    Runnable runnable = () -> {
                        this.bitmap = bd.getBitmap();
                        image.post(() -> this.generateImage(bitmap));

                    };
                    Thread thread = new Thread(runnable);
                    thread.start();

                    Picasso.get().load(uri).into(image);
                    image.setRotation(90);
                    image.setOnClickListener(view -> {
                        Intent intent = new Intent(this, ImageViewer.class);
                        intent.putExtra("image",  uri);
                        intent.putExtra("class", ProfileSettings.class);
                        startActivity(intent);
                    });
                }
            });

    private void generateImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            this.newAvatar = Base64.encodeToString(bytes, Base64.DEFAULT);
        } else {
            this.newAvatar = "";
        }
    }

}