package com.ulyssesp.fitnesspomodoro.data.exercise;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerChangedModel;
import com.ulyssesp.fitnesspomodoro.flrx.Action;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.flrx.Store;
import com.ulyssesp.fitnesspomodoro.utils.Optional;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Random;

import static com.ulyssesp.fitnesspomodoro.Constants.Actions.FETCH_EXERCISES;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.RECEIVE_EXERCISES;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.TIMER_CHANGED;

public class ExerciseStore extends Store<ExerciseStoreModel,Constants.Actions> {
    public ExerciseStore(Dispatcher<Constants.Actions> dispatcher) {
        super(dispatcher, EnumSet.of(
            FETCH_EXERCISES,
            RECEIVE_EXERCISES,
            TIMER_CHANGED
        ));
    }

    @Override
    protected ExerciseStoreModel initialState() {
        return ExerciseStoreModel.builder()
            .exercises(Collections.EMPTY_LIST)
            .currentExercise(Optional.absent())
            .build();
    }

    @Override
    public ExerciseStoreModel reducer(ExerciseStoreModel state, Action<Constants.Actions> action) {
        ExerciseStoreModel result = state;

        if(action.getType() == FETCH_EXERCISES) {
            postAction(RECEIVE_EXERCISES,
                ReceiveExercisesModel.create(Arrays.asList(ExerciseData.EXERCISES)));
        }
        else if (action.getType() == RECEIVE_EXERCISES) {
            ReceiveExercisesModel payload =
                ReceiveExercisesModel.fromParcel(action.getPayload().get());
            result = state.toBuilder().exercises(payload.exercises()).build();
        }
        else if (action.getType() == TIMER_CHANGED) {
            TimerChangedModel payload = TimerChangedModel.fromParcel(action.getPayload().get());
            if(payload.timer().isBreak()) {
                int random = new Random().nextInt(state.exercises().size());
                result = state.withExercise(state.exercises().get(random));
            }
            else {
                result = state.withoutExercise();
            }
        }

        return result;
    }
}
