package com.example.eventreminder.Activites;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.eventreminder.BaseViews.BaseActivity;
import com.example.eventreminder.R;
import com.example.eventreminder.Util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.eventreminder.Util.Constants.CALENDAR_SCOPE;
import static com.example.eventreminder.Util.Constants.RC_SIGN_IN;

public class Login extends BaseActivity {
    private static final String TAG = "Login";

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;
    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(CALENDAR_SCOPE))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, new Scope(CALENDAR_SCOPE))) {
            goToHome(account);
        }
    }

    @OnClick(R.id.sign_in_button)
    public void onViewClicked() {
        signIn();

    }
    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                goToHome(account);
            } catch (ApiException e) {
                Log.w(TAG, "handleSignInResult:error", e);
            }
        }
    }

    private void goToHome(GoogleSignInAccount account) {
        if (account != null) {
            startActivity(new Intent(this, Home.class));
        }
    }
}