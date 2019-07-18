package com.example.eventreminder.Views;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.eventreminder.Models.GoogleEventsAndForecastModel;
import com.example.eventreminder.Models.MainEntity;
import com.example.eventreminder.R;
import com.example.eventreminder.Util.Constants;
import com.example.eventreminder.Util.OnEventActionLIstner;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleEventsListAdapter extends RecyclerView.Adapter<GoogleEventsListAdapter.EventItemViewHolder> {

    private static final String TAG = "GoogleEventsListAdapter";

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
    TextView weatherTv;
    @BindView(R.id.humidity_tv)
    TextView humidityTv;
    private OnEventActionLIstner onEventActionLIstner;
    private GoogleEventsAndForecastModel googleEventsAndForecastModel;

    private String eventFromatedDate, startEventFromatedTime, endEventFromatedTime;

    private RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.drawable.ic_launcher_background);

    public GoogleEventsListAdapter(OnEventActionLIstner onEventActionLIstner, GoogleEventsAndForecastModel googleEventsAndForecastModel) {
        this.onEventActionLIstner = onEventActionLIstner;
        this.googleEventsAndForecastModel = googleEventsAndForecastModel;
    }

    @NonNull
    @Override
    public EventItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        ButterKnife.bind(this, view);
        return new EventItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventItemViewHolder holder, int position) {
        Event event = googleEventsAndForecastModel.getEventsModels().get(position);
        if (event != null ) {
            if (event.getStart().getDateTime() == null && event.getStart().getDate() == null)
                return;
            else if (event.getStart().getDateTime() != null){
                eventFromatedDate = Constants.getInstance().getFormattedDate(event.getStart().getDateTime().getValue());
                startEventFromatedTime = Constants.getInstance().getFormattedTime(event.getStart().getDateTime().getValue());
                endEventFromatedTime = Constants.getInstance().getFormattedTime(event.getEnd().getDateTime().getValue());
                eventTimeTv.append(startEventFromatedTime.concat("  ").concat("End Time : ").concat(endEventFromatedTime));
            }
            else
            {

                eventFromatedDate = Constants.getInstance().getFormattedDate(event.getStart().getDate().getValue());
               // startEventFromatedTime = Constants.getInstance().getFormattedTime(event.getStart().getDateTime().getValue());
               // endEventFromatedTime = Constants.getInstance().getFormattedTime(event.getEnd().getDateTime().getValue());
                eventTimeTv.setText("no time for this event");
            }


            setWeatherData(holder, eventFromatedDate);

            eventDetialTv.append(event.getSummary());
            eventDateTv.append(eventFromatedDate);

        }
    }

    private void setWeatherData(EventItemViewHolder holder, String eventFromatedDate) {
        if (googleEventsAndForecastModel.getForecastModels() != null && googleEventsAndForecastModel.getForecastModels().containsKey(eventFromatedDate)) {
            MainEntity mainEntity = googleEventsAndForecastModel.getForecastModels().get(eventFromatedDate).getMain();

            humidityTv.setText(String.valueOf(mainEntity.getHumidity()));
            weatherTv.setText(String.valueOf(mainEntity.getTemp()));

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(Constants.getInstance().OpenWeatherMapSotrageUrl.concat(googleEventsAndForecastModel.getForecastModels().get(eventFromatedDate).getWeather().get(0).getIcon().concat(".png")))
                    .into(weatherImageView);

        }
    }

    @Override
    public int getItemCount() {
        if (googleEventsAndForecastModel.getEventsModels() != null && googleEventsAndForecastModel.getEventsModels().size() > 0)
            return googleEventsAndForecastModel.getEventsModels().size();
        return 0;
    }

    public void setUpdatedEvents(List<Event> events) {
        googleEventsAndForecastModel.setEventsModels(events);
        notifyDataSetChanged();
    }

    public class EventItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private Button acceptBtn, rejectBtn;

        public EventItemViewHolder(@NonNull View itemView) {
            super(itemView);

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
