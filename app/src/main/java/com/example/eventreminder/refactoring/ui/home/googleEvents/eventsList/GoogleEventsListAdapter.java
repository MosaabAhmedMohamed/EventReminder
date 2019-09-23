package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;
import com.example.eventreminder.refactoring.data.models.GoogleEventsAndForecastModel;
import com.example.eventreminder.refactoring.data.models.MainEntity;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.util.Constants;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleEventsListAdapter extends RecyclerView.Adapter<GoogleEventsListAdapter.EventItemViewHolder> {

    private static final String TAG = "GoogleEventsListAdapter";


    private OnEventActionLIstner onEventActionLIstner;
    private GoogleEventsAndForecastModel googleEventsAndForecastModel;

    private String eventFormattedDate, startEventFormattedTime, endEventFormattedTime, loggedInUserEmail;
    private long eventDateTime;
    private boolean isDealingWithOverLap = false;

    private RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.drawable.ic_launcher_background);

    public void setOnEventActionLIstner(OnEventActionLIstner onEventActionLIstner) {
        this.onEventActionLIstner = onEventActionLIstner;
    }

    public void setGoogleEventsAndForecastModel(GoogleEventsAndForecastModel googleEventsAndForecastModel) {
        this.googleEventsAndForecastModel = googleEventsAndForecastModel;
    }

    public void setLoggedInUserEmail(String loggedInUserEmail) {
        this.loggedInUserEmail = loggedInUserEmail;
    }

    @NonNull
    @Override
    public EventItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventItemViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Event event = googleEventsAndForecastModel.getEventsModels().get(position);
        if (event != null) {
            eventFormattedDate = null;
            startEventFormattedTime = null;
            endEventFormattedTime = null;
            eventDateTime = 0;

            if (!event.getCreator().getEmail().equals(loggedInUserEmail) && event.getAttendees() != null)
                checkEventInvitationStatus(holder, event.getAttendees());

            if (event.getStart().getDateTime() == null && event.getStart().getDate() == null) {
                setVisibility(false, holder.itemView);
                return;
            } else if (event.getStart().getDateTime() != null &&
                    event.getStart().getDateTime().getValue() != 0 &&
                    event.getEnd().getDateTime().getValue() != 0) {

                eventDateTime = event.getStart().getDateTime().getValue();
                eventFormattedDate = Constants.getInstance().getFormattedDate(event.getStart().getDateTime().getValue());

                if (!isDealingWithOverLap)
                    cheekForEventOverlapping(eventFormattedDate, event, position);

                startEventFormattedTime = Constants.getInstance().getFormattedTime(event.getStart().getDateTime().getValue());
                endEventFormattedTime = Constants.getInstance().getFormattedTime(event.getEnd().getDateTime().getValue());
                holder.eventTimeTv.setText("Start time : ".concat(startEventFormattedTime).concat("  ").concat("End Time : ").concat(endEventFormattedTime));
                setWeatherData(holder, Constants.getInstance().getClosestTimeUnix(googleEventsAndForecastModel.getForecastModels().keySet(), eventDateTime));
                holder.eventDetialTv.setText(holder.itemView.getContext().getResources().getString(R.string.Details).concat(event.getSummary()));
                holder.eventDateTv.setText(holder.itemView.getContext().getResources().getString(R.string.date).concat(eventFormattedDate));

            } else if (event.getStart().getDate() != null && event.getStart().getDate().getValue() != 0) {
                eventFormattedDate = Constants.getInstance().getFormattedDate(event.getStart().getDate().getValue());
                holder.eventTimeTv.setText("no time for this event");
                holder.eventDetialTv.setText(holder.itemView.getContext().getResources().getString(R.string.Details).concat(event.getSummary()));
                holder.eventDateTv.setText(holder.itemView.getContext().getResources().getString(R.string.date).concat(eventFormattedDate));
                setWeatherData(holder, Constants.getInstance().getClosestTimeUnix(googleEventsAndForecastModel.getForecastModels().keySet(), eventDateTime));
            }
            if (event.getCreator() != null && event.getCreator().getEmail() != null)
                holder.eventCreatorTv.setText("Event creator : ".concat(event.getCreator().getEmail()));
            else
                holder.eventCreatorTv.setVisibility(View.GONE);
        }
    }

    private void cheekForEventOverlapping(String eventFormattedDate, Event event, int position) {
        for (EventDateTimeModel eventDateTimeModel : googleEventsAndForecastModel.getEventDateTimeModels()) {
            if (eventDateTimeModel.getDay().equals(eventFormattedDate) &&
                    !eventDateTimeModel.getEvent().getId().equals(event.getId()) &&
                    !eventDateTimeModel.isDiscard()) {

                int startTimeEventInList = Constants.getInstance().convertUnixToSeconds(event.getStart().getDateTime().getValue());
                int endTimeEventInList = Constants.getInstance().convertUnixToSeconds(event.getEnd().getDateTime().getValue());
                if (startTimeEventInList <= eventDateTimeModel.getStartTime()) {
                    if (startTimeEventInList <= eventDateTimeModel.getEndTime() || startTimeEventInList >= eventDateTimeModel.getEndTime()) {
                        onEventActionLIstner.onEventOverlapped(new EventDateTimeModel(eventFormattedDate,
                                startTimeEventInList, endTimeEventInList, event), eventDateTimeModel, position);
                        isDealingWithOverLap = true;
                        eventDateTimeModel.setDiscard(true);
                        break;
                    }
                }
            }
        }
    }


    private void checkEventInvitationStatus(EventItemViewHolder holder, List<EventAttendee> attendees) {
        for (int i = 0; i < attendees.size(); i++) {
            if (attendees.get(i).getEmail().equals(loggedInUserEmail) && !attendees.get(i).getResponseStatus().equals("accepted")) {
                holder.eventStatusColor.setBackground(holder.itemView.getContext().getDrawable(R.drawable.yeallow_circle_background_shape));
                break;
            }
        }
    }

    private void setWeatherData(EventItemViewHolder holder, int eventDateTime) {
        if (eventDateTime != 0 && googleEventsAndForecastModel.getForecastModels() != null && googleEventsAndForecastModel.getForecastModels().containsKey(eventDateTime)) {
            MainEntity mainEntity = googleEventsAndForecastModel.getForecastModels().get(eventDateTime).getMain();

            holder.humidityTv.setText("Humidity : ".concat(String.valueOf(mainEntity.getHumidity())));
            holder.tempTv.setText("Temperature : ".concat(String.valueOf(mainEntity.getTemp())));
            holder.weatherStautsTv.setText(googleEventsAndForecastModel.getForecastModels().get(eventDateTime).getWeather().get(0).getDescription());
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(Constants.getInstance().OpenWeatherMapSotrageUrl.concat(googleEventsAndForecastModel.getForecastModels().get(eventDateTime).getWeather().get(0).getIcon().concat(".png")))
                    .into(holder.weatherImageView);
        } else {
            holder.humidityTv.setVisibility(View.GONE);
            holder.tempTv.setVisibility(View.GONE);
            holder.weatherImageView.setVisibility(View.GONE);
            holder.weatherStautsTv.setText("sorry we can't find weather for this event");
        }
    }

    @Override
    public int getItemCount() {
        if (googleEventsAndForecastModel.getEventsModels() != null && googleEventsAndForecastModel.getEventsModels().size() > 0)
            return googleEventsAndForecastModel.getEventsModels().size();
        return 0;
    }

    public void setUpdatedEvents(List<Event> events) {
        googleEventsAndForecastModel.getEventsModels().clear();
        googleEventsAndForecastModel.setEventsModels(events);
        notifyDataSetChanged();
    }

    public void setVisibility(boolean isVisible, View itemView) {
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

    public void notifyEventDealingWithOvenLapping() {
        isDealingWithOverLap = false;
    }

    public class EventItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button acceptBtn, rejectBtn;

        @BindView(R.id.enent_icon)
        ImageView enentIcon;
        @BindView(R.id.event_detial_tv)
        TextView eventDetialTv;
        @BindView(R.id.event_date_tv)
        TextView eventDateTv;
        @BindView(R.id.event_time_tv)
        TextView eventTimeTv;
        @BindView(R.id.weather_image_view)
        ImageView weatherImageView;
        @BindView(R.id.weather_tv)
        TextView tempTv;
        @BindView(R.id.humidity_tv)
        TextView humidityTv;
        @BindView(R.id.view2)
        View view2;
        @BindView(R.id.weather_stauts_tv)
        TextView weatherStautsTv;
        @BindView(R.id.event_status_color)
        View eventStatusColor;
        @BindView(R.id.event_creator_tv)
        TextView eventCreatorTv;

        private EventItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            acceptBtn = itemView.findViewById(R.id.accept_btn);
            rejectBtn = itemView.findViewById(R.id.reject_btn);

            acceptBtn.setOnClickListener(this);
            rejectBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onEventActionLIstner != null)
                if (v.getId() == acceptBtn.getId()) {
                    onEventActionLIstner.onAcceptEvent(getAdapterPosition());

                } else if (v.getId() == rejectBtn.getId())
                    onEventActionLIstner.onDeleteEvent(googleEventsAndForecastModel.getEventsModels().get(getAdapterPosition()).getId());
        }
    }
}
