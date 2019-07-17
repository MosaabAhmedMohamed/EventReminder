package com.example.eventreminder.Views.Activites;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.eventreminder.BaseViews.BaseActivity;
import com.example.eventreminder.BaseViews.BaseFragment;
import com.example.eventreminder.R;
import com.example.eventreminder.Util.Constants;
import com.example.eventreminder.Views.Fragments.GoogleEventsList;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.eventreminder.Util.Constants.CALENDAR_SCOPE;
import static com.example.eventreminder.Util.Constants.RC_RECOVERABLE;

public class Home extends BaseActivity {
    private static final String TAG = "Home";

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.out_from_google_btn)
    Button outFromGooglBtn;
    @BindView(R.id.out_from_facebook_btn)
    Button outFromFacebookBtn;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.google_ic)
    ImageView googleIc;
    @BindView(R.id.google_nav_tv)
    TextView googleNavTv;
    @BindView(R.id.google_nav_layout)
    RelativeLayout googleNavLayout;
    @BindView(R.id.facebook_ic)
    ImageView facebookIc;
    @BindView(R.id.facebook_nav_tv)
    TextView facebookNavTv;
    @BindView(R.id.facebook_nav_layout)
    RelativeLayout facebookNavLayout;

    private LinkedList<BaseFragment> fragments = new LinkedList<>();
    private BaseFragment CurrentFragment;
    private GoogleEventsList googleEventsList;


    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        GoogleSignInAccount acc = new Gson().fromJson(sharedPreferences.getString(Constants.GOOGLE_USER, " "), GoogleSignInAccount.class);
        if (acc != null && acc.getAccount() != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(CALENDAR_SCOPE))
                    .requestEmail()
                    .build();
            signInClient = GoogleSignIn.getClient(this, gso);

            googleEventsList = GoogleEventsList.newInstance();
            CurrentFragment = googleEventsList;
            onTabSelected(0);
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    @OnClick({R.id.menu, R.id.out_from_google_btn, R.id.out_from_facebook_btn, R.id.facebook_nav_layout, R.id.google_nav_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.out_from_google_btn:
                signOutFromGoogle();
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.out_from_facebook_btn:
                break;
            case R.id.facebook_nav_layout:
                onTabSelected(1);
                break;
            case R.id.google_nav_layout:
                onTabSelected(0);
                break;
        }
    }


    private void signOutFromGoogle() {
        signInClient.signOut().addOnCompleteListener(task -> finish());
        editor.remove(Constants.GOOGLE_USER);
        editor.putBoolean("login", false);
        editor.commit();
        startActivity(new Intent(this, Login.class));
        finish();

    }

    public void ReplaceFragment(BaseFragment fragment) {
        fragments.clear();
        CurrentFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments, fragment, "fragments")
                .commitAllowingStateLoss();
    }

    public void PushFragmentToSpecificRes(BaseFragment fragment, int res, boolean addToBackStack) {
        if (addToBackStack) {
            fragments.addLast(CurrentFragment);
            CurrentFragment = fragment;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(res, fragment, "fragments")
                .commitAllowingStateLoss();
    }

    public void PushFragment(BaseFragment fragment) {
        fragments.addLast(CurrentFragment);
        CurrentFragment = fragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments, fragment, "fragments")
                .commitAllowingStateLoss();
    }

    public void PopFragment() {
        if (!fragments.isEmpty())
            CurrentFragment = fragments.removeLast();
        else
            CurrentFragment = googleEventsList;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragments, CurrentFragment, "fragments")
                .commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);

        if (fragments.isEmpty()) {
            if (CurrentFragment instanceof GoogleEventsList) {
                if (!sharedPreferences.getBoolean("login", false)) {
                    startActivity(new Intent(this, Login.class));
                    finish();
                } else {
                    super.onBackPressed();
                }
            } else {
                ReplaceFragment(googleEventsList);
            }
        } else {
            PopFragment();
        }

    }


    private void onTabSelected(int position) {

        if (position == 0) {
            CurrentFragment = googleEventsList;

            googleNavLayout.setBackgroundColor(getColor(R.color.black));
            googleNavTv.setTextColor(getColor(R.color.white));

            facebookNavLayout.setBackgroundColor(getColor(R.color.white));
            facebookNavTv.setTextColor(getColor(R.color.black));

        }
        /*else if (position == 1) {
            selectedFragment = allServices;

            medicaMapNavLay.setBackgroundColor(getResources().getColor(R.color.black));
            bottomNavMapIc.setColorFilter(getResources().getColor(R.color.orange));

            allServicesNavLay.setBackgroundColor(getResources().getColor(R.color.orange));
            bottomNavServicesIc.setColorFilter(getResources().getColor(R.color.white));


        }*/

        ReplaceFragment(CurrentFragment);
    }

}
