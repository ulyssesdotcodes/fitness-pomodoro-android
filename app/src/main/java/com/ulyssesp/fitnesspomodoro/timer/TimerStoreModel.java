package com.ulyssesp.fitnesspomodoro.timer;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.ulyssesp.fitnesspomodoro.models.Timer;

import java.util.List;

@AutoValue
public abstract class TimerStoreModel implements Parcelable {
    public abstract List<Timer> timers();
    public abstract Boolean paused();
    public abstract Long currentTime();
    public abstract String name();
    public abstract Long duration();
    public abstract Long previouslyCompleted();
    public abstract Long startTime();
    public abstract Integer notifications();
    public abstract Integer timerPosition();

    static TimerStoreModel create(List<Timer> timers,
                                  Boolean paused,
                                  Long currentTime,
                                  String name,
                                  Long currentDuration,
                                  Long previouslyCompleted,
                                  Long startTime,
                                  Integer notifications,
                                  Integer timerPosition) {
        return new AutoValue_TimerStoreModel(timers, paused, currentTime, name, currentDuration,
                previouslyCompleted, startTime, notifications, timerPosition);
    }
}
