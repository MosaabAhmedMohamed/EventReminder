package com.example.eventreminder.refactoring.ui.home.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.data.models.User;
import com.example.eventreminder.refactoring.ui.auth.AuthActivity;
import com.example.eventreminder.refactoring.ui.auth.AuthResource;
import com.example.eventreminder.refactoring.ui.base.BaseFragment;
import com.example.eventreminder.refactoring.ui.base.ViewModelProviderFactory;
import com.example.eventreminder.refactoring.ui.home.HomeActivity;
import com.example.eventreminder.refactoring.util.CircularImageView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfile extends BaseFragment {
    private static final String TAG = "UserProfile";
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

        observUserProfile();
    }

    private void observUserProfile() {
        profileVM.getAuthUser().observe(getViewLifecycleOwner(), new Observer<AuthResource<User>>() {
            @Override
            public void onChanged(AuthResource<User> userAuthResource) {
                if (userAuthResource != null) {
                    switch (userAuthResource.status) {
                        case AUTHENTICATED: {
                            if (userAuthResource.data != null) {
                                setUserData(userAuthResource.data);
                            }
                            Log.d(TAG, "onChanged: 1");
                        }
                        case NOT_AUTHENTICATED: {
                            startActivity(new Intent(getBaseActivity(), AuthActivity.class));
                            getBaseActivity().finish();
                            Log.d(TAG, "onChanged: 2");
                        }
                    }
                }
            }
        });
    }

    private void setUserData(User user) {
        emailEdt.setText(user.getAccount());
        fristNameEdt.setText(user.getName());
    }
}
