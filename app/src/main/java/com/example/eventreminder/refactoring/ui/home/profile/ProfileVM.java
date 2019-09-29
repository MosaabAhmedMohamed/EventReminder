package com.example.eventreminder.refactoring.ui.home.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventreminder.refactoring.SessionManager;
import com.example.eventreminder.refactoring.data.models.User;
import com.example.eventreminder.refactoring.ui.auth.AuthResource;

import javax.inject.Inject;

public class ProfileVM extends ViewModel {

    private SessionManager sessionManager;

    @Inject
    public ProfileVM(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public LiveData<AuthResource<User>> getAuthUser()
    {
        return sessionManager.getAuthUser();
    }


}
