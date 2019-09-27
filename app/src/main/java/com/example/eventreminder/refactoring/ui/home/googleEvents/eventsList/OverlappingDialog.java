package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.OnHandelOverlappingListner;
import com.example.eventreminder.refactoring.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OverlappingDialog extends DialogFragment {

    //private static final String TAG = "OverlappingDialog";
    @BindView(R.id.event_1_btn)
    Button event1Btn;
    @BindView(R.id.event_2_btn)
    Button event2Btn;
    @BindView(R.id.discard_btn)
    Button discardBtn;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.desc_tv)
    TextView descTv;
    @BindView(R.id.note_tv)
    TextView noteTv;


    private OnHandelOverlappingListner onHandelOverlappingListner;
    private ArrayList<EventDateTimeModel> eventDateTimeModels;
    private int eventPosInList;
    private boolean isHandleEvent;
    private String userEmail;

    public static OverlappingDialog newInstance(ArrayList<EventDateTimeModel> eventDateTimeModels, int position, String userEmail) {
        OverlappingDialog overlappingDialog = new OverlappingDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.EVENT_ONE, eventDateTimeModels);
        args.putInt("pos", position);
        args.putString("userEmail", userEmail);
        overlappingDialog.setArguments(args);
        return overlappingDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.select_event_dialog, container, false);
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
        if (getArguments() != null) {
            eventDateTimeModels = getArguments().getParcelableArrayList(Constants.EVENT_ONE);
            eventPosInList = getArguments().getInt("pos");
            userEmail = getArguments().getString("userEmail");
            event1Btn.setText(eventDateTimeModels.get(0).getEvent().getSummary());
            event2Btn.setText(eventDateTimeModels.get(1).getEvent().getSummary());
        }

        isHandleEvent = true;
        event1Btn.setText(eventDateTimeModels.get(0).getEvent().getSummary());
        event2Btn.setText(eventDateTimeModels.get(1).getEvent().getSummary());

        if (userEmail.equals(eventDateTimeModels.get(0).getEvent().getCreator().getEmail()) && eventDateTimeModels.get(0).getEvent().getCreator().getEmail().
                equals(eventDateTimeModels.get(1).getEvent().getCreator().getEmail())) {
            isHandleEvent = false;

            descTv.setText("you have two event that overlapped and you'r it's creator ");
            noteTv.setText("Note you can select one to be deleted or just discard");
        }
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onHandelOverlappingListner = (OnHandelOverlappingListner) getTargetFragment();
        } catch (ClassCastException c) {
            c.getMessage();
        }
    }

    @OnClick({R.id.event_1_btn, R.id.event_2_btn, R.id.discard_btn})
    public void onViewClicked(View view) {
        if (getDialog() != null) {
            switch (view.getId()) {
                case R.id.event_1_btn:
                    getDialog().dismiss();
                    onHandelOverlappingListner.onHandel(isHandleEvent, eventDateTimeModels.get(0), eventPosInList, Constants.SELECTED_EVENT_TO_RESCHDULE);
                    break;
                case R.id.event_2_btn:
                    getDialog().dismiss();
                    onHandelOverlappingListner.onHandel(isHandleEvent, eventDateTimeModels.get(1), 0, Constants.SELECTED_EVENT_TO_RESCHDULE);
                    break;
                case R.id.discard_btn:
                    getDialog().dismiss();
                    onHandelOverlappingListner.onHandel(false, eventDateTimeModels.get(0), eventPosInList, 0);
                    break;
            }
        }
    }
}
