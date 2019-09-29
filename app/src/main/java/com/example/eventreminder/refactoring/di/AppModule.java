package com.example.eventreminder.refactoring.di;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.data.models.User;
import com.example.eventreminder.refactoring.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.eventreminder.refactoring.util.Constants.CONNECTION_TIMEOUT;
import static com.example.eventreminder.refactoring.util.Constants.READ_TIMEOUT;
import static com.example.eventreminder.refactoring.util.Constants.WRITE_TIMEOUT;

@Module
public class AppModule {


    //RetroFit
    @Singleton
    @Provides
    static HttpLoggingInterceptor logging() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Singleton
    @Provides
    static OkHttpClient httpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                // establish connection to server
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                // time between each byte read from the server
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                // time between each byte sent to server
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Singleton
    @Provides
    static Retrofit provideRetrofitInstance(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    //Glide
    @Singleton
    @Provides
    static RequestOptions provideRequestOptions() {
        return RequestOptions.placeholderOf(R.drawable.logo)
                .error(R.drawable.logo);
    }

    @Singleton
    @Provides
    static RequestManager provideGlideInstance(Application application, RequestOptions requestOptions) {
        return Glide.with(application).setDefaultRequestOptions(requestOptions);
    }

    @Singleton
    @Provides
    static Drawable provideAppDrawable(Application application) {
        return ContextCompat.getDrawable(application, R.drawable.ic_launcher_background);
    }


    @Provides
    @Singleton
    static Context provideContext(Application application) {
        return application;
    }

    //shared Pref
    @Provides
    @Singleton
    static SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    //GSON
    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Provides
    @Singleton
    Bundle provideBundle() {
        return new Bundle();
    }
}
