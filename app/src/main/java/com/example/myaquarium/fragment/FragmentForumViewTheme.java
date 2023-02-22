package com.example.myaquarium.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.ForumCommentsAdapter;
import com.example.myaquarium.server.Requests;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FragmentForumViewTheme extends Fragment implements ViewSwitcher.ViewFactory {
    private View inflatedView;
    private ImageSwitcher switcher;
    private Button comment;
    private RecyclerView commentsRecycler;

    private int position = 0;
    private List<Bitmap> images;
    private JSONArray comments;

    private final JSONObject theme;
    private Requests requests = new Requests();
    private ForumCommentsAdapter forumCommentsAdapter;

    public FragmentForumViewTheme(JSONObject theme) {
        this.theme = theme;
    }

    public static FragmentForumViewTheme newInstance(JSONObject theme) {
        return new FragmentForumViewTheme(theme);
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
        title.setText(theme.optString("category_title") + " >> " + theme.optString("sections"));
        TextView themeTitle = inflatedView.findViewById(R.id.themeTitle);
        themeTitle.setText(theme.optString("title"));
        TextView content = inflatedView.findViewById(R.id.content);
        content.setText(theme.optString("content"));

        comment = inflatedView.findViewById(R.id.comment);
        commentsRecycler = inflatedView.findViewById(R.id.commentsRecycler);
        Button buttonLeft = inflatedView.findViewById(R.id.buttonLeft);
        Button buttonRight = inflatedView.findViewById(R.id.buttonRight);
        LinearLayout imagesLayout = inflatedView.findViewById(R.id.images);
        switcher = inflatedView.findViewById(R.id.imageSwitcher);
        switcher.setFactory(this);

        comment.setOnClickListener(view -> {
            FragmentForumCommentDialog dialog = new FragmentForumCommentDialog(
                    "",
                    theme.optString("id"),
                    theme
            );
            FragmentManager manager = getParentFragmentManager();
            dialog.show(manager, "myDialog");

        });

        this.getComments();

        if (theme.optString("images").equals("null") || theme.optString("images").equals("")) {
            imagesLayout.setVisibility(View.GONE);
        }
        else {
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

        return inflatedView;
    }

    private void getComments() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(requests.urlRequest + "user/forum/comments");

        List<NameValuePair> params = new ArrayList<>(List.of(
                        new BasicNameValuePair("theme_id", theme.optString("id"))
                        )
        );
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

                comments = new JSONArray(stringBuilder.toString());

                this.inflatedView.post(() -> {
                    this.setCommentsList(comments);
                });
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

            ForumCommentsAdapter.onAnswerClickListener onAnswerClickListener = (author) -> {
                FragmentForumCommentDialog dialog = new FragmentForumCommentDialog(
                        author,
                        theme.optString("id"),
                        theme
                );
                FragmentManager manager = getParentFragmentManager();
                dialog.show(manager, "myDialog");
            };

            forumCommentsAdapter = new ForumCommentsAdapter(
                    inflatedView.getContext(),
                    comments,
                    onAnswerClickListener
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
        ImageView imageView = new ImageView(inflatedView.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new
                ImageSwitcher.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return imageView;
    }

}