package com.ulyssesp.fitnesspomodoro.data.timer;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

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

    public static Builder builder() {
        return new AutoValue_TimerStoreModel.Builder();
    }

    public Builder toBuilder(){
        return new AutoValue_TimerStoreModel.Builder(this);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder timers(List<Timer> value);
        public abstract Builder paused(Boolean paused);
        public abstract Builder currentTime(Long currentTime);
        public abstract Builder name(String name);
        public abstract Builder duration(Long duration);
        public abstract Builder previouslyCompleted(Long previouslyCompleted);
        public abstract Builder startTime(Long startTime);
        public abstract Builder notifications(Integer notifications);
        public abstract Builder timerPosition(Integer timerPosition);
        public abstract TimerStoreModel build();
    }
}
