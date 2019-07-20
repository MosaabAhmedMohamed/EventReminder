package com.example.eventreminder.Requests;

import com.example.eventreminder.Requests.Responses.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("/data/2.5/forecast")
    Call<WeatherResponse> getWeatherByCityName(@Query("q") String cityName,@Query("APPID") String APIKey,@Query("cnt") String numberOfDays ,@Query("units") String metric);
}
