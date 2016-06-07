package com.ulyssesp.fitnesspomodoro.timer;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.ulyssesp.fitnesspomodoro.models.Timer;

import java.util.List;

@AutoValue
public abstract class TimerStoreModel implements Parcelable {
    public abstract List<Timer> timers();
    public abstract Long startTime();
    public abstract Boolean paused();
    public abstract Long currentDuration();
    public abstract Long currentTime();
    public abstract Integer timerPosition();

    static TimerStoreModel create(List<Timer> timers, Long startTime, Boolean paused,
                                  Long currentDuration, Long currentTime, Integer timerPosition) {
        return new AutoValue_TimerStoreModel(timers, startTime, paused, currentDuration,
            currentTime, timerPosition);
    }
}
