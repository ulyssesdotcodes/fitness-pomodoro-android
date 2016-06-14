package com.ulyssesp.fitnesspomodoro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ulyssesp.fitnesspomodoro.data.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.notifications.TimerNotificationManager;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    @Inject
    TimerStore mTimerStore;

    @Inject
    Dispatcher<Constants.Actions> mDispatcher;

    TimerNotificationManager mTimerNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((FitnessPomodoroApplication) getApplication()).getAppComponent().inject(this);

        mTimerNotificationManager =
            new TimerNotificationManager(mTimerStore, mDispatcher, getApplicationContext());
    }
}
