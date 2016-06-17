package com.ulyssesp.fitnesspomodoro.data.timer;

public class TimerData {
     public static Timer[] TIMERS = {
        Timer.create("Work", 25L * 60L * 1000L, false),
        Timer.create("Break", 5L * 60L * 1000L, true)
    };
}
