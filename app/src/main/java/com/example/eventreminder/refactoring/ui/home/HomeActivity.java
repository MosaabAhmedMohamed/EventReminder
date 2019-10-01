package com.example.eventreminder.refactoring.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.ui.base.BaseActivity;
import com.example.eventreminder.refactoring.ui.base.ViewModelProviderFactory;
import com.example.eventreminder.refactoring.ui.home.city.EventCityDialog;
import com.example.eventreminder.refactoring.ui.home.city.OnCitySelectedListner;
import com.example.eventreminder.refactoring.util.GooglePlayServiceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends BaseActivity implements OnCitySelectedListner {
    private static final String TAG = "HomeActivity";

    @Inject
    ViewModelProviderFactory providerFactory;
    HomeVM homeVM;

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.out_from_google_btn)
    Button outFromGooglBtn;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.user_profile_btn)
    Button userProfileBtn;
    @BindView(R.id.city_btn)
    Button cityBtn;
    @BindView(R.id.events_list_btn)
    Button eventsListBtn;

    private NavOptions navOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRootView(findViewById(R.id.container));
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        homeVM = ViewModelProviders.of(this, providerFactory).get(HomeVM.class);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navOptions = new NavOptions.Builder().setPopUpTo(R.id.home, true).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
    }

    private void signOutFromGoogle() {
        GooglePlayServiceUtils.getGoogleSignInClient(this)
                .signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                navLoginScreen();
                sessionManager.logOut();
            }
        });
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

    @OnClick({R.id.user_profile_btn, R.id.events_list_btn, R.id.menu, R.id.out_from_google_btn, R.id.city_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.out_from_google_btn:
                signOutFromGoogle();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.user_profile_btn:
                drawerLayout.closeDrawer(GravityCompat.START);
                if (isValidateDestination(R.id.profile)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.profile);
                }
                break;
            case R.id.events_list_btn:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.eventList, null, navOptions);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.city_btn:
                showCityDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
    }

    private boolean isValidateDestination(int destination) {
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId();
    }

    private void showCityDialog() {
        EventCityDialog cityDialog = EventCityDialog.newInstance(homeVM.getCityName());
        cityDialog.show(getSupportFragmentManager(), "cityDialog");
    }

    @Override
    public void onSelected(String cityName) {
        homeVM.setCityName(cityName);
        Log.d(TAG, "onSelected: " + cityName);
    }
}
