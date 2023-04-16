package com.example.myaquarium.service;

import com.example.myaquarium.model.Theme;
import com.example.myaquarium.model.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Requests {
    public final String urlRequest = "https://d12b-37-112-232-215.ngrok-free.app/";
    public final String urlRequestImg = "https://d12b-37-112-232-215.ngrok-free.app/img/";

    public JSONArray setRequest(
            String url,
            List<NameValuePair> params
    ) throws IOException, JSONException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(url);

        http.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
        HttpResponse httpResponse = httpclient.execute(http);
        HttpEntity httpEntity = httpResponse.getEntity();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(httpEntity.getContent(), StandardCharsets.UTF_8)
        );
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.readLine() != null) {
            stringBuilder.append(bufferedReader.readLine());
        }
        String str = stringBuilder.toString().replace("</html>", "");
        str = str.replace("<br />", "");
        return new JSONArray(str);
    }

    public User getUser(JSONArray user) throws JSONException {
        JSONObject object = user.getJSONObject(0);

        return User.builder()
                .login(object.getString("login"))
                .userName(object.getString("user_name"))
                .surname(object.getString("surname"))
                .avatar(object.getString("avatar"))
                .aquariumVolume(object.getString("aquarium_volume"))
                .city(object.getString("city"))
                .phone(object.getString("phone"))
                .build();
    }

    public Theme getTheme(JSONObject theme) throws JSONException {

        String categoryTitle = "";
        if (!theme.isNull("category_title")) {
            categoryTitle = theme.getString("category_title");
        }

        return Theme.builder()
                .id(theme.getString("id"))
                .title(theme.getString("title"))
                .author(theme.getString("author"))
                .date(theme.getString("date"))
                .city(theme.getString("city"))
                .categoryId(theme.getString("category_id"))
                .content(theme.getString("content"))
                .images(theme.getString("images"))
                .userPhone(theme.getString("user_phone"))
                .sections(theme.getString("sections"))
                .categoryTitle(categoryTitle)
                .build();
    }

}
