package com.example.eventreminder.Views.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

import com.example.eventreminder.BaseViews.BaseFragment;
import com.example.eventreminder.Models.EventDateTimeModel;
import com.example.eventreminder.R;
import com.example.eventreminder.Util.Constants;
import com.example.eventreminder.Views.Activites.Home;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RescheduleOverlappedEvent extends BaseFragment {
    private static final String TAG = "ReschudleOverlappedEven";

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

    private Context context;
    private int mYear, mMonth, mDay;
    private int mHour, mMinute;
    private String date, time, recipient;

    public static RescheduleOverlappedEvent newInstance(ArrayList<EventDateTimeModel> eventDateTimeModels, EventDateTimeModel selectedEventDateTimeModel) {
        RescheduleOverlappedEvent rescheduleOverlappedEvent = new RescheduleOverlappedEvent();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.EVENTS_MODEL, eventDateTimeModels);
        args.putParcelable(Constants.EVENT_SELECTED_TO_EDIT, selectedEventDateTimeModel);
        rescheduleOverlappedEvent.setArguments(args);
        return rescheduleOverlappedEvent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.reschdule_event, container, false);
            ButterKnife.bind(this, view);
            if (!Constants.getInstance().isDeviceOnline(getActivity()))
                checkInternetSnackbar();
            else
                init();

        }
        return view;
    }

    private void init() {
        if (getActivity() != null && isAdded())
            context = getActivity();
        ((Home) context).setTitleTv("Reschedule overlapped events");
        if (getArguments() != null) {
            eventDateTimeModels = getArguments().getParcelableArrayList(Constants.EVENTS_MODEL);
            selectedEventDateTimeModel = getArguments().getParcelable(Constants.EVENT_SELECTED_TO_EDIT);
        }

        descTv.setText(getResources().getString(R.string.to_be_reschduled)
                .concat(selectedEventDateTimeModel.getDay()
                        .concat(" " + ConvertFromSecondsToTime(selectedEventDateTimeModel.getStartTime()))));
        recipient = selectedEventDateTimeModel.getEvent().getCreator().getEmail();
        date = selectedEventDateTimeModel.getDay();
        dateTv.setText(date);
        confirmTv.setText(getResources().getString(R.string.Do_You_want_to_send_email_to).concat(" " + recipient));

        int startEndTimeDiff = selectedEventDateTimeModel.getEndTime() - selectedEventDateTimeModel.getStartTime();
    }

    private void checkInternetSnackbar() {
        if (getActivity() != null && isAdded())
            ((Home) getActivity()).showIsOfflineSnackbar();
    }

    @OnClick({R.id.date_tv, R.id.time_tv, R.id.confirm_tv, R.id.confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.date_tv:
                datePicker();
                break;
            case R.id.time_tv:
                timePicker();
                break;
            case R.id.confirm_btn:
                validateTimeDate();
                break;
        }
    }

    private void validateTimeDate() {
        if (!Constants.getInstance().isDeviceOnline(context)) {
            checkInternetSnackbar();
        } else {
            if (time == null)
                Toast.makeText(context, "Pleas select time", Toast.LENGTH_SHORT).show();
            else if (date == null)
                Toast.makeText(context, "Pleas select date", Toast.LENGTH_SHORT).show();
            else
                sendMail();
        }
    }

    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                dateTv.setText(getResources().getString(R.string.Date).concat(" : ").concat(date));
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        time = hourOfDay + " : " + minute;
                        if (!cheekForFreeTime(convertTimeToSeconds(minute, hourOfDay))) {
                            Toast.makeText(context, "this time is not available", Toast.LENGTH_SHORT).show();
                            timePicker();
                        } else {
                            //time_tv.setText(getResources().getString(R.string.Time).concat(" : ").concat(time));
                            Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private int convertTimeToSeconds(int minute, int hour) {
        return (minute * 60) + (hour * 3600);
    }


    private boolean cheekForFreeTime(int eventStartTimeInSeconds) {
        for (EventDateTimeModel eventDateTimeModel : eventDateTimeModels) {
            if (date.equals(eventDateTimeModel.getDay()) && eventStartTimeInSeconds >= eventDateTimeModel.getStartTime() ||
                    eventStartTimeInSeconds <= eventDateTimeModel.getEndTime()) {
                return false;
            } else
                return true;
        }
        return true;
    }

    private void sendMail() {
        String[] recipients = new String[2];
        recipients[0] = recipient;
        String subject = "Rescheduling event time ";
        String message = "Rescheduling event ".concat(selectedEventDateTimeModel.getEvent().getSummary() + " ")
                .concat(selectedEventDateTimeModel.getDay().concat(" ")
                        .concat(ConvertFromSecondsToTime(selectedEventDateTimeModel.getStartTime()))
                        .concat(" To : ").concat("date : " + date + " ").concat("Time : " + time));

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    private String ConvertFromSecondsToTime(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        return String.format("%02d:%02d:", hours, minutes);
    }
}
