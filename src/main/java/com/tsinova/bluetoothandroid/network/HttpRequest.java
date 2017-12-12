package com.tsinova.bluetoothandroid.network;

import android.util.Log;

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
            = MediaType.parse("application/json");

    private OkHttpClient client;

    public HttpRequest() {
        client = new OkHttpClient.Builder().addInterceptor(new CookieInterceptor()).build();
    }


    public void post(final String url, final String json) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody body = RequestBody.create(JSON, json);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = null;
                    response = client.newCall(request).execute();

                    Log.i("okhttp",response.body().string());

                } catch (Exception e) {


                }
            }
        }).start();

    }
}
