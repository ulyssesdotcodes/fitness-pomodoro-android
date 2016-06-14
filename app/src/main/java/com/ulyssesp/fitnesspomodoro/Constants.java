package com.ulyssesp.fitnesspomodoro;

public class Constants {
    public enum Actions {
        FETCH_TIMERS,
        NEXT_TIMER,
        PAUSE_TIMER,
        RECEIVE_TIMERS,
        TICK_TIMER,
        TIMER_CHANGED,
        INCREMENT_NOTIFICATIONS,

        FETCH_EXERCISES,
        RECEIVE_EXERCISES,
        RANDOM_EXERCISE,
        CLEAR_EXERCISE
    }

    public static long NOTIFICATION_TIME = 10L * 1000L;
}
