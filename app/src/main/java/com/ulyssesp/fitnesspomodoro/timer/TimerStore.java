package com.ulyssesp.fitnesspomodoro.timer;

import android.os.Parcel;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.flrx.Action;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;
import com.ulyssesp.fitnesspomodoro.flrx.Store;
import com.ulyssesp.fitnesspomodoro.models.Timer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;

import static com.ulyssesp.fitnesspomodoro.Constants.Action.FETCH_TIMERS;
import static com.ulyssesp.fitnesspomodoro.Constants.Action.NEXT_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.Action.RECEIVE_TIMERS;
import static com.ulyssesp.fitnesspomodoro.Constants.Action.TICK_TIMER;
import static com.ulyssesp.fitnesspomodoro.Constants.Action.TIMER_CHANGED;

public class TimerStore extends Store<TimerStoreModel, Constants.Action> {

    public TimerStore(Dispatcher<Constants.Action> dispatcher) {
        super(dispatcher, EnumSet.of(FETCH_TIMERS, NEXT_TIMER, RECEIVE_TIMERS, TICK_TIMER));
    }

    @Override
    protected TimerStoreModel initialState() {
        return TimerStoreModel.create(Collections.emptyList(), new Date().getTime(),
            Boolean.TRUE, 0L, new Date().getTime(), 0);
    }

    @Override
    public TimerStoreModel reducer(TimerStoreModel state, Action action) {
        TimerStoreModel result = state;

        if (action.getType()==Constants.Action.TICK_TIMER && action.getPayload().isPresent()) {
            TickTimerModel payload =
                TickTimerModel.fromParcel((Parcel) action.getPayload().get());

            result = TimerStoreModel.create(
                state.timers(),
                state.startTime(),
                false,
                state.currentDuration(),
                payload.currentTime(),
                state.timerPosition());
        }  else if (action.getType()==Constants.Action.NEXT_TIMER && action.getPayload().isPresent()) {
            NextTimerModel payload = NextTimerModel.fromParcel((Parcel) action.getPayload().get());

            int newPosition = (payload.currentPosition() + 1) % state.timers().size();
            Timer newTimer = state.timers().get(newPosition);

            if(newPosition != state.timerPosition()) {
                result = TimerStoreModel.create(
                        state.timers(),
                        payload.currentTime(),
                        false,
                        newTimer.duration(),
                        payload.currentTime(),
                        newPosition
                    );
            }

            postAction(Action.create(TIMER_CHANGED, TimerChangedModel.create(newTimer)));
        } else if(action.getType()==FETCH_TIMERS) {
            postAction(Action.create(Constants.Action.RECEIVE_TIMERS,
                ReceiveTimersModel.create(Arrays.asList(TimerData.TIMERS))));
        } else if (action.getType()==RECEIVE_TIMERS) {
            ReceiveTimersModel payload =
                ReceiveTimersModel.fromParcel((Parcel) action.getPayload().get());
            result = TimerStoreModel.create(
                    payload.timers(),
                    state.currentTime(),
                    state.paused(),
                    payload.timers().get(state.timerPosition()).duration(),
                    state.currentTime(),
                    state.timerPosition()
                );
        }

        return result;
    }
}
