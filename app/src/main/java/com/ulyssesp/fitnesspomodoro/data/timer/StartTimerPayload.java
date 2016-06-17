package com.ulyssesp.fitnesspomodoro.data.timer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class StartTimerPayload implements Parcelable{
    abstract Long time();

    public static StartTimerPayload create(Long currentTime) {
        return new AutoValue_StartTimerPayload(currentTime);
    }

    static StartTimerPayload fromParcel(Parcel p) {
        return AutoValue_StartTimerPayload.CREATOR.createFromParcel(p);
    }
}
