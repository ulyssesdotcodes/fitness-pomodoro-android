package com.ulyssesp.fitnesspomodoro.data.timer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TickTimerModel implements Parcelable {
    abstract Long currentTime();

    public static TickTimerModel create(long time) {
        return new AutoValue_TickTimerModel(time);
    }

    public static TickTimerModel fromParcel(Parcel parcel) {
        return AutoValue_TickTimerModel.CREATOR.createFromParcel(parcel);
    }
}

