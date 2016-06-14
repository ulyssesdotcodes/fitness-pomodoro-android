package com.ulyssesp.fitnesspomodoro.dagger;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.data.exercise.ExerciseStore;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ExerciseModule {
    @Provides
    @Singleton
    ExerciseStore provideExerciseStore(Dispatcher<Constants.Actions> dispatcher) {
        return new ExerciseStore(dispatcher);
    }
}
