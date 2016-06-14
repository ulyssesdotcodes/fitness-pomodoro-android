package com.ulyssesp.fitnesspomodoro.data.exercise;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.ulyssesp.fitnesspomodoro.utils.Optional;

import java.util.List;

@AutoValue
public abstract class ExerciseStoreModel implements Parcelable{
    public abstract List<Exercise> exercises();
    public abstract Optional<Exercise> currentExercise();

    public static Builder builder() {
        return new AutoValue_ExerciseStoreModel.Builder();
    }

    public Builder toBuilder(){
        return new AutoValue_ExerciseStoreModel.Builder(this);
    }

    public ExerciseStoreModel withExercise(Exercise exercise) {
        return this.toBuilder().currentExercise(Optional.of(exercise)).build();
    }

    public ExerciseStoreModel withoutExercise() {
        return this.toBuilder().currentExercise(Optional.absent()).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder exercises(List<Exercise> value);
        public abstract Builder currentExercise(Optional<Exercise> value);
        public abstract ExerciseStoreModel build();
    }

}
