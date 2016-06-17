package com.ulyssesp.fitnesspomodoro.data.exercise;

import android.support.v4.util.Pair;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.data.timer.Timer;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerData;
import com.ulyssesp.fitnesspomodoro.flrx.Action;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.flrx.Store;
import com.ulyssesp.fitnesspomodoro.utils.Optional;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Random;

import rx.Observable;

import static com.ulyssesp.fitnesspomodoro.Constants.Actions.FETCH_EXERCISES;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.NEXT_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.RECEIVE_EXERCISES;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.STOP_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.TIMER_CHANGED;

public class ExerciseStore extends Store<ExerciseStoreModel,Constants.Actions> {
    public ExerciseStore(Dispatcher<Constants.Actions> dispatcher) {
        super(dispatcher, EnumSet.of(
            FETCH_EXERCISES,
            NEXT_TIMER,
            RECEIVE_EXERCISES,
            STOP_TIMER,
            TIMER_CHANGED
        ));
    }

    @Override
    protected ExerciseStoreModel initialState() {
        return ExerciseStoreModel.builder()
            .timers(Arrays.asList(TimerData.TIMERS))
            .currentTimer(0)
            .exercises(Arrays.asList(ExerciseData.EXERCISES))
            .currentExercise(Optional.absent())
            .build();
    }

    @Override
    public Pair<ExerciseStoreModel, Observable<Action<Constants.Actions>>> reducer(ExerciseStoreModel state, Action<Constants.Actions> action) {
        ExerciseStoreModel result = state;
        Observable<Action<Constants.Actions>> effects = Observable.empty();

        if (action.getType() == NEXT_TIMER) {
            result = state.toBuilder()
                .currentTimer((state.currentTimer() + 1) % state.timers().size())
                .build();

            Timer nextTimer = result.timers().get(result.currentTimer());

            if(nextTimer.isBreak()) {
                int random = new Random().nextInt(state.exercises().size());
                result = result.withExercise(state.exercises().get(random));
            }
            else {
                result = result.withoutExercise();
            }

        } else if (action.getType() == STOP_TIMER) {
            result = state.withoutExercise();
        }

        return Pair.create(result, effects);
    }
}
