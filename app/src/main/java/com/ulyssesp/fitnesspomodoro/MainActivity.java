package com.ulyssesp.fitnesspomodoro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {
    private Dispatcher<Constants.Action> mDispatcher;
    private TimerStore mStore;

    private TextView mTextView;

    private int mCurrentTimerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDispatcher = new Dispatcher<>(EnumSet.allOf(Constants.Action.class));
        mStore = new TimerStore(mDispatcher);

        mTextView = (TextView) findViewById(R.id.main_text);

        mStore.dataObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((TimerStoreModel timerStoreModel) -> {
                if(timerStoreModel.timers().size() - 1 < timerStoreModel.timerPosition()) {
                    mTextView.setText("Inactive");
                    return;
                }

                mCurrentTimerPosition = timerStoreModel.timerPosition();

                long timerDuration =
                    timerStoreModel.timers().get(timerStoreModel.timerPosition()).duration();
                long currentTime = timerStoreModel.currentTime();
                long currentDuration = timerStoreModel.currentDuration();
                boolean paused = timerStoreModel.paused();

                long prevElapsedMillis = timerDuration - currentDuration;
                long elapsedMillis =
                    (currentTime - timerStoreModel.startTime()) + prevElapsedMillis;
                long timeRemaining = timerDuration - elapsedMillis;

                float percentDone = (float) elapsedMillis / (float) timerDuration;

                mTextView.setText(String.valueOf(percentDone));
            });

        Observable.interval(16, TimeUnit.MILLISECONDS)
            .subscribe(__ ->
                mDispatcher.postAction(Action.create(Constants.Action.TICK_TIMER,
                    TickTimerModel.create(System.currentTimeMillis()))));

        assert findViewById(R.id.btn_change_timer) != null;
        findViewById(R.id.btn_change_timer)
            .setOnClickListener((e) ->
                mDispatcher.postAction(
                    Action.create(
                        Constants.Action.NEXT_TIMER,
                        NextTimerModel.create(System.currentTimeMillis(), mCurrentTimerPosition)
                    )
                ));

        mDispatcher.postAction(Action.create(Constants.Action.FETCH_TIMERS));
    }
}
