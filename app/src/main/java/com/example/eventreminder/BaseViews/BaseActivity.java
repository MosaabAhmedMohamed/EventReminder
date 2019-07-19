package com.example.eventreminder.BaseViews;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.eventreminder.R;
import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity {
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    public ProgressBar mProgressBar;

    @Override
    public void setContentView(int layoutResID) {

        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        mProgressBar = constraintLayout.findViewById(R.id.progress_bar);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(constraintLayout);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();
    }


    public void showProgressBar(boolean visibility) {
        mProgressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

    public void showIsOfflineSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_content), "please check you'r internet connection", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        snackbar.setActionTextColor(getColor(R.color.white));
        snackbar.setDuration(5000);
        snackbar.show();
    }
}

