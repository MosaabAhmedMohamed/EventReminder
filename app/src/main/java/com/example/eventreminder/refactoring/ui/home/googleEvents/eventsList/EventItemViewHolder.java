package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.data.models.GoogleEventsAndForecastModel;
import com.example.eventreminder.refactoring.data.models.MainEntity;
import com.example.eventreminder.refactoring.ui.base.BaseViewHolder;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.OnEventActionLIstner;
import com.example.eventreminder.refactoring.util.Constants;
import com.example.eventreminder.refactoring.util.DateTimeUtils;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventItemViewHolder extends BaseViewHolder implements View.OnClickListener {

    private Button acceptBtn, rejectBtn;
    private OnEventActionLIstner onEventActionLIstner;
    private GoogleEventsAndForecastModel eventsAndForecastModel;

    private String eventFormattedDate, startEventFormattedTime, endEventFormattedTime, loggedInUserEmail;
    private long eventDateTime;

    private RequestManager requestManager;
    private Resources res;

    @BindView(R.id.enent_icon)
    protected ImageView enentIcon;
    @BindView(R.id.event_detial_tv)
    protected TextView eventDetialTv;
    @BindView(R.id.event_date_tv)
    protected TextView eventDateTv;
    @BindView(R.id.event_time_tv)
    protected TextView eventTimeTv;
    @BindView(R.id.weather_image_view)
    protected ImageView weatherImageView;
    @BindView(R.id.weather_tv)
    protected TextView tempTv;
    @BindView(R.id.humidity_tv)
    protected TextView humidityTv;
    @BindView(R.id.view2)
    protected View view2;
    @BindView(R.id.weather_stauts_tv)
    protected TextView weatherStautsTv;
    @BindView(R.id.event_status_color)
    protected View eventStatusColor;
    @BindView(R.id.event_creator_tv)
    protected TextView eventCreatorTv;

    public EventItemViewHolder(View itemView, OnEventActionLIstner onEventActionLIstner, RequestManager glide, String loggedInUserEmail) {
        super(itemView);
        this.onEventActionLIstner = onEventActionLIstner;
        this.requestManager = glide;
        this.loggedInUserEmail = loggedInUserEmail;
        res = itemView.getResources();
        ButterKnife.bind(this, itemView);

        acceptBtn = itemView.findViewById(R.id.accept_btn);
        rejectBtn = itemView.findViewById(R.id.reject_btn);

        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
    }

    @Override
    public void onBind(int position, Object o) {
        if (o instanceof GoogleEventsAndForecastModel) {
            eventsAndForecastModel = (GoogleEventsAndForecastModel) o;
            Event event = eventsAndForecastModel.getEventsModels().get(position);
            initDateTimeFromat();
            if (!event.getCreator().getEmail().equals(loggedInUserEmail) && event.getAttendees() != null)
                checkEventInvitationStatus(event.getAttendees());

            if (event.getStart().getDateTime() == null && event.getStart().getDate() == null) {
                setVisibility(false);
                return;
            } else if (event.getStart().getDateTime() != null &&
                    event.getStart().getDateTime().getValue() != 0 &&
                    event.getEnd().getDateTime().getValue() != 0) {

                eventDateTime = event.getStart().getDateTime().getValue();
                eventFormattedDate = DateTimeUtils.getFormattedDate(event.getStart().getDateTime().getValue());

                startEventFormattedTime = DateTimeUtils.getFormattedTime(event.getStart().getDateTime().getValue());
                endEventFormattedTime = DateTimeUtils.getFormattedTime(event.getEnd().getDateTime().getValue());
                setEventData(startEventFormattedTime.concat(" : " + endEventFormattedTime), res.getString(R.string.Details).concat(event.getSummary()), res.getString(R.string.date).concat(eventFormattedDate));
                setWeatherData(Constants.getInstance().getClosestTimeUnix(eventsAndForecastModel.getForecastModels().keySet(), eventDateTime));
            } else if (event.getStart().getDate() != null && event.getStart().getDate().getValue() != 0) {
                eventFormattedDate = DateTimeUtils.getFormattedDate(event.getStart().getDate().getValue());
                setEventData("no time for this event", res.getString(R.string.Details).concat(event.getSummary()), res.getString(R.string.date).concat(eventFormattedDate));
                setWeatherData(Constants.getInstance().getClosestTimeUnix(eventsAndForecastModel.getForecastModels().keySet(), eventDateTime));
            }
            if (event.getCreator() != null && event.getCreator().getEmail() != null)
                eventCreatorTv.setText("Event creator : ".concat(event.getCreator().getEmail()));
            else
                eventCreatorTv.setVisibility(View.GONE);
        }
    }

    private void initDateTimeFromat() {
        eventFormattedDate = null;
        startEventFormattedTime = null;
        endEventFormattedTime = null;
        eventDateTime = 0;

    }

    private void checkEventInvitationStatus(List<EventAttendee> attendees) {
        for (int i = 0; i < attendees.size(); i++) {
            if (attendees.get(i).getEmail().equals(loggedInUserEmail) && !attendees.get(i).getResponseStatus().equals("accepted")) {
                eventStatusColor.setBackground(itemView.getContext().getDrawable(R.drawable.yeallow_circle_background_shape));
                break;
            }
        }
    }

    private void setVisibility(boolean isVisible) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        if (isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }


    private void setEventData(String time_event, String eventDetial, String date) {
        eventTimeTv.setText(time_event);
        eventDetialTv.setText(eventDetial);
        eventDateTv.setText(date);
    }

    private void setWeatherData(int eventDateTime) {
        if (eventDateTime != 0 && eventsAndForecastModel.getForecastModels() != null && eventsAndForecastModel.getForecastModels().containsKey(eventDateTime)) {
            MainEntity mainEntity = eventsAndForecastModel.getForecastModels().get(eventDateTime).getMain();
            humidityTv.setText("Humidity : ".concat(String.valueOf(mainEntity.getHumidity())));
            tempTv.setText("Temperature : ".concat(String.valueOf(mainEntity.getTemp())));
            weatherStautsTv.setText(eventsAndForecastModel.getForecastModels().get(eventDateTime).getWeather().get(0).getDescription());
            requestManager.load(Constants.getInstance().OpenWeatherMapSotrageUrl.concat(eventsAndForecastModel.getForecastModels().get(eventDateTime).getWeather().get(0).getIcon().concat(".png")))
                    .into(weatherImageView);
        } else {
            humidityTv.setVisibility(View.GONE);
            tempTv.setVisibility(View.GONE);
            weatherImageView.setVisibility(View.GONE);
            weatherStautsTv.setText("sorry we can't find weather for this event");
        }
    }


    @Override
    public void onClick(View v) {
        if (onEventActionLIstner != null)
            if (v.getId() == acceptBtn.getId()) {
                onEventActionLIstner.onAcceptEvent(getAdapterPosition());

            } else if (v.getId() == rejectBtn.getId())
                onEventActionLIstner.onDeleteEvent(eventsAndForecastModel.getEventsModels().get(getAdapterPosition()).getId());
    }

}
