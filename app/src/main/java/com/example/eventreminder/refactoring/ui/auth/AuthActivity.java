package com.example.eventreminder.refactoring.ui.auth;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.data.models.User;
import com.example.eventreminder.refactoring.ui.base.BaseActivity;
import com.example.eventreminder.refactoring.ui.base.ViewModelProviderFactory;
import com.example.eventreminder.refactoring.util.GooglePlayServiceUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import static com.example.eventreminder.refactoring.util.Constants.RC_SIGN_IN;
import static com.example.eventreminder.refactoring.util.Constants.REQUEST_GOOGLE_PLAY_SERVICES;

public class AuthActivity extends BaseActivity {
    //private static final String TAG = "AuthActivity";

    @Inject
    ViewModelProviderFactory providerFactory;
    AuthVM authVM;

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testtest", "onCreate: " + this.getClass().getSimpleName());

        setContentView(R.layout.activity_login);
        authVM = ViewModelProviders.of(this, providerFactory).get(AuthVM.class);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        if (!GooglePlayServiceUtils.isGooglePlayServicesAvailable(this)) {
            GooglePlayServiceUtils.acquireGooglePlayServices(this);
        }

        observuserprofile();
    }
    private void observuserprofile() {
        authVM.getAuthUser().observe(this, new Observer<AuthResource<User>>() {
            @Override
            public void onChanged(AuthResource<User> userAuthResource) {
                if (userAuthResource != null) {
                    switch (userAuthResource.status) {
                        case AUTHENTICATED: {
                           goToHome();
                        }
                    }
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = GooglePlayServiceUtils.getGoogleSignInClient(this).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_PLAY_SERVICES) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "This app requires Google Play Services. Please install " +
                        "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authVM.checkForSignIn(account);
            } catch (ApiException e) {
                e.printStackTrace();
                //Log.w(TAG, "handleSignInResult:error", e);
            }
        }
    }

    @OnClick(R.id.sign_in_button)
    public void onViewClicked(View view) {
        signIn();
    }
}