package com.ulyssesp.fitnesspomodoro.data.timer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NextTimerModel implements Parcelable {
    abstract Long currentTime();

    public static NextTimerModel create(Long currentTime) {
        return new AutoValue_NextTimerModel(currentTime);
    }

    static NextTimerModel fromParcel(Parcel p) {
        return AutoValue_NextTimerModel.CREATOR.createFromParcel(p);
    }
}
