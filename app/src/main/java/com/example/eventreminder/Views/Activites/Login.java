package com.example.eventreminder.Views.Activites;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eventreminder.BaseViews.BaseActivity;
import com.example.eventreminder.R;
import com.example.eventreminder.Util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.eventreminder.Util.Constants.CALENDAR_SCOPE;
import static com.example.eventreminder.Util.Constants.RC_SIGN_IN;

public class Login extends BaseActivity {
    //private static final String TAG = "Login";

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;
    private GoogleSignInClient signInClient;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (!Constants.getInstance().isDeviceOnline(this))
            showIsOfflineSnackbar();
        else
            init();
    }

    private void init() {

        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else {
            checkAccount();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                Login.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void checkAccount() {
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acc != null && acc.getAccount() != null) {
            goToHomeAndSaveUser(acc);
        } else {
            initGSO();
        }
    }

    private void initGSO() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(CALENDAR_SCOPE))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_PLAY_SERVICES) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "This app requires Google Play Services. Please install " +
                        "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
            } else {
                checkAccount();
            }
        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                goToHomeAndSaveUser(account);
            } catch (ApiException e) {
                //Log.w(TAG, "handleSignInResult:error", e);
            }
        }
    }

    private void goToHomeAndSaveUser(GoogleSignInAccount account) {
        if (account != null) {
            editor.putBoolean("login", true);
            editor.putString(Constants.GOOGLE_USER, new Gson().toJson(account));
            editor.commit();
            startActivity(new Intent(this, Home.class));
            finish();
        } else {
            Toast.makeText(this, "error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.sign_in_button)
    public void onViewClicked(View view) {
        if (!Constants.getInstance().isDeviceOnline(this))
            showIsOfflineSnackbar();
        else {
            if (signInClient == null)
                initGSO();
            signIn();
        }

    }
}