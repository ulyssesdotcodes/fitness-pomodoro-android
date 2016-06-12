package com.ulyssesp.fitnesspomodoro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ulyssesp.fitnesspomodoro.flrx.Action;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.timer.NextTimerModel;
import com.ulyssesp.fitnesspomodoro.timer.TickTimerModel;
import com.ulyssesp.fitnesspomodoro.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.timer.TimerStoreModel;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {
    private Dispatcher<Constants.Action> mDispatcher;
    private TimerStore mStore;
    private ActiveTimerView mActiveTimerView;
    private TextView mInactiveTimerView;

    private int mCurrentTimerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDispatcher = new Dispatcher<>(EnumSet.allOf(Constants.Action.class));
        mStore = new TimerStore(mDispatcher);

        mInactiveTimerView = (TextView) findViewById(R.id.text_inactive_timer);
        mActiveTimerView = (ActiveTimerView) findViewById(R.id.timer_view);

        mStore.dataObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((TimerStoreModel timerStoreModel) -> {
                if(timerStoreModel.timers().size() - 1 < timerStoreModel.timerPosition()) {
                    showTimer(false);
                    return;
                }

                showTimer(true);

                mCurrentTimerPosition = timerStoreModel.timerPosition();

                long timerDuration = timerStoreModel.duration();

                boolean paused = timerStoreModel.paused();

                long sectionTimeCompleted =
                        timerStoreModel.currentTime() - timerStoreModel.startTime();
                long timeCompleted = sectionTimeCompleted + timerStoreModel.previouslyCompleted();
                long timeRemaining = timerDuration - timeCompleted;

                float percentDone = (float) timeCompleted / (float) timerDuration;

                ActiveTimerView.Model model =
                        ActiveTimerView.Model.create(timeRemaining, percentDone, paused);
                mActiveTimerView.update(model);
            });

        Observable.interval(16, TimeUnit.MILLISECONDS)
            .subscribe(__ ->
                mDispatcher.postAction(Action.create(Constants.Action.TICK_TIMER,
                    Single.fromCallable(() -> TickTimerModel.create(System.currentTimeMillis())))));

        assert findViewById(R.id.btn_change_timer) != null;
        findViewById(R.id.btn_change_timer)
            .setOnClickListener((e) ->
                mDispatcher.postAction(
                    Action.create(
                        Constants.Action.NEXT_TIMER,
                        Single.fromCallable(() ->
                            NextTimerModel.create(System.currentTimeMillis(), mCurrentTimerPosition))
                    )
                ));

        mDispatcher.postAction(Action.create(Constants.Action.FETCH_TIMERS));
    }

    private void showTimer(boolean show) {
        mInactiveTimerView.setVisibility(show ? View.GONE : View.VISIBLE);
        mActiveTimerView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
