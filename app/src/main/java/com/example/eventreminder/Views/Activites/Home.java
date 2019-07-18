package com.example.eventreminder.Views.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.gson.Gson;
import java.util.LinkedList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.example.eventreminder.Util.Constants.CALENDAR_SCOPE;

public class Home extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.out_from_google_btn)
    Button outFromGooglBtn;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.google_nav_tv)
    TextView googleNavTv;

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
            ReplaceFragment(CurrentFragment);
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    @OnClick({R.id.menu, R.id.out_from_google_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.out_from_google_btn:
                signOutFromGoogle();
                drawerLayout.openDrawer(GravityCompat.START);
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

}
