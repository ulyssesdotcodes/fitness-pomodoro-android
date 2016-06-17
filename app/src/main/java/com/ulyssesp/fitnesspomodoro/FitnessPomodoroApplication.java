package com.ulyssesp.fitnesspomodoro;

import android.app.Application;
import android.content.Intent;

import com.ulyssesp.fitnesspomodoro.dagger.AppComponent;
import com.ulyssesp.fitnesspomodoro.dagger.DaggerAppComponent;
import com.ulyssesp.fitnesspomodoro.dagger.DispatcherModule;
import com.ulyssesp.fitnesspomodoro.dagger.ExerciseModule;
import com.ulyssesp.fitnesspomodoro.dagger.TimerModule;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.notifications.NotificationService;

import java.util.EnumSet;

public class FitnessPomodoroApplication extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Dispatcher<Constants.Actions> dispatcher =
            new Dispatcher<>(EnumSet.allOf(Constants.Actions.class));

        mAppComponent =
            DaggerAppComponent.builder()
                .dispatcherModule(new DispatcherModule(dispatcher))
                .timerModule(new TimerModule(getApplicationContext()))
                .exerciseModule(new ExerciseModule())
                .build();

        Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
        serviceIntent.setAction(NotificationService.START);
        startService(serviceIntent);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
