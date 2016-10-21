package com.typingsolutions.passwordmanager.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import com.typingsolutions.passwordmanager.fragments.ScheduleBackupFragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BackupHelper extends BroadcastReceiver {
  private static final String SHARED_PREFS = " com.typingsolutions.passwordmanager.activities.BackupActivity.xml";
  private static final int FAILED_NOTIFICATION_ID = 563465457;

  public BackupHelper() {

  }

  @Override
  public void onReceive(Context context, Intent intent) {
    SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    String accountName = preferences.getString(ScheduleBackupFragment.PREF_ACCOUNT_NAME, null);

    if(accountName == null) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
          .setContentTitle("Could not create a Backup, because no there is no valid Google account")
          .setAutoCancel(true)
          .setPriority(Notification.PRIORITY_HIGH)
          .setSmallIcon(R.drawable.lock_outline);

      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(FAILED_NOTIFICATION_ID, builder.build());

      return;
    }

    try {
      GoogleAccountCredential credential = GoogleAccountCredential
          .usingOAuth2(context.getApplicationContext(), Arrays.asList(ScheduleBackupFragment.SCOPES))
          .setBackOff(new ExponentialBackOff());

      credential.setSelectedAccountName(accountName);
      HttpTransport transport = AndroidHttp.newCompatibleTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      Drive service = new Drive.Builder(transport, jsonFactory, credential)
          .setApplicationName("kore")
          .build();

      File folderMetadata = new File();
      folderMetadata.setName("kore");
      folderMetadata.setMimeType("application/vnd.google-apps.folder");

      List<File> results = service.files().list().setQ("name='kore' and trashed=false").execute().getFiles();
      File folder = null;

      if (results.isEmpty()) {
        folder = service.files()
            .create(folderMetadata)
            .setFields("id, name")
            .execute();

      } else {
        folder = results.get(0);
      }

      java.io.File local = context.getDatabasePath(DatabaseConnection.DATABASE_NAME);
      FileContent localContent = new FileContent("application/octet-stream", local);
      File remote = new File();
      remote.setName("backup-123");
      remote.setDescription("This is backup file created by the awesome password manager kore.");
      remote.setParents(Collections.singletonList(folder.getId()));

      File file = service.files()
          .create(remote, localContent)
          .setFields("id, parents")
          .execute();

      if(file.getId() == null) {
        throw new Exception("Backup was not created!");
      }

      // TODO: reschedule

    } catch (Exception e) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
          .setContentTitle("There was an error during uploading: " + e.getMessage())
          .setAutoCancel(true)
          .setPriority(Notification.PRIORITY_HIGH)
          .setSmallIcon(R.drawable.lock_outline);

      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(FAILED_NOTIFICATION_ID, builder.build());
    }
  }
}
