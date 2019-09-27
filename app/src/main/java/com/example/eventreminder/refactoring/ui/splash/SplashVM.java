package com.example.eventreminder.refactoring.ui.splash;

import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.data.local.PreferencesHelper;
import javax.inject.Inject;
import static com.example.eventreminder.refactoring.util.Constants.IS_USER_LOGGED_IN_KEY;

public class SplashVM extends ViewModel {

    private PreferencesHelper mPreferencesHelper;

    @Inject
    public SplashVM(PreferencesHelper preferencesHelper) {
        mPreferencesHelper = preferencesHelper;

    }

    public boolean isLoggedIn() {
        if (mPreferencesHelper.getBoolean(IS_USER_LOGGED_IN_KEY)) {
            return true;
        }
        return false;
    }

}
