package com.example.eventreminder.old.Repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.eventreminder.old.Requests.Responses.WeatherResponse;
import com.example.eventreminder.old.Requests.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepo {

    private static WeatherRepo weatherRepo;
    private MutableLiveData<WeatherResponse> weatherResponseMutableLiveData;

    public static WeatherRepo getInstance() {
        if (weatherRepo == null)
            weatherRepo = new WeatherRepo();
        return weatherRepo;
    }

    public MutableLiveData<WeatherResponse> weatherResponseMutableLiveData(String cityName, String ApiKey, String numberOfDays) {
        weatherResponseMutableLiveData = new MutableLiveData<>();
        ServiceGenerator.getWetherApi().getWeatherByCityName(cityName, ApiKey, numberOfDays,"metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    weatherResponseMutableLiveData.setValue(response.body());
                } else {
                    weatherResponseMutableLiveData.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherResponseMutableLiveData.setValue(null);
            }
        });


        return weatherResponseMutableLiveData;
    }

    public MutableLiveData<WeatherResponse> getWeatherResponseMutableLiveData() {
        return weatherResponseMutableLiveData;
    }
}
