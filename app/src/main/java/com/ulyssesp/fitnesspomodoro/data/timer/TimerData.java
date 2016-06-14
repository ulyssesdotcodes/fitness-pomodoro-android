package com.ulyssesp.fitnesspomodoro.data.timer;

public class TimerData {
    static Timer[] TIMERS = {
        Timer.create("Work", 25L * 1000L, false),
        Timer.create("Break", 5L * 1000L, true)
    };
}
