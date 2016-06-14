package com.ulyssesp.fitnesspomodoro.dagger;

import com.ulyssesp.fitnesspomodoro.MainActivity;
import com.ulyssesp.fitnesspomodoro.ui.timer.TimerFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={TimerModule.class})
public interface AppComponent {
    void inject (MainActivity activity);
    void inject (TimerFragment fragment);
}
