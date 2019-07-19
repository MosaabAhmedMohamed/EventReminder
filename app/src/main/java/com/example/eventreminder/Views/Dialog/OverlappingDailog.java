package com.example.eventreminder.Views.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventreminder.Models.EventDateTimeModel;
import com.example.eventreminder.R;
import com.example.eventreminder.Util.Constants;
import com.example.eventreminder.Util.OnHandelOverlappingListner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OverlappingDailog extends DialogFragment {
    private static final String TAG = "OverlappingDailog";
    @BindView(R.id.event_1_btn)
    Button event1Btn;
    @BindView(R.id.event_2_btn)
    Button event2Btn;
    @BindView(R.id.discard_btn)
    Button discardBtn;

    private OnHandelOverlappingListner onHandelOverlappingListner;
    private View view;
    ArrayList<EventDateTimeModel> eventDateTimeModels;
    private int eventPosInList;


    public static OverlappingDailog newInstance(ArrayList<EventDateTimeModel> eventDateTimeModels, int position) {
        OverlappingDailog overlappingDailog = new OverlappingDailog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.EVENT_ONE, eventDateTimeModels);
        args.putInt("pos", position);
        overlappingDailog.setArguments(args);
        return overlappingDailog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.select_event_dialog, container, false);
        ButterKnife.bind(this, view);
        init();

        return view;
    }

    private void init() {
        if (getDialog().getWindow() != null && isAdded())
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (getArguments() != null) {
            eventDateTimeModels = getArguments().getParcelableArrayList(Constants.EVENT_ONE);
            eventPosInList = getArguments().getInt("pos");
            event1Btn.setText(eventDateTimeModels.get(0).getEvent().getSummary());
            event2Btn.setText(eventDateTimeModels.get(1).getEvent().getSummary());
        }

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onHandelOverlappingListner = (OnHandelOverlappingListner) getTargetFragment();
        } catch (ClassCastException c) {
            c.getMessage();
        }
    }

    @OnClick({R.id.event_1_btn, R.id.event_2_btn, R.id.discard_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.event_1_btn:
                 getDialog().dismiss();
                onHandelOverlappingListner.onHandel(true, eventDateTimeModels.get(0), eventPosInList);
                break;
            case R.id.event_2_btn:
                getDialog().dismiss();
                onHandelOverlappingListner.onHandel(true, eventDateTimeModels.get(1), 0);
                break;
            case R.id.discard_btn:
                getDialog().dismiss();
                onHandelOverlappingListner.onHandel(false, eventDateTimeModels.get(0), eventPosInList);
                break;
        }
    }
}
