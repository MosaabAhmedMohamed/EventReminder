package com.example.eventreminder.refactoring.ui.home.googleEvents.reschduleEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.ui.base.BaseFragment;
import com.example.eventreminder.refactoring.ui.home.HomeActivity;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.EventsUtils;
import com.example.eventreminder.refactoring.util.Constants;
import com.example.eventreminder.refactoring.util.DateTimeUtils;
import com.example.eventreminder.refactoring.util.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RescheduleOverlappedEvent extends BaseFragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    // private static final String TAG = "ReschudleOverlappedEven";

    @BindView(R.id.desc_tv)
    TextView descTv;
    @BindView(R.id.select_option_tv)
    TextView selectOptionTv;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.time_tv)
    TextView time_tv;
    @BindView(R.id.confirm_tv)
    TextView confirmTv;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;


    private View view;
    private EventDateTimeModel selectedEventDateTimeModel;
    private ArrayList<EventDateTimeModel> eventDateTimeModels;
    private String date, time, recipient, startEventFormattedTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.reschdule_event, container, false);
            ButterKnife.bind(this, view);
            init();
        }
        return view;
    }

    private void init() {
        ((HomeActivity) getBaseActivity()).setTitleTv("Reschedule overlapped events");
        if (getArguments() != null) {
            eventDateTimeModels = getArguments().getParcelableArrayList(Constants.EVENTS_MODEL);
            selectedEventDateTimeModel = getArguments().getParcelable(Constants.EVENT_SELECTED_TO_EDIT);
        }

        startEventFormattedTime = DateTimeUtils.getFormattedTime(selectedEventDateTimeModel.getEvent().getStart().getDateTime().getValue());

        descTv.setText(getResources().getString(R.string.to_be_reschduled)
                .concat(selectedEventDateTimeModel.getDay().concat(" " + startEventFormattedTime)));
        recipient = selectedEventDateTimeModel.getEvent().getCreator().getEmail();

        date = selectedEventDateTimeModel.getDay();
        dateTv.setText(date);
        confirmTv.setText(getResources().getString(R.string.Do_You_want_to_send_email_to).concat(" " + recipient));
    }

    @OnClick({R.id.date_tv, R.id.time_tv, R.id.confirm_tv, R.id.confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.date_tv:
                showDatePicker();
                break;
            case R.id.time_tv:
                showTimePicker();
                break;
            case R.id.confirm_btn:
                validateTimeDate();
                break;
        }
    }

    private void validateTimeDate() {
        if (!NetworkUtils.isNetworkConnected(getBaseActivity())) {
            showSnackBar("check you'r internet connection");
        } else {
            if (time == null)
                Toast.makeText(getBaseActivity(), "Pleas select time", Toast.LENGTH_SHORT).show();
            else if (date == null)
                Toast.makeText(getBaseActivity(), "Pleas select date", Toast.LENGTH_SHORT).show();
            else
                sendMail();
        }
    }


    private void sendMail() {
        String[] recipients = new String[2];
        recipients[0] = recipient;
        String subject = "Rescheduling event time ";
        String message = "Rescheduling event : ".concat(selectedEventDateTimeModel.getEvent().getSummary())
                .concat("\n From Date " + selectedEventDateTimeModel.getDay().concat(" time ")
                        .concat(startEventFormattedTime))
                .concat("\nTo : ").concat("date " + date).concat(" time " + time);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    private void showTimePicker() {
        DialogFragment timePicker = new com.example.eventreminder.refactoring.ui.custom.TimePicker();
        timePicker.setTargetFragment(RescheduleOverlappedEvent.this, 1);
        timePicker.show(getParentFragmentManager(), "time picker");
    }

    private void showDatePicker() {
        DialogFragment timePicker = new com.example.eventreminder.refactoring.ui.custom.DatePicker();
        timePicker.setTargetFragment(RescheduleOverlappedEvent.this, 2);
        timePicker.show(getParentFragmentManager(), "date picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time = hourOfDay + " : " + minute;

        if (!EventsUtils.getInstance().cheekForFreeTimeForEvent(DateTimeUtils.getInstance().convertTimeToSeconds(minute, hourOfDay), date, eventDateTimeModels)) {
            Toast.makeText(getBaseActivity(), "this time is not available", Toast.LENGTH_SHORT).show();
            showTimePicker();
        } else {
            time_tv.setText(getResources().getString(R.string.Time).concat(" : ").concat(time));
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth + "-" + (month + 1) + "-" + year;
        dateTv.setText(getResources().getString(R.string.Date).concat(" : ").concat(date));
    }
}
