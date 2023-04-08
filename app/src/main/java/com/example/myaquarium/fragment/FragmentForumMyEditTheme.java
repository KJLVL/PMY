package com.example.myaquarium.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.example.myaquarium.model.Theme;
import com.example.myaquarium.service.ImageEditor;
import com.example.myaquarium.service.Requests;

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
    private LinearLayout linearLayout;

    private Requests requests;
    private List<String> photoNames;
    private List<String> photoList;
    private Bitmap bitmap;
    private int countPhoto = 0;
    private final Theme theme;

    public FragmentForumMyEditTheme(Theme theme) {
        this.theme = theme;
    }

    public static FragmentForumMyEditTheme newInstance(Theme theme) {
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
        description.setText(theme.getContent());
        title = inflatedView.findViewById(R.id.title);
        title.setText(theme.getTitle());
        Button edit = inflatedView.findViewById(R.id.edit);
        Button addPhoto = inflatedView.findViewById(R.id.addPhoto);
        linearLayout = inflatedView.findViewById(R.id.layout);

        requests = new Requests();
        photoNames = new ArrayList<>();
        photoList = new ArrayList<>();

        if (!theme.getImages().equals("null") && !theme.getImages().equals("")) {
            for (String image : theme.getImages().split(";")) {
                this.getImage(image);
            }
        }

        addPhoto.setOnClickListener(view -> this.addPhoto());

        edit.setOnClickListener(view -> this.checkTheme());

        return inflatedView;
    }

    private void setToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(
                view -> this.startActivity(new Intent(inflatedView.getContext(), Forum.class))
        );

        ActionBar actionBar = ((Forum)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void getImage(String image) {
        if (image.equals("null")) {
            return;
        }
        countPhoto++;
        ImageView img = new ImageView(inflatedView.getContext());
        Button button = ImageEditor.editAddedImage(Uri.parse(requests.urlRequestImg + image), inflatedView.getContext(), img);
        LinearLayout layout = ImageEditor.editLayoutImage(inflatedView.getContext(), button, img);

        photoNames.add(image);
        photoList.add(image);

        button.setOnClickListener(view -> {
            layout.removeAllViews();
            int index = photoNames.indexOf(image);
            photoNames.remove(image);
            photoList.remove(index);
            countPhoto--;
        });

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

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    countPhoto++;
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    photoNames.add(uri.getLastPathSegment());

                    ImageView img = new ImageView(inflatedView.getContext());
                    img.setImageURI(uri);
                    BitmapDrawable bd = (BitmapDrawable) img.getDrawable();
                    Runnable runnable = () -> {
                        this.bitmap = bd.getBitmap();
                        img.post(() -> this.generateImage(this.bitmap));

                    };
                    Thread thread = new Thread(runnable);
                    thread.start();

                    Button button = ImageEditor.editAddedImage(uri, inflatedView.getContext(), img);
                    LinearLayout layout = ImageEditor.editLayoutImage(inflatedView.getContext(), button, img);

                    button.setOnClickListener(view -> {
                        layout.removeAllViews();
                        int index = photoNames.indexOf(uri.getLastPathSegment());
                        photoNames.remove(uri.getLastPathSegment());
                        photoList.remove(index);
                        countPhoto--;
                    });

                    linearLayout.addView(layout);
                }
            });

    private void generateImage(Bitmap bitmap) {
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
                new BasicNameValuePair("id", theme.getId()),
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