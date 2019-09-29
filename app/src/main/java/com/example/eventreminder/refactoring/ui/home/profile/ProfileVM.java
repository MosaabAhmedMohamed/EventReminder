package com.example.eventreminder.refactoring.ui.home.profile;

import androidx.lifecycle.ViewModel;
import com.example.eventreminder.refactoring.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

public class ProfileVM extends ViewModel {

    private SessionManager sessionManager;

    @Inject
    public ProfileVM(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public GoogleSignInAccount getUserProfile()
    {
        return sessionManager.getAcc();
    }


}
