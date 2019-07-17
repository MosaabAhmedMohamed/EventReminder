package com.example.eventreminder.Requests;

import com.example.eventreminder.Util.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder().addInterceptor(logging);


    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.getInstance().BaseUrl)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static WeatherApi weatherApi = retrofit.create(WeatherApi.class);

    public static WeatherApi getWetherApi(){
        return weatherApi;
    }
}