package com.typingsolutions.passwordmanager.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.BackupActivity;
import com.typingsolutions.passwordmanager.async.GDriveBackupUploader;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import com.typingsolutions.passwordmanager.fragments.ScheduleBackupFragment;
import com.typingsolutions.passwordmanager.utils.BackupScheduleHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BackupHelper extends BroadcastReceiver {
  private static final String SHARED_PREFS = BackupActivity.class.getName();
  public static final int FAILED_NOTIFICATION_ID = 563465457;

  public BackupHelper() {

  }

  @Override
  public void onReceive(Context context, Intent intent) {
    SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    String accountName = preferences.getString(ScheduleBackupFragment.PREF_ACCOUNT_NAME, null);
    int time = preferences.getInt(ScheduleBackupFragment.PREF_SCHEDULE_TIME, -1);

    if(accountName == null) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
          .setContentTitle("Could not create a Backup")
          .setContentText("There was no valid Google Account")
          .setAutoCancel(true)
          .setPriority(Notification.PRIORITY_HIGH)
          .setSmallIcon(R.drawable.lock_outline);

      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(FAILED_NOTIFICATION_ID, builder.build());

      return;
    }

    try {
      if(!core.Utils.isDeviceOnline(context))
        throw new Exception("Device wasn't online");

      GDriveBackupUploader uploader = new GDriveBackupUploader(context);
      uploader.execute(accountName);

      if(time == -1) {
        throw new Exception("Didn't know when to reschedule");
      }

      BackupScheduleHelper.schedule(context, time, false);
    } catch (Exception e) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
          .setContentTitle("There was an error during uploading")
          .setContentText(e.getMessage())
          .setAutoCancel(true)
          .setPriority(Notification.PRIORITY_HIGH)
          .setSmallIcon(R.drawable.lock_outline);

      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(FAILED_NOTIFICATION_ID, builder.build());
    }
  }
}
