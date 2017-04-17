package com.mangoreceipt.protocol;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hyungsoklee on 2017. 4. 8..
 */

public class ServiceGenerator {

    private static final String BASE_URL = "http://172.30.25.194:8080/";

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();



    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
