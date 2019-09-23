package com.example.eventreminder.refactoring.ui.home.googleEvents.eventsList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eventreminder.refactoring.Async.AcceptGoogleEventsTask;
import com.example.eventreminder.refactoring.Async.DeleteGoogleEventTask;
import com.example.eventreminder.refactoring.Async.MakeGoogleEventsRequestTask;
import com.example.eventreminder.refactoring.data.models.EventDateTimeModel;
import com.example.eventreminder.refactoring.data.models.GoogleEventsAndForecastModel;
import com.example.eventreminder.refactoring.data.models.ListEntity;
import com.example.eventreminder.R;
import com.example.eventreminder.refactoring.data.models.WeatherResponse;
import com.example.eventreminder.refactoring.network.Resource;
import com.example.eventreminder.refactoring.ui.auth.AuthActivity;
import com.example.eventreminder.refactoring.ui.base.BaseFragment;
import com.example.eventreminder.refactoring.ui.base.ViewModelProviderFactory;
import com.example.eventreminder.refactoring.ui.home.HomeActivity;
import com.example.eventreminder.refactoring.util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.eventreminder.refactoring.util.Constants.RC_RECOVERABLE;

public class GoogleEventsList extends BaseFragment implements OnEventActionLIstner, SwipeRefreshLayout.OnRefreshListener, OnHandelOverlappingListner {
    private static final String TAG = "GoogleEventsList";

    @BindView(R.id.events_recycler)
    RecyclerView eventsRecycler;
    @BindView(R.id.list_view_status_tv)
    TextView listViewStatusTv;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;


    @Inject
    GoogleEventsListAdapter googleEventsListAdapter;
    @Inject
    ViewModelProviderFactory providerFactory;
    private GoogleListViewModel googleListViewModel;


    private GoogleSignInAccount acc;
    private AcceptGoogleEventsTask acceptGoogleEventsTask;
    private DeleteGoogleEventTask deleteGoogleEventTask;
    private MakeGoogleEventsRequestTask makeGoogleEventsRequestTask;
    private GoogleEventsAndForecastModel googleEventsAndForecastModel;
    private Calendar googleCalendar;

