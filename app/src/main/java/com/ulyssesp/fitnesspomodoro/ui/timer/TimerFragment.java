package com.ulyssesp.fitnesspomodoro.ui.timer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxFragment;
import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.FitnessPomodoroApplication;
import com.ulyssesp.fitnesspomodoro.R;
import com.ulyssesp.fitnesspomodoro.data.exercise.Exercise;
import com.ulyssesp.fitnesspomodoro.data.exercise.ExerciseStore;
import com.ulyssesp.fitnesspomodoro.data.timer.NextTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.PauseTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.StartTimerPayload;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStoreModel;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.utils.Optional;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class TimerFragment extends RxFragment {
    @Inject Dispatcher<Constants.Actions> mDispatcher;

    @Inject TimerStore mTimerStore;

    @Inject ExerciseStore mExerciseStore;

    private ActiveTimerView mActiveTimerView;
    private TextView mTimeRemaining;
    private FloatingActionButton mPauseFab;
    private FloatingActionButton mUnpauseFab;
    private FloatingActionButton mStartFab;
    private FloatingActionButton mStopFab;
    private FloatingActionButton mNextTimer;

    private ViewGroup mExercise;
    private TextView mExerciseTextName;
    private TextView mExerciseTextReps;
    private TextView mExerciseTextUnits;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((FitnessPomodoroApplication) getActivity().getApplication()).getAppComponent().inject(this);

        mTimerStore.dataObservable()
            .compose(bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((TimerStoreModel timerStoreModel) -> {
                toggleStopped(timerStoreModel.stopped());

                if(!timerStoreModel.stopped()) {
                    togglePause(timerStoreModel.paused());
                }

                long timerDuration = timerStoreModel.duration();

                long sectionTimeCompleted =
                    timerStoreModel.currentTime() - timerStoreModel.startTime();
                long timeCompleted = sectionTimeCompleted + timerStoreModel.previouslyCompleted();

                float percentDone = (float) timeCompleted / (float) timerDuration;

                ActiveTimerView.Model model =
                    ActiveTimerView.Model.create(percentDone);
                mActiveTimerView.update(model);

                long timeRemaining = timerDuration - timeCompleted;
                float secondsRemaining = Math.abs(timeRemaining / 1000f);
                String time =
                    (timeRemaining > 0 ? "" : '-') +
                        String.format("%02d:%02d",
                            (int) secondsRemaining / 60,
                            (int) secondsRemaining % 60
                        );

                mTimeRemaining.setText(time);
            });

        mExerciseStore.dataObservable()
            .compose(bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(exerciseStore -> {
                Optional<Exercise> exercise = exerciseStore.currentExercise();
                if (exercise.isPresent()) {
                    mExercise.setVisibility(View.VISIBLE);
                    mExerciseTextName.setText(exercise.get().name());
                    mExerciseTextReps.setText(exercise.get().reps().toString());
                    mExerciseTextUnits.setText(exercise.get().units());
                }
                else {
                    mExercise.setVisibility(View.GONE);
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        mTimeRemaining = (TextView) view.findViewById(R.id.text_time_remaining);
        mActiveTimerView = (ActiveTimerView) view.findViewById(R.id.timer_view);
        mPauseFab = (FloatingActionButton) view.findViewById(R.id.btn_pause_timer);
        mUnpauseFab = (FloatingActionButton) view.findViewById(R.id.btn_unpause_timer);
        mStartFab = (FloatingActionButton) view.findViewById(R.id.btn_start_timer);
        mStopFab = (FloatingActionButton) view.findViewById(R.id.btn_stop_timer);
        mNextTimer = (FloatingActionButton) view.findViewById(R.id.btn_next_timer);

        mExercise = (ViewGroup) view.findViewById(R.id.group_exercise);
        mExerciseTextName = (TextView) view.findViewById(R.id.text_exercise_name);
        mExerciseTextReps = (TextView) view.findViewById(R.id.text_exercise_reps);
        mExerciseTextUnits = (TextView) view.findViewById(R.id.text_exercise_units);

        mNextTimer
            .setOnClickListener((v) -> mDispatcher.postAction(
                Constants.Actions.NEXT_TIMER,
                NextTimerModel.create(System.currentTimeMillis())
            ));

        View.OnClickListener togglePause = (v) ->
            mDispatcher.postAction(
                    Constants.Actions.PAUSE_TIMER,
                    PauseTimerModel.create(System.currentTimeMillis())
            );

        mPauseFab.setOnClickListener(togglePause);
        mUnpauseFab.setOnClickListener(togglePause);

        mStartFab.setOnClickListener(v -> mDispatcher.postAction(
            Constants.Actions.START_TIMER,
            StartTimerPayload.create(System.currentTimeMillis())
        ));

        mStopFab.setOnClickListener(v -> mDispatcher.postAction(
            Constants.Actions.STOP_TIMER
        ));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void togglePause(boolean paused) {
        mPauseFab.setVisibility(paused ? View.GONE : View.VISIBLE);
        mUnpauseFab.setVisibility(!paused ? View.GONE : View.VISIBLE);
    }

    private void toggleStopped(boolean stopped) {
        mStartFab.setVisibility(stopped ? View.VISIBLE : View.GONE);
        mStopFab.setVisibility(stopped ? View.GONE : View.VISIBLE);
        mPauseFab.setVisibility(stopped ? View.GONE : View.VISIBLE);
        mNextTimer.setVisibility(stopped ? View.GONE : View.VISIBLE);
    }
}
