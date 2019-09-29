package com.example.eventreminder.refactoring.ui.splash;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.SessionManager;
import com.example.eventreminder.refactoring.data.local.PreferencesHelper;
import com.example.eventreminder.refactoring.data.models.User;
import com.example.eventreminder.refactoring.ui.auth.AuthResource;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;
import static com.example.eventreminder.refactoring.util.Constants.IS_USER_LOGGED_IN_KEY;

public class SplashVM extends ViewModel {

    private PreferencesHelper mPreferencesHelper;
    private SessionManager sessionManager;

    @Inject
    public SplashVM(PreferencesHelper preferencesHelper,SessionManager sessionManager) {
        mPreferencesHelper = preferencesHelper;
        this.sessionManager = sessionManager;

    }

    private LiveData<AuthResource<User>> signedInAccount(GoogleSignInAccount acc) {

        MutableLiveData<AuthResource<User>> userLV = new MutableLiveData<>();
        if (acc != null && acc.getAccount() != null) {
            User user = new User(acc.getEmail(), acc.getDisplayName(), acc.getIdToken(), acc.getPhotoUrl());
            userLV.setValue(AuthResource.authenticated(user));
            return userLV;
        }
        userLV.setValue(AuthResource.error("Not Authuntecated", null));
        return userLV;
    }

   private void checkForSignIn(GoogleSignInAccount account) {
        sessionManager.authenticateGoogleUser(signedInAccount(account));
    }

    public boolean isLoggedIn() {
        if (mPreferencesHelper.getBoolean(IS_USER_LOGGED_IN_KEY)) {
            checkForSignIn(sessionManager.getAcc());
            return true;
        }
        return false;
    }

}
