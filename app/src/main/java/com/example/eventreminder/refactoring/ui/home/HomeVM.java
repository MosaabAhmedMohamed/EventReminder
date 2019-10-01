package com.example.eventreminder.refactoring.ui.home;

import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.data.local.PreferencesHelper;

import javax.inject.Inject;

import static com.example.eventreminder.refactoring.util.Constants.USER_CITY_KEY;

public class HomeVM extends ViewModel {

    private PreferencesHelper preferencesHelper;

    @Inject
    public HomeVM(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public String getCityName() {
        if (preferencesHelper.containKey(USER_CITY_KEY)) {
            return preferencesHelper.getString(USER_CITY_KEY);
        }
        return null;
    }

    public void setCityName(String cityName)
    {
        preferencesHelper.putString(USER_CITY_KEY,cityName);
    }
}
