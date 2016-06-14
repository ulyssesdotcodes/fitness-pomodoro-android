package com.ulyssesp.fitnesspomodoro;

public class Constants {
    public enum TimerActions {
        FETCH_TIMERS,
        NEXT_TIMER,
        PAUSE_TIMER,
        RECEIVE_TIMERS,
        TICK_TIMER,
        TIMER_CHANGED,
        INCREMENT_NOTIFICATIONS
    }

    public static long NOTIFICATION_TIME = 10L * 1000L;
}
