package com.ulyssesp.fitnesspomodoro.data.timer;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v4.util.Pair;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.flrx.Action;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.flrx.Store;
import com.ulyssesp.fitnesspomodoro.notifications.NotificationService;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import rx.Observable;
import rx.Single;

import static com.ulyssesp.fitnesspomodoro.Constants.Actions.NEXT_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.NO_OP;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.PAUSE_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.START_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.STOP_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.Actions.TICK_TIMER;

public class TimerStore extends Store<TimerStoreModel, Constants.Actions> {
    Context mContext;

    public TimerStore(Context context, Dispatcher<Constants.Actions> dispatcher) {
        super(dispatcher, EnumSet.of(
            NEXT_TIMER,
            PAUSE_TIMER,
            TICK_TIMER,
            START_TIMER,
            STOP_TIMER
        ));

        mContext = context;
    }

    @Override
    protected TimerStoreModel initialState() {
        List<Timer> timers = Arrays.asList(TimerData.TIMERS);
        Timer initialTimer = timers.get(0);
        return TimerStoreModel.builder()
            .timers(timers)
            .paused(Boolean.TRUE)
            .currentTime(System.currentTimeMillis())
            .name(initialTimer.name())
            .duration(initialTimer.duration())
            .previouslyCompleted(0L)
            .startTime(System.currentTimeMillis())
            .notifications(0)
            .timerPosition(0)
            .stopped(Boolean.TRUE)
            .build();
    }

    @Override
    public Pair<TimerStoreModel, Observable<Action<Constants.Actions>>>
        reducer(TimerStoreModel state, Action action) {
        TimerStoreModel result = state;
        Observable<Action<Constants.Actions>>  effects = Observable.empty();

        if (action.getType()== TICK_TIMER && action.getPayload().isPresent()) {
            TickTimerModel payload =
                TickTimerModel.fromParcel((Parcel) action.getPayload().get());

            if(!(state.paused() || state.stopped())) {
                result = state.toBuilder()
                    .currentTime(payload.currentTime())
                    .build();

                long timeCompleted = result.currentTime() - result.startTime() +
                    result.previouslyCompleted();

                if(timeCompleted - result.duration() >
                    result.notifications() * Constants.NOTIFICATION_TIME) {

                    result = result.toBuilder()
                        .notifications(result.notifications() + 1)
                        .build();

                    effects = Single.defer(() -> {
                        Intent serviceIntent = new Intent(mContext, NotificationService.class);
                        serviceIntent.setAction(NotificationService.VIBRATE);
                        mContext.startService(serviceIntent);
                        return Single.just(Action.create(NO_OP));
                    }).toObservable();
                }
            }
        }  else if (action.getType()== NEXT_TIMER && action.getPayload().isPresent()) {
            NextTimerModel payload = NextTimerModel.fromParcel((Parcel) action.getPayload().get());

            int newPosition = (state.timerPosition() + 1) % state.timers().size();
            Timer newTimer = state.timers().get(newPosition);

            if (newPosition != state.timerPosition()) {
                result = state.toBuilder()
                    .paused(Boolean.FALSE)
                    .startTime(payload.currentTime())
                    .name(newTimer.name())
                    .duration(newTimer.duration())
                    .previouslyCompleted(0L)
                    .currentTime(payload.currentTime())
                    .notifications(0)
                    .timerPosition(newPosition)
                    .build();
            }
        } else if(action.getType() == PAUSE_TIMER) {
            PauseTimerModel payload =
                PauseTimerModel.fromParcel((Parcel) action.getPayload().get());
            if(state.paused()) {
                result = state.toBuilder()
                    .paused(Boolean.FALSE)
                    .startTime(payload.currentTime())
                    .currentTime(payload.currentTime())
                    .build();
            }
            else {
                result = state.toBuilder()
                    .paused(Boolean.TRUE)
                    .previouslyCompleted(state.previouslyCompleted() +
                        payload.currentTime() - state.startTime())
                    .startTime(payload.currentTime())
                    .currentTime(payload.currentTime())
                    .build();
            }
        } else if (action.getType() == START_TIMER) {
            StartTimerPayload payload = StartTimerPayload.fromParcel((Parcel) action.getPayload().get());

            result = state.toBuilder()
                .timers(state.timers())
                .paused(Boolean.FALSE)
                .stopped(Boolean.FALSE)
                .startTime(payload.time())
                .currentTime(payload.time())
                .notifications(0)
                .build();
        } else if (action.getType() == STOP_TIMER) {
            result = initialState();
        }

        return Pair.create(result, effects);
    }
}
