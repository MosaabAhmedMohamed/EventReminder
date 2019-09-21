package com.example.eventreminder.refactoring;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.eventreminder.refactoring.data.local.PreferencesHelper;
import com.example.eventreminder.refactoring.data.models.User;
import com.example.eventreminder.refactoring.ui.auth.AuthResource;
import com.example.eventreminder.refactoring.util.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.example.eventreminder.refactoring.util.Constants.IS_USER_LOGGED_IN_KEY;

@Singleton
public class SessionManager {
    private MediatorLiveData<AuthResource<User>> cachedUser = new MediatorLiveData<>();

    @Inject
    PreferencesHelper preferencesHelper;

    @Inject
    public SessionManager() {

    }

    public void authenticateGoogleUser(final LiveData<AuthResource<User>> source) {
        if (cachedUser != null) {
            cachedUser.setValue(AuthResource.loading((User) null));
            cachedUser.addSource(source, new Observer<AuthResource<User>>() {
                @Override
                public void onChanged(AuthResource<User> userAuthResource) {
                    preferencesHelper.putBoolean(IS_USER_LOGGED_IN_KEY,true);
                    cachedUser.setValue(userAuthResource);
                    cachedUser.removeSource(source);

                    if (userAuthResource.status.equals(AuthResource.AuthStatus.ERROR)) {
                        cachedUser.setValue(AuthResource.<User>logout());
                        preferencesHelper.putBoolean(IS_USER_LOGGED_IN_KEY, false);
                    }
                }
            });
        }
    }

    public void logOut() {
        preferencesHelper.putBoolean(IS_USER_LOGGED_IN_KEY, false);
        cachedUser.setValue(AuthResource.<User>logout());
    }

    public LiveData<AuthResource<User>> getAuthUser() {
        return cachedUser;
    }

}
