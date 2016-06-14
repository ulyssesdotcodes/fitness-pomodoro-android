package com.ulyssesp.fitnesspomodoro.dagger;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DispatcherModule {
    Dispatcher<Constants.Actions> mDispatcher;

    public DispatcherModule(Dispatcher<Constants.Actions> dispatcher) {
        mDispatcher = dispatcher;
    }

    @Provides
    @Singleton
    Dispatcher<Constants.Actions> provideDispatcher() {
        return mDispatcher;
    }
}
