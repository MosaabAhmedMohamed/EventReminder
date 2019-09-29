package com.example.eventreminder.refactoring.ui.home.profile;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.RequestManager;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.ui.base.BaseFragment;
import com.example.eventreminder.refactoring.ui.base.ViewModelProviderFactory;
import com.example.eventreminder.refactoring.ui.home.HomeActivity;
import com.example.eventreminder.refactoring.util.CircularImageView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfile extends BaseFragment {
    private static final String TAG = "UserProfile";

    @Inject
    RequestManager requestManager;
    @Inject
    ViewModelProviderFactory providerFactory;
    private ProfileVM profileVM;

    @BindView(R.id.frist_name_edt)
    EditText fristNameEdt;
    @BindView(R.id.email_edt)
    EditText emailEdt;
    @BindView(R.id.user_image_view)
    CircularImageView userImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) getBaseActivity()).setTitleTv("UserProfile");
        ButterKnife.bind(this, view);
        profileVM = ViewModelProviders.of(this, providerFactory).get(ProfileVM.class);
        init();
    }

    private void init() {

        getUserProfile();
    }

    private void getUserProfile() {
        if (profileVM.getUserProfile() != null) {
            setUserData(profileVM.getUserProfile());
        } else {
            navLoginScreen();
        }
    }


    private void setUserData(GoogleSignInAccount user) {
        try {
            Log.d(TAG, "setUserData: "+user.getPhotoUrl());
            emailEdt.setText(user.getAccount().name);
            fristNameEdt.setText(user.getDisplayName());
            requestManager.load(user.getPhotoUrl()).into(userImageView);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
