package com.example.eventreminder.refactoring.ui.splash;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.SessionManager;
import com.example.eventreminder.refactoring.data.models.User;
import com.example.eventreminder.refactoring.ui.auth.AuthResource;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

public class SplashVM extends ViewModel {

    private Context mContext;
    private SessionManager mSessionManager;

    @Inject
    public SplashVM(SessionManager sessionManager, Context context) {

        mSessionManager = sessionManager;
        mContext = context;
    }

    private LiveData<AuthResource<User>> checkLastSignedInAccount() {

        MutableLiveData<AuthResource<User>> userLV = new MutableLiveData<>();
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(mContext);
        if (acc != null && acc.getAccount() != null) {
            User user = new User(acc.getEmail(), acc.getDisplayName(), acc.getIdToken(), acc.getPhotoUrl());
            userLV.setValue(AuthResource.authenticated(user));
            return userLV;
        }
        userLV.setValue(AuthResource.error("Not Authuntecated", null));
        return userLV;
    }

    public void checkForSignIn() {
        mSessionManager.authenticateGoogleUser(checkLastSignedInAccount());
    }
}
