package com.example.eventreminder.refactoring.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.SessionManager;
import com.example.eventreminder.refactoring.data.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

public class AuthVM extends ViewModel {

    private SessionManager sessionManager;

    @Inject
    public AuthVM(SessionManager sessionManager) {
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

    public void checkForSignIn(GoogleSignInAccount account) {
        sessionManager.authenticateGoogleUser(signedInAccount(account));
    }
}
