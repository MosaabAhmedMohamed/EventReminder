package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventreminder.old.Repositories.WeatherRepo;
import com.example.eventreminder.old.Requests.Responses.WeatherResponse;

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
