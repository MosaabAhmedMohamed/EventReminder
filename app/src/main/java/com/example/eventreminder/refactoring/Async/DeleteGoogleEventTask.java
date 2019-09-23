package com.example.eventreminder.refactoring.Async;

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

            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // googleEventsList.showProgressBar(true);
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        super.onPostExecute(events);
        // googleEventsList.showProgressBar(false);
        if (events == null || events.size() == 0) {
            try {
                if (getGoogleEventsListRefrence() != null)
                    getGoogleEventsListRefrence().getEvents(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (getGoogleEventsListRefrence() != null)
                    getGoogleEventsListRefrence().getEvents(events);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        // googleEventsList.showProgressBar(false);
        if (mLastError != null && getGoogleEventsListRefrence() != null) {
            if (mLastError instanceof UserRecoverableAuthIOException) {
                getGoogleEventsListRefrence().onRecoverableAuthException((UserRecoverableAuthIOException) mLastError);
            } else {
                //  Log.d(TAG, "onCancelled: " + mLastError.getMessage());
            }
        } else {
            //Log.d(TAG, "onCancelled: " + "canceled");
        }
    }

    private List<Event> getDataFromApi() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = googleCalendar.events()
                .list("primary")
                .setMaxResults(30)
                .setTimeMin(now)
                .setOrderBy("startTime").setSingleEvents(true).execute();
        //  Log.d(TAG, "getDataFromApi: " + events.getItems().toString());
        return events.getItems();
    }

    private GoogleEventsList getGoogleEventsListRefrence() {
        GoogleEventsList googleEventsList = googleEventsListWeakReference.get();
        if (googleEventsList == null || googleEventsList.getBaseActivity().isFinishing()) {
            return null;
        }
        return googleEventsList;
    }
}
