package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.SessionManager;
import com.example.eventreminder.refactoring.data.models.WeatherResponse;
import com.example.eventreminder.refactoring.network.Resource;
import com.example.eventreminder.refactoring.network.WeatherApi.WeatherApi;
import com.example.eventreminder.refactoring.util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GoogleListViewModel extends ViewModel {
    private MediatorLiveData<Resource<WeatherResponse>> weatherResource;

    private WeatherApi weatherApi;
    private SessionManager sessionManager;

    @Inject
    public GoogleListViewModel(WeatherApi weatherApi, SessionManager sessionManager) {
        this.weatherApi = weatherApi;
        this.sessionManager = sessionManager;
    }


    public LiveData<Resource<WeatherResponse>> observeWeather() {
        if (weatherResource == null) {
            weatherResource = new MediatorLiveData<>();
            weatherResource.postValue(Resource.loading(null));
            final LiveData<Resource<WeatherResponse>> source = LiveDataReactiveStreams.fromPublisher(weatherApi
                    .getWeatherByCityName("cairo", Constants.getInstance().openWeatherMapAPIKey, "40", "metric")
                    .onErrorReturn(new Function<Throwable, WeatherResponse>() {
                        @Override
                        public WeatherResponse apply(Throwable throwable) throws Exception {
                            WeatherResponse weatherResponse = new WeatherResponse();
                            weatherResponse.setId(-1);
                            weatherResponse.setNetworkMessage(throwable.getLocalizedMessage());
                            return weatherResponse;
                        }
                    }).map(new Function<WeatherResponse, Resource<WeatherResponse>>() {
                        @Override
                        public Resource<WeatherResponse> apply(WeatherResponse weatherResponse) throws Exception {
                            if (weatherResponse.getId() == -1) {
                                return Resource.error("something went wrong : " + weatherResponse.getNetworkMessage(), null);
                            }
                            return Resource.success(weatherResponse);
                        }
                    }).subscribeOn(Schedulers.io()));
            weatherResource.addSource(source, new Observer<Resource<WeatherResponse>>() {
                @Override
                public void onChanged(Resource<WeatherResponse> weatherResponseResource) {
                    weatherResource.setValue(weatherResponseResource);
                    weatherResource.removeSource(source);
                }
            });
        }
        return weatherResource;
    }

    public GoogleSignInAccount getAuthAccount() {
        if (sessionManager.getAcc() == null) {
            sessionManager.logOut();
            return null;
        }
        return sessionManager.getAcc();
    }

    public void setGoogleAuthAccount(GoogleSignInAccount lastSignedInAccount) {
        sessionManager.setAcc(lastSignedInAccount);
    }

}
