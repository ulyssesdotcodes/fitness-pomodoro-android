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
import com.ulyssesp.fitnesspomodoro.data.timer.TickTimerModel;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStoreModel;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class NotificationService extends Service {
    @Inject Dispatcher<Constants.Actions> mDispatcher;

    @Inject TimerStore mStore;

    public static String VIBRATE = "vibrate";
    public static String START = "start";

    private static int NOTIFICATION_ID = 12345;

    private NotificationCompat.Builder mNotification;
    private Subscription mStoreSubscription;
    private Subscription mIntervalSubscription;

    @Override
    public void onCreate() {
        super.onCreate();

        ((FitnessPomodoroApplication) getApplication()).getAppComponent().inject(this);

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
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pi =
                PendingIntent.getActivity(getApplicationContext(),
                    0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mNotification =
                new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("Pomodoro starting")
                    .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setOngoing(true)
                    .setContentIntent(pi)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);

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
                    mStore.dataObservable()
                        .sample(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateNotification);
            }
        } else if(intent.getAction().equals(VIBRATE)) {
            mNotification.setDefaults(Notification.DEFAULT_VIBRATE);

            getNotificationManager().notify(NOTIFICATION_ID, mNotification.build());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateNotification(TimerStoreModel timerStoreModel){
        if (timerStoreModel.stopped()) {
            stopForeground(true);
            return;
        }

        long sectionTimeCompleted =
            timerStoreModel.currentTime() - timerStoreModel.startTime();

        long timeCompleted = sectionTimeCompleted + timerStoreModel.previouslyCompleted();
        long timeRemaining = timerStoreModel.duration() - timeCompleted;

        boolean completed = timeCompleted > timerStoreModel.duration();

        startForeground(NOTIFICATION_ID, mNotification.build());

        mNotification
            .setContentTitle((completed ? "Extending " : "") + timerStoreModel.name())
            .setContentText(completed ?
                formatMillis(timeCompleted) + " completed" :
                formatMillis(timeRemaining) + " remaining")
            .setWhen(timerStoreModel.startTime() - timerStoreModel.previouslyCompleted())
            .setDefaults(0);

        getNotificationManager().notify(NOTIFICATION_ID, mNotification.build());
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
