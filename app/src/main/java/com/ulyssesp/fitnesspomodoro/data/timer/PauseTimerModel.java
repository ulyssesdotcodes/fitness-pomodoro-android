package com.ulyssesp.fitnesspomodoro.data.timer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PauseTimerModel implements Parcelable {
    abstract Long currentTime();

    public static PauseTimerModel create(Long currentTime) {
        return new AutoValue_PauseTimerModel(currentTime);
    }

    static PauseTimerModel fromParcel(Parcel p) {
        return AutoValue_PauseTimerModel.CREATOR.createFromParcel(p);
    }
}
