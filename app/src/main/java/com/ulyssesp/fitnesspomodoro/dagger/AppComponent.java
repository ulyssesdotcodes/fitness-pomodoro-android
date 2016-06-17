package com.ulyssesp.fitnesspomodoro.dagger;

import com.ulyssesp.fitnesspomodoro.notifications.NotificationService;
import com.ulyssesp.fitnesspomodoro.ui.timer.TimerFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={DispatcherModule.class, ExerciseModule.class, TimerModule.class})
public interface AppComponent {
    void inject (TimerFragment fragment);
    void inject (NotificationService service);
}
