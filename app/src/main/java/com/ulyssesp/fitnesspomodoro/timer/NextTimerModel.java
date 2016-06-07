package com.ulyssesp.fitnesspomodoro.timer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NextTimerModel implements Parcelable {
    abstract Long currentTime();
    abstract Integer currentPosition();

    public static NextTimerModel create(Long currentTime, Integer currentPosition) {
        return new AutoValue_NextTimerModel(currentTime, currentPosition);
    }

    static NextTimerModel fromParcel(Parcel p) {
        return AutoValue_NextTimerModel.CREATOR.createFromParcel(p);
    }
}