    private ScheduledExecutorService scheduledExecutorService;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    public static GoogleEventsList newInstance() {
        return new GoogleEventsList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) getBaseActivity()).setTitleTv("Google calendar events");
        ButterKnife.bind(this, view);
        initRefreshLayout();
        init();
    }

    private void init() {
        googleListViewModel = ViewModelProviders.of(this, providerFactory).get(GoogleListViewModel.class);
        eventsRecycler.setHasFixedSize(true);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        googleEventsListAdapter.setOnEventActionLIstner(this);
        googleEventsAndForecastModel = new GoogleEventsAndForecastModel();
        acc = GoogleSignIn.getLastSignedInAccount(getBaseActivity().getApplicationContext());
        googleEventsListAdapter.setLoggedInUserEmail(acc.getEmail());
        weatherObserver();
    }

    private void initRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.black,
                android.R.color.holo_red_light,
                android.R.color.darker_gray);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void weatherObserver() {
        googleListViewModel.observWeather().removeObservers(getViewLifecycleOwner());
        googleListViewModel.observWeather().observe(getViewLifecycleOwner(), new Observer<Resource<WeatherResponse>>() {
            @Override
            public void onChanged(Resource<WeatherResponse> weatherResponseResource) {
                if (weatherResponseResource != null) {
                    switch (weatherResponseResource.status) {
                        case LOADING: {
                            //  Log.d(TAG, "onChanged:loading ");
                            setLoadingStatus(true);
                            break;
                        }
                        case SUCCESS: {
                            // Log.d(TAG, "onChanged: get posts" + weatherResponseResource.data.getList().toString());
                            setLoadingStatus(false);
                            applyKeyValuePairForDateAndModel(weatherResponseResource.data.getList());
                            getUserData(acc);
                            // adapter.setPosts(listResource.data);
                            break;
                        }
                        case ERROR: {
                            setLoadingStatus(false);
                            //  Log.d(TAG, "onChanged:  error" + weatherResponseResource.message);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void applyKeyValuePairForDateAndModel(List<ListEntity> weatherList) {
        HashMap<Integer, ListEntity> forecastModelsMap = new HashMap<>();
        for (int i = 0; i < weatherList.size(); i++) {
            forecastModelsMap.put(weatherList.get(i).getDt(), weatherList.get(i));
        }
        googleEventsAndForecastModel.setForecastModels(forecastModelsMap);
    }

    private void getUserData(GoogleSignInAccount acc) {
        if (acc != null && acc.getAccount() != null) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(),
                    Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(acc.getAccount().name);
            credential.setSelectedAccount(acc.getAccount());
            initCalendarAndGetEventsFromApi(credential);
        } else if (getActivity() != null) {
            startActivity(new Intent(getActivity(), AuthActivity.class));
            getActivity().finish();
        }
    }

    private void initCalendarAndGetEventsFromApi(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        googleCalendar = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("Event Reminder").build();
        reInitGoogleEventsTask();
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
        swipeRefreshLayout.setRefreshing(false);
        if (events != null) {
            for (Event event : events) {
                if (event.getStart().getDateTime() != null &&
                        event.getStart().getDateTime().getValue() != 0 &&
                        event.getEnd().getDateTime().getValue() != 0) {

                    String date = Constants.getInstance().getFormattedDate(event.getStart().getDateTime().getValue());
                    int eventStart = Constants.getInstance().convertUnixToSeconds(event.getStart().getDateTime().getValue());
                    int eventEnd = Constants.getInstance().convertUnixToSeconds(event.getEnd().getDateTime().getValue());

                    googleEventsAndForecastModel.getEventDateTimeModels().add(new EventDateTimeModel(date, eventStart, eventEnd, event));
                }
                //  Log.d(TAG, "getDataFromApi: " + String.format("%s (%s)", event.getSummary(), start));
            }
        }
        callEventsListAdapter(events);
    }

    private void callEventsListAdapter(List<Event> events) {
        //  if (googleEventsListAdapter == null) {
        if (events == null || events.size() == 0) {
            listViewStatusTv.setVisibility(View.VISIBLE);
            listViewStatusTv.setText("You don't have any events yet");
        } else {
            listViewStatusTv.setVisibility(View.GONE);
            eventsRecycler.setVisibility(View.VISIBLE);
            googleEventsAndForecastModel.setEventsModels(events);
            googleEventsListAdapter.setGoogleEventsAndForecastModel(googleEventsAndForecastModel);
            eventsRecycler.setAdapter(googleEventsListAdapter);
        }
        runEvery30Second();
        /*} else {
            googleEventsListAdapter.setUpdatedEvents(events);
        }*/
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
            Toast.makeText(getActivity(), "You can't accept event that you already created", Toast.LENGTH_SHORT).show();
        } else if (googleEventsAndForecastModel.getEventsModels().get(position).getCreator() == null)
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
        else {
            List<EventAttendee> attendees = googleEventsAndForecastModel.getEventsModels().get(position).getAttendees();
            for (int i = 0; i < attendees.size(); i++) {

                if (attendees.get(i).getEmail().equals(acc.getEmail())) {
                    if (attendees.get(i).getResponseStatus().equals("accepted")) {
                        Toast.makeText(getActivity(), "You already accepted the invention", Toast.LENGTH_SHORT).show();
                    } else {
                        //showProgressBar(true);
                        attendees.get(i).setResponseStatus("accepted");
                        googleEventsAndForecastModel.getEventsModels().get(position).setAttendees(attendees);
                        if (acceptGoogleEventsTask != null)
                            acceptGoogleEventsTask.cancel(true);

                        acceptGoogleEventsTask = new AcceptGoogleEventsTask(this, googleCalendar, googleEventsAndForecastModel.getEventsModels().get(position));
                        acceptGoogleEventsTask.execute();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onEventOverlapped(EventDateTimeModel firstEvent, EventDateTimeModel secondEvent, int position) {
        ArrayList<EventDateTimeModel> eventDateTimeModels = new ArrayList<>();
        eventDateTimeModels.add(firstEvent);
        eventDateTimeModels.add(secondEvent);
        OverlappingDailog overlappingDailog = OverlappingDailog.newInstance(eventDateTimeModels, position, acc.getEmail());
        overlappingDailog.setTargetFragment(GoogleEventsList.this, 1);
        if (getFragmentManager() != null)
            overlappingDailog.show(getFragmentManager(), "selectDialog");
    }


    @Override
    public void onRefresh() {
        if (googleEventsAndForecastModel.getEventsModels() != null)
            googleEventsAndForecastModel.getEventsModels().clear();
        swipeRefreshLayout.setRefreshing(true);
        reInitGoogleEventsTask();
    }

    private void reInitGoogleEventsTask() {
        if (makeGoogleEventsRequestTask != null)
            makeGoogleEventsRequestTask.cancel(true);

        makeGoogleEventsRequestTask = new MakeGoogleEventsRequestTask(this, googleCalendar);
        makeGoogleEventsRequestTask.execute();
    }

    private void runEvery30Second() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  if (Constants.getInstance().isDeviceOnline(getActivity()))
                            reInitGoogleEventsTask();
                            //Log.d(TAG, "run: ");
                        }
                    });
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        acceptGoogleEventsTask = null;
        deleteGoogleEventTask = null;
        makeGoogleEventsRequestTask = null;
        scheduledExecutorService = null;
    }

    @Override
    public void onHandel(boolean status, EventDateTimeModel selectedEventToReschedule, int positionOfEventInList, int eventSelectedFromDialog) {
        if (status && eventSelectedFromDialog == Constants.SELECTED_EVENT_TO_RESCHDULE) {
            onDeleteEvent(selectedEventToReschedule.getEvent().getId());
            googleEventsListAdapter.notifyEventDealingWithOvenLapping();
            // if (getActivity() != null && isAdded())
            //  ((HomeActivity) getActivity()).PushFragment(RescheduleOverlappedEvent.
            //  newInstance(googleEventsAndForecastModel.getEventDateTimeModels(), selectedEventToReschedule));
        } else if (!status && eventSelectedFromDialog == 0) {
            googleEventsListAdapter.notifyEventDealingWithOvenLapping();
        } else if (!status) {
            onDeleteEvent(selectedEventToReschedule.getEvent().getId());
            googleEventsListAdapter.notifyEventDealingWithOvenLapping();
        }

    }

    public void onErrorResponse(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
