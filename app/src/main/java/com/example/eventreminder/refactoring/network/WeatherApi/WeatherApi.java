package com.example.eventreminder.refactoring.network.WeatherApi;

import com.example.eventreminder.refactoring.data.models.WeatherResponse;
import com.example.eventreminder.refactoring.network.Resource;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("/data/2.5/forecast")
    Flowable<WeatherResponse> getWeatherByCityName(@Query("q") String cityName, @Query("APPID") String APIKey,
                                                            @Query("cnt") String numberOfDays , @Query("units") String metric);
}
