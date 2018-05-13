package com.example.allen.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * HttpUtil
 *
 * @author: Allen
 * @time: 2018/3/1 10:15
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
