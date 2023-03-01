package com.example.myaquarium.server;

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
//    public String urlRequest = "http://192.168.0.106/";
//    public String urlRequestImg = "http://192.168.0.106/img/";
    public static JSONObject user;
    public String urlRequestImg = "http://172.20.10.2/img/";
    public String urlRequest = "http://172.20.10.2/";

    public JSONArray setRequest(String url, List<NameValuePair> params) throws IOException, JSONException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(url);

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

        return new JSONArray(stringBuilder.toString());
    }

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
        Requests.user = user;
    }

}
