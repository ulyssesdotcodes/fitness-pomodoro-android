package com.ulyssesp.fitnesspomodoro.notifications;

import com.google.auto.value.AutoValue;

@AutoValue
abstract class NotificationModel {
    public abstract String title();
    public abstract String content();
    public abstract Long when();
    public abstract Boolean stopped();
    public abstract Boolean paused();

    static Builder builder() {
        return new AutoValue_NotificationModel.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        public abstract Builder title(String value);
        public abstract Builder content(String value);
        public abstract Builder when(Long value);
        public abstract Builder stopped(Boolean value);
        public abstract Builder paused(Boolean value);
        public abstract NotificationModel build();
    }
}

