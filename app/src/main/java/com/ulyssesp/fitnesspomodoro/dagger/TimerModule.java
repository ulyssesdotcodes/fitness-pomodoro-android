package com.ulyssesp.fitnesspomodoro.dagger;

import android.content.Context;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TimerModule {
    @Inject Context mAppContext;

    public TimerModule(Context appContext) {
        mAppContext = appContext;
    }

    @Provides
    @Singleton
    TimerStore provideTimerStore(final Dispatcher<Constants.Actions> dispatcher) {
        return new TimerStore(mAppContext, dispatcher);
    }
}
