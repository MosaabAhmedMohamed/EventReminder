package com.example.eventreminder.refactoring.ui.splash;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import androidx.lifecycle.ViewModelProviders;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.ui.base.BaseActivity;
import com.example.eventreminder.refactoring.ui.base.ViewModelProviderFactory;

import javax.inject.Inject;


public class SplashActivity extends BaseActivity {

    @Inject
    ViewModelProviderFactory providerFactory;

    private SplashVM splashVM;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        splashVM = ViewModelProviders.of(this, providerFactory).get(SplashVM.class);
        init();
    }

    private void init() {
        startProgressAnimation();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashVM.checkForSignIn();
                userAuthStatusObserver();
            }
        }, 2800);
    }

    private void startProgressAnimation() {
        ProgressBar mProgressBar = findViewById(R.id.splash_pb_loading);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 100);
        progressAnimator.setDuration(2800);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
