package com.ulyssesp.fitnesspomodoro.data.exercise;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Exercise implements Parcelable{
    public abstract String name();
    public abstract Integer reps();
    public abstract String units();

    public static Exercise create(String name, Integer reps, String units){
        return new AutoValue_Exercise(name, reps, units);
    }

    @Override
    public String toString() {
        return name() + " " + reps() + " " + units();
    }
}
