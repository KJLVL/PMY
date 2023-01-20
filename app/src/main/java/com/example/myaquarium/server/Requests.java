package com.example.myaquarium.server;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Requests {
    public String urlFishList = "http://192.168.0.106/fish/list";
    public String urlUserFish= "http://192.168.0.106/user/fish";
    public String urlTipsMenu = "http://192.168.0.106/tips/menu";
    public String urlTips = "http://192.168.0.106/tips/tips";
    public String urlGetUser = "http://192.168.0.106/user";
    public String urlUserUpdate = "http://192.168.0.106/user/update";
    public String urlPasswordUpdate = "http://192.168.0.106/user/password";
    public String urlSectionsTitle = "http://192.168.0.106/themes";
    public String urlCalculateFish = "http://192.168.0.106/calculate";

    public String[] setRequest(String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(url);

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
        String res = stringBuilder.toString();
        res = res.replace("[", "");
        res = res.replace("]", "");

        return res.split(",(?!\"| )");
    }

}
