package com.typingsolutions.passwordmanager.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public final class BackupScheduleHelper {
  public static final int EVERY_DAY = 0;
  public static final int ONCE_A_WEEK = 1;
  public static final int ONCE_A_MONTH = 2;
  public static final int ID = 12345678;

  public static void schedule(Context context, int when, boolean init) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    if(init || when == EVERY_DAY) {
      calendar.add(Calendar.DATE, 1);
    } else if(when == ONCE_A_WEEK) {
      calendar.add(Calendar.DATE, 7);
    } else if(when == ONCE_A_MONTH) {
      int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
      calendar.add(Calendar.DATE, days);
    }
    calendar.set(Calendar.HOUR_OF_DAY, 2);

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(context, BackupHelper.class);
    intent.putExtra("id", ID);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
  }

  public static void cancel(Context context) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(context, BackupHelper.class);
    intent.putExtra("id", ID);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    alarmManager.cancel(pendingIntent);
  }
}
