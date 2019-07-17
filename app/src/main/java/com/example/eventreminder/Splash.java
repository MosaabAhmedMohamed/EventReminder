package com.example.eventreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.example.eventreminder.Activites.Home;
import com.example.eventreminder.Activites.Login;
import com.example.eventreminder.BaseViews.BaseActivity;

public class Splash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        init();
    }

    private void init() {

        startProgressAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.contains("login") && sharedPreferences.getBoolean("login", true)) {
                    startActivity(new Intent(Splash.this, Home.class));

                } else {
                    startActivity(new Intent(Splash.this, Login.class));

                }
                finish();
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

}
