package com.ulyssesp.fitnesspomodoro.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ulyssesp.fitnesspomodoro.Constants;
import com.ulyssesp.fitnesspomodoro.MainActivity;
import com.ulyssesp.fitnesspomodoro.R;
import com.ulyssesp.fitnesspomodoro.data.timer.TimerStore;
import com.ulyssesp.fitnesspomodoro.flrx.Action;
import com.ulyssesp.fitnesspomodoro.flrx.Dispatcher;

public class TimerNotificationManager {
    NotificationManager mNotificationManager;

    public TimerNotificationManager(TimerStore timerStore, final Dispatcher<Constants.TimerActions> dispatcher, Context context){
        mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        timerStore.dataObservable()
            .filter(timerStoreModel -> {
                long sectionTimeCompleted =
                    timerStoreModel.currentTime() - timerStoreModel.startTime();
                long timeCompleted = sectionTimeCompleted + timerStoreModel.previouslyCompleted();

                return  timerStoreModel.duration() > 0 &&
                    timeCompleted > timerStoreModel.duration() &&
                    timerStoreModel.notifications() * Constants.NOTIFICATION_TIME <
                        timeCompleted - timerStoreModel.duration();
                })
            .subscribe(completedTimer -> {
                String title =
                    completedTimer.notifications() == 0 ? "Completed " : "Extending " +
                        completedTimer.name();


                NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                        .setContentTitle(title)
                        .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                        .setAutoCancel(true);

                Intent resultIntent = new Intent(context, MainActivity.class);
                PendingIntent pi =
                    PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(pi);

                mNotificationManager.notify(completedTimer.notifications(), builder.build());

                dispatcher.postAction(Action.create(Constants.TimerActions.INCREMENT_NOTIFICATIONS));
            });
    }
}
