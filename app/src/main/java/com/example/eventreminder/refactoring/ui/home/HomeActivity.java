package com.example.eventreminder.refactoring.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.ui.base.BaseActivity;
import com.example.eventreminder.refactoring.util.GooglePlayServiceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.out_from_google_btn)
    Button outFromGooglBtn;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.title_tv)
    TextView titleTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        init();
    }


    private void init() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
    }

    @OnClick({R.id.menu, R.id.out_from_google_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.out_from_google_btn:
                signOutFromGoogle();
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    private void signOutFromGoogle() {
        GooglePlayServiceUtils.getGoogleSignInClient(this)
                .signOut().addOnCompleteListener(task -> userAuthStatusObserver());
    }

    public void setTitleTv(String title) {
        titleTv.setText(title);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            super.onBackPressed();
        }
    }

}
