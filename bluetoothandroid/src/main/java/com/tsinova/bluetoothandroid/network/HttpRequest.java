package com.tsinova.bluetoothandroid.network;

import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;

import java.io.IOException;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ihgoo on 2017/11/23.
 */

public class HttpRequest {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;

    public HttpRequest() {
        client = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                return SingletonBTInfo.INSTANCE.getCookies();
            }
        }).build();
    }


    public void post(String url, String json) {
        try {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            response.body().string();
        } catch (Exception e) {

        }
    }

}
