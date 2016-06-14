package com.ulyssesp.fitnesspomodoro.ui.timer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.FitnessPomodoroApplication;
import com.ulyssesp.fitnesspomodoro.R;
import com.ulyssesp.fitnesspomodoro.data.timer.NextTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.PauseTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.TickTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStoreModel;
import com.ulyssesp.fitnesspomodoro.flrx.Action;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class TimerFragment extends Fragment {
    @Inject
    Dispatcher<Constants.TimerActions> mDispatcher;

    @Inject
    TimerStore mTimerStore;

    private ActiveTimerView mActiveTimerView;
    private TextView mTimeRemaining;
    private FloatingActionButton mPauseFab;
    private FloatingActionButton mUnpauseFab;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((FitnessPomodoroApplication) getActivity().getApplication()).getAppComponent().inject(this);

        mTimerStore.dataObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((TimerStoreModel timerStoreModel) -> {
                if (timerStoreModel.timers().size() - 1 < timerStoreModel.timerPosition()) {
                    showTimer(false);
                    return;
                }

                showTimer(true);

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

                togglePause(timerStoreModel.paused());
            });

        Observable.interval(16, TimeUnit.MILLISECONDS)
            .subscribe(__ ->
                mDispatcher.postAction(Action.create(Constants.TimerActions.TICK_TIMER,
                    TickTimerModel.create(System.currentTimeMillis()))));
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

        view.findViewById(R.id.btn_change_timer)
            .setOnClickListener((e) ->
                mDispatcher.postAction(
                    Action.create(
                        Constants.TimerActions.NEXT_TIMER,
                        NextTimerModel.create(System.currentTimeMillis())
                    )
                ));

        View.OnClickListener togglePause = (v) ->
            mDispatcher.postAction(
                Action.create(
                    Constants.TimerActions.PAUSE_TIMER,
                    PauseTimerModel.create(System.currentTimeMillis())
                )
            );

        mPauseFab.setOnClickListener(togglePause);
        mUnpauseFab.setOnClickListener(togglePause);

        mDispatcher.postAction(Action.create(Constants.TimerActions.FETCH_TIMERS));
        return view;
    }

    private void showTimer(boolean show) {
        mActiveTimerView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void togglePause(boolean paused) {
        mPauseFab.setVisibility(paused ? View.GONE : View.VISIBLE);
        mUnpauseFab.setVisibility(!paused ? View.GONE : View.VISIBLE);
    }
}
