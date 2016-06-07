package com.ulyssesp.fitnesspomodoro.timer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.ulyssesp.fitnesspomodoro.models.Timer;

import java.util.List;

@AutoValue
public abstract class ReceiveTimersModel implements Parcelable {
    public abstract List<Timer> timers();

    static ReceiveTimersModel create(List<Timer> timers) {
        return new AutoValue_ReceiveTimersModel(timers);
    }

    static ReceiveTimersModel fromParcel(Parcel p) {
        return AutoValue_ReceiveTimersModel.CREATOR.createFromParcel(p);
    }
}
