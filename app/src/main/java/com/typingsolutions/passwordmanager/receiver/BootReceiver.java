package com.typingsolutions.passwordmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.typingsolutions.passwordmanager.activities.BackupActivity;
import com.typingsolutions.passwordmanager.fragments.ScheduleBackupFragment;
import com.typingsolutions.passwordmanager.services.LoginService;
import com.typingsolutions.passwordmanager.utils.BackupScheduleHelper;

public class BootReceiver extends BroadcastReceiver {
  private static final String SHARED_PREFS = BackupActivity.class.getName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Intent serviceIntent = new Intent(context, LoginService.class);
    context.startService(serviceIntent);

    SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    int time = preferences.getInt(ScheduleBackupFragment.PREF_SCHEDULE_TIME, 0);

    BackupScheduleHelper.schedule(context, time, true);
  }
}
