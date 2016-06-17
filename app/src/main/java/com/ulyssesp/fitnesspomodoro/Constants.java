package com.ulyssesp.fitnesspomodoro;

public class Constants {
    public enum Actions {
        NEXT_TIMER,
        PAUSE_TIMER,
        TICK_TIMER,
        TIMER_CHANGED,
        START_TIMER,
        STOP_TIMER,

        FETCH_EXERCISES,
        RECEIVE_EXERCISES,

        NO_OP
    }

    public static long NOTIFICATION_TIME = 3L * 60L * 1000L;
}
