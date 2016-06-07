package com.ulyssesp.fitnesspomodoro.timer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.ulyssesp.fitnesspomodoro.models.Timer;

@AutoValue
public abstract class TimerChangedModel implements Parcelable {
    public abstract Timer timer();

    static TimerChangedModel create(Timer timer) {
        return new AutoValue_TimerChangedModel(timer);
    }

    public static TimerChangedModel fromParcel(Parcel p) {
        return AutoValue_TimerChangedModel.CREATOR.createFromParcel(p);
    }
}
