package com.example.eventreminder.BaseViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventreminder.R;
public class BaseFragment extends Fragment {

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

}