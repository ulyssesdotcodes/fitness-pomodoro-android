package com.ulyssesp.fitnesspomodoro.dagger;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TimerModule {
    Dispatcher<Constants.TimerActions> mDispatcher;

    public TimerModule(Dispatcher<Constants.TimerActions> dispatcher) {
        mDispatcher = dispatcher;
    }

    @Provides
    @Singleton
    Dispatcher<Constants.TimerActions> provideDispatcher() {
        return mDispatcher;
    }

    @Provides
    @Singleton
    TimerStore provideTimerStore() {
        return new TimerStore(mDispatcher);
    }
}
