package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.Async;

import android.os.AsyncTask;

import com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.GoogleEventsList;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import static com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList.utils.EventsUtils.getDataFromApi;
import static com.example.eventreminder.refactoring.util.Constants.EVENTS_ERROR;
import static com.example.eventreminder.refactoring.util.Constants.UPDATE_EVENTS;

public class DeleteGoogleEventTask extends AsyncTask<Void, Void, List<Event>> {
    //private static final String TAG = "DeleteGoogleEventTask";
    private Calendar googleCalendar;
    private Exception mLastError = null;
    private String eventId;

    private WeakReference<GoogleEventsList> googleEventsListWeakReference;

    public DeleteGoogleEventTask(GoogleEventsList googleEventsList, Calendar calendar, String eventId) {
        googleEventsListWeakReference = new WeakReference<>(googleEventsList);
        googleCalendar = calendar;
        this.eventId = eventId;
    }

    @Override
    protected List<Event> doInBackground(Void... params) {
        try {
            googleCalendar.events().delete("primary", eventId).execute();
            // Log.d(TAG, "doInBackground: ");
        } catch (IOException e) {
            e.printStackTrace();
            //  Log.d(TAG, "doInBackground: "+e.getMessage());
        }
        try {
            return getDataFromApi(googleCalendar);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (getGoogleEventsListRefrence() != null) {
            getGoogleEventsListRefrence().setLoadingStatus(true);
        }
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        super.onPostExecute(events);
        if (getGoogleEventsListRefrence() != null) {
            getGoogleEventsListRefrence().setLoadingStatus(false);
            if (events == null || events.size() == 0) {
                try {
                    if (getGoogleEventsListRefrence() != null)
                        getGoogleEventsListRefrence().getEvents(null, EVENTS_ERROR);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (getGoogleEventsListRefrence() != null)
                        getGoogleEventsListRefrence().getEvents(events, UPDATE_EVENTS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        // googleEventsList.showProgressBar(false);
        if (mLastError != null && getGoogleEventsListRefrence() != null) {
            getGoogleEventsListRefrence().setLoadingStatus(false);
            if (mLastError instanceof UserRecoverableAuthIOException) {
                getGoogleEventsListRefrence().onRecoverableAuthException((UserRecoverableAuthIOException) mLastError);
            } else {
                //  Log.d(TAG, "onCancelled: " + mLastError.getMessage());
            }
        }
    }

    private GoogleEventsList getGoogleEventsListRefrence() {
        GoogleEventsList googleEventsList = googleEventsListWeakReference.get();
        if (googleEventsList == null || googleEventsList.getBaseActivity() == null || googleEventsList.getBaseActivity().isFinishing()) {
            return null;
        }
        return googleEventsList;
    }
}
