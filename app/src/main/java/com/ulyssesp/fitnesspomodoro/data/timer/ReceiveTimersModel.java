package com.ulyssesp.fitnesspomodoro.data.timer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.ulyssesp.fitnesspomodoro.data.models.Timer;

import java.util.List;

@AutoValue
public abstract class ReceiveTimersModel implements Parcelable {
    public abstract List<Timer> timers();
    public abstract Long time();

    static ReceiveTimersModel create(List<Timer> timers, Long time) {
        return new AutoValue_ReceiveTimersModel(timers, time);
    }

    static ReceiveTimersModel fromParcel(Parcel p) {
        return AutoValue_ReceiveTimersModel.CREATOR.createFromParcel(p);
    }
}
