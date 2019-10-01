package com.example.eventreminder.refactoring.ui.home.city;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventreminder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventCityDialog extends DialogFragment {

    private OnCitySelectedListner onCitySelectedListner;

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.name_edt)
    EditText nameEdt;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;
    @BindView(R.id.discard_btn)
    Button discardBtn;


    public static EventCityDialog newInstance(String cityName) {
        EventCityDialog eventCityDialog = new EventCityDialog();
        if (cityName != null) {
            Bundle args = new Bundle();
            args.putString("USER_CITY_KEY", cityName);
            eventCityDialog.setArguments(args);
        }
        return eventCityDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onCitySelectedListner = (OnCitySelectedListner) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.set_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        if (getDialog() != null && getDialog().getWindow() != null && isAdded())
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (getArguments() != null && getArguments().containsKey("USER_CITY_KEY") && getArguments().getString("USER_CITY_KEY") != null) {
            nameEdt.setText(getArguments().getString("USER_CITY_KEY"));
        }
    }

    @OnClick({R.id.confirm_btn, R.id.discard_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                if (validateCityName()) {
                    onCitySelectedListner.onSelected(nameEdt.getText().toString());
                    dismiss();
                }
                break;
            case R.id.discard_btn:
                dismiss();
                break;
        }
    }

    private boolean validateCityName() {
        if (TextUtils.isEmpty(nameEdt.getText().toString())) {
            Toast.makeText(getActivity(), "Enter your city name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
