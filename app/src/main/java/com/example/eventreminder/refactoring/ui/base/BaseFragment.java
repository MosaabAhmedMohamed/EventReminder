package com.example.eventreminder.refactoring.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.android.support.DaggerFragment;

public abstract class BaseFragment extends DaggerFragment {
    private static final String TAG = "BaseFragment";
    private BaseActivity mActivity;
    private View mRootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public View getmRootView() {
        return mRootView;
    }

    public boolean isNetworkConnected() {
        return mActivity != null && mActivity.isNetworkConnected();
    }

    public void setLoadingStatus(boolean visibility) {
        if (mActivity != null) {
            if (visibility)
                mActivity.showLoading();
            else
                mActivity.hideLoading();
        }

    }

    protected void navLoginScreen() {
        if (mActivity != null)
            mActivity.navLoginScreen();
    }

    public void showSnackBar(String message) {
        mActivity.showSnackBar(message);
    }

}
