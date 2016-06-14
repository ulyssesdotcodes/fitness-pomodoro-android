package com.ulyssesp.fitnesspomodoro.data.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Timer implements Parcelable {
    public abstract String name();
    public abstract Long duration();
    public abstract Boolean isBreak();

    public static Timer create(String name, Long duration, Boolean isBreak){
        return new AutoValue_Timer(name, duration, isBreak);
    }
}
