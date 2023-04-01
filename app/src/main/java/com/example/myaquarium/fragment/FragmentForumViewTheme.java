package com.example.myaquarium.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.ImageViewer;
import com.example.myaquarium.R;
import com.example.myaquarium.adapter.ForumCommentsAdapter;
import com.example.myaquarium.server.Requests;
import com.github.chrisbanes.photoview.PhotoView;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FragmentForumViewTheme extends Fragment implements ViewSwitcher.ViewFactory {
    private View inflatedView;
    private ImageSwitcher switcher;
    private RecyclerView commentsRecycler;

    private int position = 0;
    private List<Bitmap> images;
    private JSONArray comments;
    private LinearLayout linearLayout;
    private Bitmap bitmap;
    private EditText comment;
    private TextView phone;
    private TextView city;
    private LinearLayout layoutPhoto;

    private List<String> photoNames;
    private List<String> photoList;
    private int count = 0;
    private String loginTo = "";
    private String idLoginTo = "";

    private final JSONObject theme;
    private final String id;
    private final Requests requests = new Requests();
    private ForumCommentsAdapter forumCommentsAdapter;

    public FragmentForumViewTheme(JSONObject theme, String id) {
        this.theme = theme;
        this.id = id;
    }

    public static FragmentForumViewTheme newInstance(JSONObject theme, String id) {
        return new FragmentForumViewTheme(theme, id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(
                R.layout.fragment_forum_view_discussions,
                container,
                false
        );

        TextView title = inflatedView.findViewById(R.id.title);
        title.setText(theme.optString("sections"));

        TextView phone = inflatedView.findViewById(R.id.phone);
        if (!theme.optString("user_phone").equals("") && !theme.optString("user_phone").equals("null")) {
            phone.setText("Номер телефона: " + theme.optString("user_phone"));
        } else {
            phone.setVisibility(View.GONE);
        }

        TextView city = inflatedView.findViewById(R.id.city);
        if (!theme.optString("city").equals("") && !theme.optString("city").equals("null")) {
            city.setText("Город: " + theme.optString("city"));
        } else {
            city.setVisibility(View.GONE);
        }


        TextView themeTitle = inflatedView.findViewById(R.id.themeTitle);
        themeTitle.setText(theme.optString("title"));
        TextView content = inflatedView.findViewById(R.id.content);
        content.setText(theme.optString("content"));
        photoNames = new ArrayList<>();
        photoList = new ArrayList<>();
        linearLayout = inflatedView.findViewById(R.id.layout);

        ImageButton send = inflatedView.findViewById(R.id.send);
        ImageButton attach = inflatedView.findViewById(R.id.attach);
        commentsRecycler = inflatedView.findViewById(R.id.commentsRecycler);
        Button buttonLeft = inflatedView.findViewById(R.id.buttonLeft);
        Button buttonRight = inflatedView.findViewById(R.id.buttonRight);
        LinearLayout imagesLayout = inflatedView.findViewById(R.id.images);
        switcher = inflatedView.findViewById(R.id.imageSwitcher);
        switcher.setFactory(this);
        comment = inflatedView.findViewById(R.id.comment);

        send.setOnClickListener(view -> {
            if (comment.getText().toString().equals("")) {
                Toast.makeText(
                        inflatedView.getContext(),
                        "Введите комментарий", Toast.LENGTH_LONG
                ).show();
            } else {
                this.sendComment();
            }
        });
        attach.setOnClickListener(view -> {
            if (count < 1) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(intent);
            } else {
                Toast.makeText(
                        inflatedView.getContext(),
                        "Вы не можете добавить более 1 изображения", Toast.LENGTH_LONG
                ).show();
            }
        });

        this.getComments();

        if (theme.optString("images").equals("null") || theme.optString("images").equals("")) {
            imagesLayout.setVisibility(View.GONE);
        } else {
            this.setImages();
            buttonLeft.setOnClickListener(
                    view -> {
                        this.setPosition(-1);
                        switcher.setImageDrawable(new BitmapDrawable(
                                        inflatedView.getContext().getResources(),
                                        images.get(position)
                                )
                        );
                    }
            );
            buttonRight.setOnClickListener(
                    view -> {
                        this.setPosition(1);
                        switcher.setImageDrawable(new BitmapDrawable(
                                        inflatedView.getContext().getResources(),
                                        images.get(position)
                                )
                        );
                    }
            );

        }

        LikeButton likeButton = inflatedView.findViewById(R.id.like);
        this.getLike(likeButton);
        likeButton.setOnClickListener(view -> this.likeAction(likeButton));

        return inflatedView;
    }

    private void sendComment() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(requests.urlRequest + "user/forum/comment");

        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("theme_id", theme.optString("id")),
                new BasicNameValuePair("response_user_id", idLoginTo),
                new BasicNameValuePair("comment", comment.getText().toString())
        ));

        if (!photoNames.isEmpty()) {
            params.add(new BasicNameValuePair("imageName", photoNames.toString()));
            params.add(new BasicNameValuePair("image", photoList.toString()));
        }

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
                        comment.setText("");
                        if (count == 1) {
                            layoutPhoto.removeAllViewsInLayout();
                        }
                        count = 0;
                        Toast.makeText(
                                inflatedView.getContext(),
                                "Комментарий был успешно добавлен", Toast.LENGTH_LONG
                        ).show();
                        InputMethodManager imm = (InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                getActivity()
                                        .getWindow()
                                        .getDecorView()
                                        .getWindowToken(),
                                0
                        );
                        this.getComments();
                    });
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void likeAction(LikeButton likeButton) {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("theme_id", theme.optString("id")),
                new BasicNameValuePair("like", String.valueOf(likeButton.isLiked()))
        )
        );

        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "user/forum/like", params);
                inflatedView.post(() -> {
                    try {
                        if (result.getJSONObject(0).getString("success").equals("1")) {
                            Toast.makeText(
                                    inflatedView.getContext(),
                                    "Добавлено в избранное", Toast.LENGTH_SHORT
                            ).show();
                            likeButton.setLiked(true);
                        } else {
                            Toast.makeText(
                                    inflatedView.getContext(),
                                    "Удалено из избранного", Toast.LENGTH_SHORT
                            ).show();
                            likeButton.setLiked(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getLike(LikeButton likeButton) {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("theme_id", theme.optString("id"))
        )
        );

        Runnable runnable = () -> {
            try {
                JSONArray result = requests.setRequest(requests.urlRequest + "user/forum/getLike", params);
                inflatedView.post(() -> {
                    try {
                        likeButton.setLiked(result.getJSONObject(0).getString("success").equals("1"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getComments() {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("theme_id", theme.optString("id"))
            )
        );

        Runnable runnable = () -> {
            try {
                comments = requests.setRequest(requests.urlRequest + "user/forum/comments", params);

                if (!comments.getJSONObject(0).toString().contains("success")) {
                    this.inflatedView.post(() -> this.setCommentsList(comments));
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setCommentsList(JSONArray comments) {
        commentsRecycler.post(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    inflatedView.getContext(),
                    RecyclerView.VERTICAL,
                    false
            );
            commentsRecycler.setLayoutManager(layoutManager);

            ForumCommentsAdapter.onAnswerClickListener onAnswerClickListener = (author, loginAuthor, nameAuthor) -> {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(comment, InputMethodManager.SHOW_IMPLICIT);
                comment.requestFocus();
                comment.setText(nameAuthor + ", ");
                loginTo = loginAuthor;
                idLoginTo = author;
            };

            ForumCommentsAdapter.onClickImageListener onClickImageListener = (uri, image) -> {
                image.setOnClickListener(view -> {
                    Intent intent = new Intent(this.getContext(), ImageViewer.class);
                    intent.putExtra("image", uri);
                    intent.putExtra("theme", theme.toString());
                    intent.putExtra("id", id);
                    intent.putExtra("class", "ViewTheme");
                    startActivity(intent);
                });
            };

            forumCommentsAdapter = new ForumCommentsAdapter(
                    inflatedView.getContext(),
                    comments,
                    onAnswerClickListener,
                    onClickImageListener
            );
            commentsRecycler.setAdapter(forumCommentsAdapter);
        });
    }

    private void setImages() {
        images = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                for (String item : theme.optString("images").split(";")) {
                    Bitmap bitmap = Picasso.get().load(requests.urlRequestImg + item).get();
                    this.images.add(bitmap);
                }
                switcher.post(() -> switcher.setImageDrawable(new BitmapDrawable(
                                inflatedView.getContext().getResources(),
                                images.get(0)
                        )
                ));

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void setPosition(int step) {
        position += step;
        if (position > images.size() - 1 || position < 0) {
            position = 0;
        }
    }

    @Override
    public View makeView() {
        PhotoView imageView = new PhotoView(inflatedView.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new
                ImageSwitcher.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        return imageView;
    }

    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    count++;
                    Intent data = result.getData();
                    Uri uri = data.getData();

                    photoNames.add(uri.getLastPathSegment());
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(
                                inflatedView.getContext().getApplicationContext().getContentResolver(),
                                uri
                        );

                        layoutPhoto = new LinearLayout(inflatedView.getContext());

                        layoutPhoto.setOrientation(LinearLayout.HORIZONTAL);
                        layoutPhoto.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT)
                        );

                        ImageView newImage = new ImageView(inflatedView.getContext());
                        Picasso.get()
                                .load(uri)
                                .resize(0, 100)
                                .centerCrop()
                                .into(newImage);

                        generateImage();

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                200,
                                200
                        );
                        lp.setMargins(10,5,0,5);

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
                            layoutPhoto.removeAllViews();
                            int index = photoNames.indexOf(uri.getLastPathSegment());
                            photoNames.remove(uri.getLastPathSegment());
                            photoList.remove(index);
                        });

                        newImage.setLayoutParams(lp);
                        layoutPhoto.setGravity(Gravity.CENTER_VERTICAL);
                        layoutPhoto.addView(newImage);
                        layoutPhoto.addView(button);
                        linearLayout.addView(layoutPhoto);

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

}