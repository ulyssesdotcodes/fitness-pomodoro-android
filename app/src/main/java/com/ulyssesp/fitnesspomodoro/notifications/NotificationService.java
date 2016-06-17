package com.ulyssesp.fitnesspomodoro.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.FitnessPomodoroApplication;
import com.ulyssesp.fitnesspomodoro.MainActivity;
import com.ulyssesp.fitnesspomodoro.R;
import com.ulyssesp.fitnesspomodoro.data.exercise.ExerciseStore;
import com.ulyssesp.fitnesspomodoro.data.exercise.ExerciseStoreModel;
import com.ulyssesp.fitnesspomodoro.data.timer.NextTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.PauseTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.TickTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStoreModel;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class NotificationService extends Service {
    @Inject Dispatcher<Constants.Actions> mDispatcher;

    @Inject TimerStore mTimerStore;
    @Inject ExerciseStore mExerciseStore;

    public static String VIBRATE = "vibrate";
    public static String START = "start";
    public static String PAUSE = "pause";
    public static String NEXT_TIMER = "next";
    public static String STOP = "stop";
    public static String RESUME = "resume";

    private static int NOTIFICATION_ID = 12345;

    private NotificationCompat.Builder mNotification;
    private Subscription mStoreSubscription;
    private Subscription mIntervalSubscription;

    private Map<String, NotificationCompat.Action> mActions;


    @Override
    public void onCreate() {
        super.onCreate();

        ((FitnessPomodoroApplication) getApplication()).getAppComponent().inject(this);

        mActions = new HashMap<>(3);

        PendingIntent pause = createActionIntent(PAUSE);
        PendingIntent stop = createActionIntent(STOP);
        PendingIntent nextTimer = createActionIntent(NEXT_TIMER);

        mActions.put(STOP, new NotificationCompat.Action(R.drawable.ic_stop_black_24dp, STOP, stop));
        mActions.put(PAUSE, new NotificationCompat.Action(R.drawable.ic_pause_black_24dp, PAUSE, pause));
        mActions.put(NEXT_TIMER, new NotificationCompat.Action(R.drawable.ic_skip_next_black_24dp, NEXT_TIMER, nextTimer));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mIntervalSubscription.unsubscribe();
        mStoreSubscription.unsubscribe();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mNotification == null) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent defaultPi =
                PendingIntent.getActivity(getApplicationContext(),
                    0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            mNotification =
                new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("Pomodoro starting")
                    .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setOngoing(true)
                    .setContentIntent(defaultPi)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);

            updateActions();

            startForeground(NOTIFICATION_ID, mNotification.build());
        }

        if(intent.getAction().equals(START)) {
            if (mIntervalSubscription == null) {
                mIntervalSubscription =
                    Observable.interval(1, TimeUnit.SECONDS)
                        .subscribe(__ ->
                            mDispatcher.postAction(
                                Constants.Actions.TICK_TIMER,
                                TickTimerModel.create(System.currentTimeMillis())
                            ));
            }

            if (mStoreSubscription == null) {
                mStoreSubscription =
                    Observable.combineLatest(
                        mTimerStore.dataObservable(),
                        mExerciseStore.dataObservable(),
                        this::createNotificationModel
                    )
                        .sample(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateNotification);
            }
        } else if(intent.getAction().equals(VIBRATE)) {
            mNotification.setDefaults(Notification.DEFAULT_VIBRATE);

            getNotificationManager().notify(NOTIFICATION_ID, mNotification.build());
        } else if (intent.getAction().equals(STOP)) {
            mDispatcher.postAction(Constants.Actions.STOP_TIMER);
        } else if (intent.getAction().equals(PAUSE)) {
            mDispatcher.postAction(Constants.Actions.PAUSE_TIMER,
                PauseTimerModel.create(System.currentTimeMillis()));
        } else if (intent.getAction().equals(NEXT_TIMER)) {
            mDispatcher.postAction(Constants.Actions.NEXT_TIMER,
                NextTimerModel.create(System.currentTimeMillis()));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private NotificationModel createNotificationModel(TimerStoreModel timerStoreModel,
                                         ExerciseStoreModel exerciseStoreModel) {
        long sectionTimeCompleted =
            timerStoreModel.currentTime() - timerStoreModel.startTime();

        long timeCompleted = sectionTimeCompleted + timerStoreModel.previouslyCompleted();
        long timeRemaining = timerStoreModel.duration() - timeCompleted;

        boolean completed = timeCompleted > timerStoreModel.duration();

        String title = (completed ? "Extending " : "") + timerStoreModel.name();
        long completionTime = completed ?
            timeCompleted - timerStoreModel.duration() :
            timerStoreModel.duration() - timerStoreModel.previouslyCompleted() +
                timerStoreModel.startTime();

        String exercise = exerciseStoreModel.currentExercise().isPresent() ?
            ". Exercise!" : "";

        String content =
            formatMillis(timeRemaining) + (completed ? " completed" : " remaining") + exercise;

        return NotificationModel.builder()
            .stopped(timerStoreModel.stopped())
            .title(title)
            .content(content)
            .when(completionTime)
            .paused(timerStoreModel.paused())
            .build();
    }

    private void updateNotification(NotificationModel notificationModel){
        if (notificationModel.stopped()) {
            stopForeground(true);
            return;
        }

        startForeground(NOTIFICATION_ID, mNotification.build());

        mNotification
            .setContentTitle(notificationModel.title())
            .setContentText(notificationModel.content())
            .setWhen(notificationModel.when())
            .setDefaults(0);

        PendingIntent pi = mActions.get(PAUSE).getActionIntent();
        mActions.put(PAUSE, new NotificationCompat.Action(
            notificationModel.paused() ?
                R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_pause_black_24dp,
            notificationModel.paused() ? RESUME : PAUSE,
            pi
        ));
        updateActions();

        getNotificationManager().notify(NOTIFICATION_ID, mNotification.build());
    }

    private void updateActions() {
        mNotification.mActions.clear();

        mNotification
            .addAction(mActions.get(STOP))
            .addAction(mActions.get(PAUSE))
            .addAction(mActions.get(NEXT_TIMER));
    }

    private PendingIntent createActionIntent(String action) {
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.setAction(action);
        PendingIntent pi =
            PendingIntent.getService(getApplicationContext(), 0, intent, 0);

        return pi;
    }

    private String formatMillis(long millis) {
        long secondsRemaining = millis / 1000;
        long minutesRemaining = secondsRemaining / 60;
        return minutesRemaining > 0 ? String.format("%d minutes", minutesRemaining) : "<1 minute";
    }

    private NotificationManagerCompat getNotificationManager() {
        return  NotificationManagerCompat.from(getApplicationContext());
    }
}
