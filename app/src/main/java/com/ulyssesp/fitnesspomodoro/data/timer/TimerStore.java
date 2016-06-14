package com.ulyssesp.fitnesspomodoro.data.timer;

import android.os.Parcel;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.data.models.Timer;
import com.ulyssesp.fitnesspomodoro.flrx.Action;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.flrx.Store;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;

import static com.ulyssesp.fitnesspomodoro.Constants.TimerActions.FETCH_TIMERS;
import static com.ulyssesp.fitnesspomodoro.Constants.TimerActions.INCREMENT_NOTIFICATIONS;
import static com.ulyssesp.fitnesspomodoro.Constants.TimerActions.PAUSE_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.TimerActions.RECEIVE_TIMERS;
import static com.ulyssesp.fitnesspomodoro.Constants.TimerActions.TIMER_CHANGED;

public class TimerStore extends Store<TimerStoreModel, Constants.TimerActions> {

    public TimerStore(Dispatcher<Constants.TimerActions> dispatcher) {
        super(dispatcher, EnumSet.allOf(Constants.TimerActions.class));
    }

    @Override
    protected TimerStoreModel initialState() {
        return TimerStoreModel.builder()
            .timers(Collections.emptyList())
            .paused(Boolean.TRUE)
            .currentTime(new Date().getTime())
            .name("")
            .duration(0L)
            .previouslyCompleted(0L)
            .startTime(new Date().getTime())
            .notifications(0)
            .timerPosition(0)
            .build();
    }

    @Override
    public TimerStoreModel reducer(TimerStoreModel state, Action action) {
        TimerStoreModel result = state;

        if (action.getType()== Constants.TimerActions.TICK_TIMER && action.getPayload().isPresent()) {
            TickTimerModel payload =
                TickTimerModel.fromParcel((Parcel) action.getPayload().get());

            if(!state.paused()) {
                result = state.toBuilder()
                    .currentTime(payload.currentTime())
                    .build();
            }
        }  else if (action.getType()== Constants.TimerActions.NEXT_TIMER && action.getPayload().isPresent()) {
            NextTimerModel payload = NextTimerModel.fromParcel((Parcel) action.getPayload().get());

            if (state.timers().isEmpty()) {
                postAction(Action.create(Constants.TimerActions.RECEIVE_TIMERS,
                    ReceiveTimersModel.create(Arrays.asList(TimerData.TIMERS), payload.currentTime())
                ));
            }
            else {
                int newPosition = (state.timerPosition() + 1) % state.timers().size();
                Timer newTimer = state.timers().get(newPosition);

                if (newPosition != state.timerPosition()) {
                    result = state.toBuilder()
                        .paused(false)
                        .startTime(payload.currentTime())
                        .name(newTimer.name())
                        .duration(newTimer.duration())
                        .previouslyCompleted(0L)
                        .currentTime(payload.currentTime())
                        .notifications(0)
                        .timerPosition(newPosition)
                        .build();
                }

                postAction(Action.create(TIMER_CHANGED, TimerChangedModel.create(newTimer)));
            }
        } else if(action.getType()==FETCH_TIMERS) {
            postAction(Action.create(Constants.TimerActions.RECEIVE_TIMERS,
                ReceiveTimersModel.create(Arrays.asList(TimerData.TIMERS),
                    System.currentTimeMillis())));
        } else if (action.getType()==RECEIVE_TIMERS) {
            ReceiveTimersModel payload =
                ReceiveTimersModel.fromParcel((Parcel) action.getPayload().get());
            Timer initialTimer = payload.timers().get(state.timerPosition());
            result = state.toBuilder()
                .timers(payload.timers())
                .paused(false)
                .startTime(payload.time())
                .name(initialTimer.name())
                .duration(initialTimer.duration())
                .currentTime(payload.time())
                .notifications(0)
                .build();
        } else if(action.getType() == INCREMENT_NOTIFICATIONS) {
            result = state.toBuilder()
                .notifications(state.notifications() + 1)
                .build();
        } else if(action.getType() == PAUSE_TIMER) {
            PauseTimerModel payload =
                PauseTimerModel.fromParcel((Parcel) action.getPayload().get());
            if(state.paused()) {
                result = state.toBuilder()
                    .paused(false)
                    .startTime(payload.currentTime())
                    .currentTime(payload.currentTime())
                    .build();
            }
            else {
                result = state.toBuilder()
                    .paused(true)
                    .previouslyCompleted(state.previouslyCompleted() +
                        payload.currentTime() - state.startTime())
                    .startTime(payload.currentTime())
                    .currentTime(payload.currentTime())
                    .build();
            }
        }

        return result;
    }
}
