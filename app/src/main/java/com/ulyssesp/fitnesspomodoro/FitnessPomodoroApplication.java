package com.ulyssesp.fitnesspomodoro;

import android.app.Application;

import com.ulyssesp.fitnesspomodoro.dagger.AppComponent;
import com.ulyssesp.fitnesspomodoro.dagger.DaggerAppComponent;
import com.ulyssesp.fitnesspomodoro.dagger.TimerModule;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

import java.util.EnumSet;

public class FitnessPomodoroApplication extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .timerModule(new TimerModule(new Dispatcher<>(EnumSet.allOf(Constants.TimerActions.class))))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
