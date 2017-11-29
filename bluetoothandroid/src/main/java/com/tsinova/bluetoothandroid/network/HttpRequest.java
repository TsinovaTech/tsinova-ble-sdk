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
            = MediaType.parse("application/x-www-form-urlencoded");

    private OkHttpClient client;

    public HttpRequest() {
        client = new OkHttpClient.Builder().addInterceptor(new CookieInterceptor()).build();
    }


    public void post(final String url, final String json) {
        try {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestBody body = RequestBody.create(JSON, json);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (Exception e) {


        }
    }



}
