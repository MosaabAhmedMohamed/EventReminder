package com.example.eventreminder.Activites;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.eventreminder.BaseViews.BaseActivity;
import com.example.eventreminder.R;
import com.example.eventreminder.Util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.eventreminder.Util.Constants.CALENDAR_SCOPE;
import static com.example.eventreminder.Util.Constants.RC_RECOVERABLE;

public class Home extends BaseActivity {
    private static final String TAG = "Home";

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.fragments)
    FrameLayout fragments;
    @BindView(R.id.out_from_google_btn)
    Button outFromGooglBtn;
    @BindView(R.id.out_from_facebook_btn)
    Button outFromFacebookBtn;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private GoogleSignInClient signInClient;
    private com.google.api.services.calendar.Calendar mService = null;
    private GoogleAccountCredential credential;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        GoogleSignInAccount acc = new Gson().fromJson(sharedPreferences.getString(Constants.GOOGLE_USER, " "), GoogleSignInAccount.class);
        if (acc != null && acc.getAccount() != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(CALENDAR_SCOPE))
                    .requestEmail()
                    .build();
            signInClient = GoogleSignIn.getClient(this, gso);

            credential = GoogleAccountCredential
                    .usingOAuth2(getApplicationContext(),
                            Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(acc.getAccount().name);
            getResultsFromApi(credential);
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    private void getResultsFromApi(GoogleAccountCredential credential) {


        new MakeRequestTask(credential).execute();
    }

    @OnClick({R.id.menu, R.id.out_from_google_btn, R.id.out_from_facebook_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.out_from_google_btn:
                signOutFromGoogle();
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.out_from_facebook_btn:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void signOutFromGoogle() {
        signInClient.signOut().addOnCompleteListener(task -> finish());
        editor.remove(Constants.GOOGLE_USER);
        editor.commit();
        startActivity(new Intent(this, Login.class));
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_RECOVERABLE) {
            if (resultCode == RESULT_OK) {
                buildSchedule();
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void buildSchedule() {
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acc != null) {
            getResultsFromApi(credential);
        } else {
            Toast.makeText(this, R.string.please_sign_in, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    public void onRecoverableAuthException(UserRecoverableAuthIOException recoverableException) {
        Log.w(TAG, "onRecoverableAuthException", recoverableException);
        startActivityForResult(recoverableException.getIntent(), RC_RECOVERABLE);
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private static final String TAG = "MakeRequestTask";
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Event Reminder")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
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
        }

        @Override
        protected void onPostExecute(List<String> output) {
            super.onPostExecute(output);
            if (output == null || output.size() == 0) {
                Log.d(TAG, "onPostExecute: " + "No results returned.");
            } else {
                Log.d(TAG, "onPostExecuteasfasgasgaghd.skngldnsdkgmsdg;gmlagmasg;lamg;lsamgs;lamsga;lsgm;: " + output);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mLastError != null) {
                if (mLastError instanceof UserRecoverableAuthIOException) {
                    onRecoverableAuthException((UserRecoverableAuthIOException) mLastError);
                } else {
                    Log.d(TAG, "onCancelled: " + mLastError.getMessage());
                    Log.d(TAG, "onCancelled: " + mLastError.getCause());
                }
            } else {
                Log.d(TAG, "onCancelled: " + "canceled");
            }
        }
    }

    private List<String> getDataFromApi() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
        }
        return eventStrings;
    }

}
