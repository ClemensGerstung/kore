package com.typingsolutions.passwordmanager.async;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
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
import com.typingsolutions.passwordmanager.receiver.BackupHelper;
import core.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GDriveBackupUploader extends AsyncTask<String, Void, Void> {
  private Context mContext;

  public GDriveBackupUploader(Context context) {
    mContext = context;
  }

  @Override
  protected Void doInBackground(String... params) {
    try {
      GoogleAccountCredential credential = GoogleAccountCredential
          .usingOAuth2(mContext.getApplicationContext(), Arrays.asList(ScheduleBackupFragment.SCOPES))
          .setBackOff(new ExponentialBackOff());

      credential.setSelectedAccountName(params[0]);
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

      java.io.File local = mContext.getDatabasePath(DatabaseConnection.DATABASE_NAME);
      FileContent localContent = new FileContent("application/octet-stream", local);
      File remote = new File();
      remote.setName("backup-" + Utils.getToday());
      remote.setDescription("This is backup file created by the awesome password manager kore.");
      remote.setParents(Collections.singletonList(folder.getId()));

      File file = service.files()
          .create(remote, localContent)
          .setFields("id, parents")
          .execute();

      if(file.getId() == null) {
        throw new IOException("Backup was not created!");
      }
    } catch (IOException e) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
          .setContentTitle("There was an error during uploading")
          .setContentText(e.getMessage())
          .setAutoCancel(true)
          .setPriority(Notification.PRIORITY_HIGH)
          .setSmallIcon(R.drawable.lock_outline);

      NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(BackupHelper.FAILED_NOTIFICATION_ID, builder.build());
    }
    return null;
  }
}
