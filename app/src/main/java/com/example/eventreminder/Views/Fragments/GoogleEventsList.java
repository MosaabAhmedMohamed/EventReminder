package com.example.eventreminder.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventreminder.BaseViews.BaseFragment;
import com.example.eventreminder.R;

public class GoogleEventsList extends BaseFragment {

    private View view;

    public static GoogleEventsList newInstance() {
        return new GoogleEventsList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null)
        {
            view = inflater.inflate(R.layout.events_list,container,false);

        }
        return view;
    }
}
