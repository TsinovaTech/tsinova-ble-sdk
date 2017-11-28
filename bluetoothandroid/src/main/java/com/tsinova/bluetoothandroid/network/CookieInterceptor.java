package com.tsinova.bluetoothandroid.network;

import android.text.TextUtils;
import android.util.Log;

import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;
import com.tsinova.bluetoothandroid.util.StringUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ihgoo on 2017/11/28.
 */

public class CookieInterceptor implements Interceptor {
    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        //添加cookie
        if (!TextUtils.isEmpty(SingletonBTInfo.INSTANCE.getCookies())){
            request = request.newBuilder().addHeader("Cookie", SingletonBTInfo.INSTANCE.getCookies()).build();
        }

        return chain.proceed(request);
    }
}