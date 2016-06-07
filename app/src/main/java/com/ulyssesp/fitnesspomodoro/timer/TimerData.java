package com.ulyssesp.fitnesspomodoro.timer;

import com.ulyssesp.fitnesspomodoro.models.Timer;

public class TimerData {
    static Timer[] TIMERS = {
        Timer.create("Work", 25L * 1000L, false),
        Timer.create("Break", 5L * 1000L, true)
    };
}
