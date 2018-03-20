package com.rogmax.amirjaber.playground.retrofit;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Amir Jaber on 1/30/2018.
 */

class RetrofitClient {
    private static Retrofit retrofit = null;

    static Retrofit getClient(String url) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .setLenient()
                            .create()))
                    .client(new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build())
                    .build();
        }
        return retrofit;

    }
}
