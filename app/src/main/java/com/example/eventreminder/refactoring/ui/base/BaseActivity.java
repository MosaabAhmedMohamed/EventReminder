package com.example.eventreminder.refactoring.ui.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.ui.home.HomeActivity;
import com.example.eventreminder.refactoring.SessionManager;
import com.example.eventreminder.refactoring.data.models.User;
import com.example.eventreminder.refactoring.ui.auth.AuthResource;
import com.example.eventreminder.refactoring.ui.auth.AuthActivity;
import com.example.eventreminder.refactoring.util.CommonUtils;
import com.example.eventreminder.refactoring.util.NetworkUtils;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public abstract class BaseActivity extends DaggerAppCompatActivity {
    private static final String TAG = "BaseActivity";

    private ProgressDialog mProgressDialog;
    private View mRootView;

    @Inject
    public SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (!this.getClass().getSimpleName().equals(HomeActivity.class.getSimpleName()))
            userAuthStatusObserver();*/
    }

    protected void userAuthStatusObserver() {
        sessionManager.getAuthUser().observe(this, new Observer<AuthResource<User>>() {
            @Override
            public void onChanged(AuthResource<User> userAuthResource) {
                if (userAuthResource != null) {
                    switch (userAuthResource.status) {
                        case LOADING: {
                            Log.d(TAG, "onChanged: BaseActivity: LOADING...");
                            showLoading();
                            break;
                        }
                        case AUTHENTICATED: {
                            Log.d(TAG, "onChanged: BaseActivity: AUTHENTICATED... " +
                                    "Authenticated as: "); //+; userAuthResource.data.getEmail());
                            /*if (!this.getClass().getSimpleName().equals(HomeActivity.class.getSimpleName())) {
                                goToHome();
                            }*/
                          /*  if (!this.getClass().getSimpleName().equals(HomeActivity.class.getSimpleName()))
                                goToHome();*/
                            hideLoading();
                            break;
                        }
                        case ERROR: {
                            Log.d(TAG, "onChanged: BaseActivity: ERROR...");
                            hideLoading();
                            break;
                        }
                        case NOT_AUTHENTICATED: {
                            Log.d(TAG, "onChanged: BaseActivity: NOT AUTHENTICATED. Navigating to AuthActivity screen.");
                            hideLoading();
                            if (!this.getClass().getSimpleName().equals(AuthActivity.class.getSimpleName()))
                                navLoginScreen();
                            break;
                        }
                    }
                }
            }
        });
    }

    public void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    protected void navLoginScreen() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
        // sessionManager.logOut();
    }

    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this);
    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    public void setRootView(View mRootView) {
        this.mRootView = mRootView;
    }

    public void showSnackBar(String message) {
        if (mRootView != null) {
            Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            snackbar.setActionTextColor(getColor(R.color.white));
            snackbar.setDuration(2500);
            snackbar.show();
        }
    }
}
