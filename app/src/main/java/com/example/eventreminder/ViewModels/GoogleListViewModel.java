package com.example.eventreminder.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventreminder.Repositories.WeatherRepo;
import com.example.eventreminder.Requests.Responses.WeatherResponse;

public class GoogleListViewModel extends ViewModel {
    private MutableLiveData<WeatherResponse> weatherResponseMutableLiveData;
    private WeatherRepo weatherRepo;

    public GoogleListViewModel() {
        if (weatherResponseMutableLiveData != null)
                  return;
        weatherRepo = WeatherRepo.getInstance();
    }


    public MutableLiveData<WeatherResponse> getForCastData(String cityName,String ApiKey,String numberOfDays)
    {
        weatherResponseMutableLiveData = weatherRepo.weatherResponseMutableLiveData(cityName,ApiKey,numberOfDays);
        return weatherResponseMutableLiveData;
    }


    public MutableLiveData<WeatherResponse> getWeatherResponseMutableLiveData() {
        return weatherResponseMutableLiveData;
    }
}
