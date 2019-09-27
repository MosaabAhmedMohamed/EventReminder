package com.example.eventreminder.refactoring.di.home;


import com.bumptech.glide.RequestManager;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.GoogleEventsListAdapter;
import com.example.eventreminder.refactoring.network.WeatherApi.WeatherApi;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public abstract class HomeModule {


    @HomeScope
    @Provides
    static WeatherApi provideWeatherApi(Retrofit retrofit) {
        return retrofit.create(WeatherApi.class);
    }

    @HomeScope
    @Provides
    static GoogleEventsListAdapter provideGoogleEventsListAdapter(RequestManager manager) {
        return new GoogleEventsListAdapter(manager);
    }
}
