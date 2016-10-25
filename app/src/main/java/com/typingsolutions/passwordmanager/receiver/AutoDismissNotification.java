package com.typingsolutions.passwordmanager.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.typingsolutions.passwordmanager.R;

import java.util.Calendar;


public class AutoDismissNotification extends BroadcastReceiver {

  public AutoDismissNotification() {

  }

  public static AutoDismissNotification notify(Context context, String text, int id, int dismissTime) {
    AutoDismissNotification notification = new AutoDismissNotification();

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
        .setContentTitle(text)
        .setAutoCancel(true)
        .setPriority(Notification.PRIORITY_HIGH)
        .setSmallIcon(R.drawable.lock_outline);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(id, builder.build());

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(context, AutoDismissNotification.class);
    intent.putExtra("id", id);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    Calendar time = Calendar.getInstance();
    time.setTimeInMillis(System.currentTimeMillis());
    time.add(Calendar.MILLISECOND, dismissTime);

    alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);

    return notification;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    int id = intent.getIntExtra("id", 0);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(id);
    Log.d(getClass().getSimpleName(), "onReceive");
  }
}
