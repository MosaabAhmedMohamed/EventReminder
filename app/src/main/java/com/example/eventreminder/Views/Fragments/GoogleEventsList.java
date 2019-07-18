package com.example.eventreminder.Views.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventreminder.Async.DeleteGoogleEventTask;
import com.example.eventreminder.Async.MakeGoogleEventsRequestTask;
import com.example.eventreminder.BaseViews.BaseFragment;
import com.example.eventreminder.Models.GoogleEventsAndForecastModel;
import com.example.eventreminder.Models.ListEntity;
import com.example.eventreminder.R;
import com.example.eventreminder.Requests.Responses.WeatherResponse;
import com.example.eventreminder.Util.Constants;
import com.example.eventreminder.Util.OnEventActionLIstner;
import com.example.eventreminder.ViewModels.GoogleListViewModel;
import com.example.eventreminder.Views.Activites.Home;
import com.example.eventreminder.Views.GoogleEventsListAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.eventreminder.Util.Constants.RC_RECOVERABLE;

public class GoogleEventsList extends BaseFragment implements OnEventActionLIstner {
    private static final String TAG = "GoogleEventsList";
    @BindView(R.id.events_recycler)
    RecyclerView eventsRecycler;
    private View view;


    private GoogleSignInAccount acc;
    private DeleteGoogleEventTask deleteGoogleEventTask;
    private MakeGoogleEventsRequestTask makeGoogleEventsRequestTask;
    private GoogleEventsAndForecastModel googleEventsAndForecastModel;
    private GoogleListViewModel googleListViewModel;
    private GoogleEventsListAdapter googleEventsListAdapter;
    private Calendar googleCalendar;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    public static GoogleEventsList newInstance() {
        return new GoogleEventsList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.events_list, container, false);
            ButterKnife.bind(this, view);
            init();

        }
        return view;
    }

    private void init() {
        googleListViewModel = ViewModelProviders.of(this).get(GoogleListViewModel.class);
        eventsRecycler.setHasFixedSize(true);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        googleEventsAndForecastModel = new GoogleEventsAndForecastModel();

        acc = new Gson().fromJson(sharedPreferences.getString(Constants.GOOGLE_USER, " "), GoogleSignInAccount.class);
        getUserData(acc);
        subScribeToObserver();
    }

    private void subScribeToObserver() {
        showProgressBar(true);
        if (googleListViewModel.getWeatherResponseMutableLiveData() == null) {
            googleListViewModel.getForCastData("cairo", Constants.getInstance().openWeatherMapAPIKey, "40").observe(this, new Observer<WeatherResponse>() {
                @Override
                public void onChanged(WeatherResponse weatherResponse) {
                    if (weatherResponse != null && weatherResponse.getCod().equals("200")) {
                        Log.d(TAG, "onChanged: 1" + weatherResponse.getList().size());
                        applayKeyValuePairForDateAndModel(weatherResponse.getList());
                    } else {
                        Toast.makeText(getActivity(), "errorr", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            googleListViewModel.getWeatherResponseMutableLiveData().observe(this, new Observer<WeatherResponse>() {
                @Override
                public void onChanged(WeatherResponse weatherResponse) {
                    Log.d(TAG, "onChanged: 2" + weatherResponse.getList().size());

                }
            });
        }
    }

    private void applayKeyValuePairForDateAndModel(List<ListEntity> weatherList) {
        HashMap<String, ListEntity> forecastModelsMap = new HashMap<>();
        for (int i = 0; i < weatherList.size(); i++) {
            forecastModelsMap.put(Constants.getInstance().convertUnixToDate(weatherList.get(i).getDt()), weatherList.get(i));
        }
        googleEventsAndForecastModel.setForecastModels(forecastModelsMap);
    }

    private void getUserData(GoogleSignInAccount acc) {
        if (acc != null && acc.getAccount() != null) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(),
                    Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(acc.getAccount().name);
            initCalendarAndGetEventsFromApi(credential);
        } else {
          /*  startActivity(new Intent(this, Login.class));
            finish();*/
        }
    }

    private void initCalendarAndGetEventsFromApi(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        googleCalendar = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("Event Reminder").build();
        makeGoogleEventsRequestTask = new MakeGoogleEventsRequestTask(this, googleCalendar);
        makeGoogleEventsRequestTask.execute();
    }

    public void onRecoverableAuthException(UserRecoverableAuthIOException recoverableException) {
        // Log.w(TAG, "onRecoverableAuthException", recoverableException);
        startActivityForResult(recoverableException.getIntent(), RC_RECOVERABLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_RECOVERABLE) {
            if (resultCode == RESULT_OK) {
                if (getActivity() != null && isAdded()) {
                    acc = GoogleSignIn.getLastSignedInAccount(getActivity());
                    getUserData(acc);
                }

            } else {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getEvents(List<Event> events) throws IOException {
        if (events != null) {
            for (Event event : events) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                //  Log.d(TAG, "getDataFromApi:   2 " + event.toPrettyString());
                //  Log.d(TAG, "getDataFromApi: " + String.format("%s (%s)", event.getSummary(), start));
            }
            callEventsListAdapter(events);

        }
    }

    private void callEventsListAdapter(List<Event> events) {
        if (googleEventsListAdapter == null)
        {
            googleEventsAndForecastModel.setEventsModels(events);
            googleEventsListAdapter = new GoogleEventsListAdapter(this, googleEventsAndForecastModel);
            eventsRecycler.setAdapter(googleEventsListAdapter);
        }
        else
            googleEventsListAdapter.setUpdatedEvents(events);
    }

    public void showProgressBar(boolean visibility) {
        if (getActivity() != null && isAdded())
            ((Home) getActivity()).showProgressBar(visibility);
    }

    @Override
    public void onDeleteEvent(String id) {
        if (deleteGoogleEventTask != null)
            deleteGoogleEventTask.cancel(true);

        deleteGoogleEventTask = new DeleteGoogleEventTask(this, googleCalendar, id);
        deleteGoogleEventTask.execute();

    }

    @Override
    public void onAcceptEvent(int position) {
        if (googleEventsAndForecastModel.getEventsModels().get(position).getCreator().getEmail().equals(acc.getEmail())) {
            Toast.makeText(getActivity(), "you can't accept event that you already created", Toast.LENGTH_SHORT).show();
        } else if (googleEventsAndForecastModel.getEventsModels().get(position).getCreator() == null)
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
        else

            Log.d(TAG, "onAcceptEvent: " + position);
    }
}
