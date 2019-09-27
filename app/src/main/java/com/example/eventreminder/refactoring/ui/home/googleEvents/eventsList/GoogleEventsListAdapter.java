package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;
import com.example.eventreminder.refactoring.data.models.GoogleEventsAndForecastModel;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.ui.base.BaseViewHolder;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.EventsUtils;
import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.OnEventActionLIstner;
import com.example.eventreminder.refactoring.util.DateTimeUtils;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import javax.inject.Inject;

public class GoogleEventsListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "GoogleEventsListAdapter";


    private OnEventActionLIstner onEventActionLIstner;
    private GoogleEventsAndForecastModel googleEventsAndForecastModel;

    private String loggedInUserEmail;
    private boolean isDealingWithOverLap = false;

    private RequestManager requestManager;


    @Inject
    public GoogleEventsListAdapter(RequestManager manager) {
        this.requestManager = manager;
    }

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
        return new EventItemViewHolder(view, onEventActionLIstner, requestManager, loggedInUserEmail);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Event event = googleEventsAndForecastModel.getEventsModels().get(position);
        holder.onBind(position, googleEventsAndForecastModel);
        if (event != null && event.getStart().getDateTime() != null &&
                event.getStart().getDateTime().getValue() != 0 &&
                event.getEnd().getDateTime().getValue() != 0) {
            if (!isDealingWithOverLap) {
                String eventFormattedDate = DateTimeUtils.getFormattedDate(event.getStart().getDateTime().getValue());
                isDealingWithOverLap = EventsUtils.cheekForEventOverlapping(eventFormattedDate, event, position,
                        googleEventsAndForecastModel.getEventDateTimeModels(), onEventActionLIstner);
            }
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

    public void notifyEventDealingWithOvenLapping() {
        isDealingWithOverLap = false;
    }


}
