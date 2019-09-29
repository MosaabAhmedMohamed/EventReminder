package com.example.eventreminder.refactoring.ui.home.city;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.data.local.PreferencesHelper;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.eventreminder.refactoring.util.Constants.USER_CITY_KEY;

public class EventCityDialog extends DialogFragment {

    @Inject
    PreferencesHelper preferencesHelper;

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.name_edt)
    EditText nameEdt;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;
    @BindView(R.id.discard_btn)
    Button discardBtn;

    public static EventCityDialog newInstance() {
        return new EventCityDialog();
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


        if (preferencesHelper.containKey(USER_CITY_KEY) && preferencesHelper.getString(USER_CITY_KEY) != null) {
            nameEdt.setText(preferencesHelper.getString(USER_CITY_KEY));
        }
    }

    @OnClick({R.id.confirm_btn, R.id.discard_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                break;
            case R.id.discard_btn:
                dismiss();
                break;
        }
    }
}
