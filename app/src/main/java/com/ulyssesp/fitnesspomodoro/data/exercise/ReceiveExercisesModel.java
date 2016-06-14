package com.ulyssesp.fitnesspomodoro.data.exercise;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class ReceiveExercisesModel implements Parcelable {
    public abstract List<Exercise> exercises();

    static ReceiveExercisesModel create(List<Exercise> exercises) {
        return new AutoValue_ReceiveExercisesModel(exercises);
    }

    static ReceiveExercisesModel fromParcel(Parcel p) {
        return AutoValue_ReceiveExercisesModel.CREATOR.createFromParcel(p);
    }
}
