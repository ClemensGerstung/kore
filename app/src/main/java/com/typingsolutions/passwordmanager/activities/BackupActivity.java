package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.ToolbarNavigationCallback;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.database.BackupDatabaseConnection;
import com.typingsolutions.passwordmanager.utils.AutoDismissNotification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class BackupActivity extends BaseDatabaseActivity {
  public static final int BACKUP_REQUEST_CODE = 36;
  public static final int RESTORE_REQUEST_CODE = 37;

  private Toolbar mToolbarAsActionbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backup_layout);

    mToolbarAsActionbar = findCastedViewById(R.id.backuprestorelayout_toolbar_actionbar);
    setSupportActionBar(mToolbarAsActionbar);
    mToolbarAsActionbar.setNavigationOnClickListener(new ToolbarNavigationCallback(this));
  }


  @Override
  protected View getSnackbarRelatedView() {
    return this.mToolbarAsActionbar;
  }

  @Override
  protected void onActivityChange() {

  }

  @Nullable
  public File backup(@NonNull String password, @Nullable FileOutputStream target) {
    File file = getDatabasePath(BackupDatabaseConnection.NAME);
    File tmp = null;
    try {
      PasswordContainer[] passwords = new PasswordContainer[containerCount()];
      getItems().toArray(passwords);
      if (file.exists()) file.delete();

      BackupDatabaseConnection connection = new BackupDatabaseConnection(this, password, 0x400);
      connection.load(passwords);
      connection.close();

      FileInputStream source = new FileInputStream(file);
      if (target == null) {
        tmp = new File(file.getAbsolutePath() + ".tmp");

        //noinspection ResultOfMethodCallIgnored
        tmp.createNewFile();
        target = new FileOutputStream(tmp);
      }

      byte[] buffer = new byte[512];
      int length;
      int size = 0;

      ByteBuffer byteBuffer = ByteBuffer.allocate(512);
      long seed = SystemClock.elapsedRealtimeNanos();
      byteBuffer.putLong(seed);
      Random r = new Random(seed);

      for (int i = 0; i < 63; i++) {
        byteBuffer.putLong(r.nextLong()); // generate random padding.
      }

      byte[] time = byteBuffer.array();
      byteBuffer.clear();

      MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");

      while ((length = source.read(buffer)) > 0) {
        shaDigest.update(buffer, 0, length);
      }

      byteBuffer.put(shaDigest.digest());

      for (int i = 0; i < (512 - 32) / 8; i++) {
        byteBuffer.putLong(r.nextLong()); // generate random padding.
      }

      byte[] hash = byteBuffer.array();

      source.close();
      source = new FileInputStream(file);

      target.write(time, 0, time.length);
      target.write(hash, 0, hash.length);

      while ((length = source.read(buffer)) > 0) {
        target.write(buffer, 0, length);
        size += length;
      }

      source.close();
      target.close();

      AutoDismissNotification.notify(this, "Backup done successful", 0x123, 5000);

    } catch (NoSuchAlgorithmException | IOException e) {
      // TODO: send notification!
      makeSnackbar("Backup failed!");

    } finally {
      if (file.exists() && file.delete()) {
        Log.d(getClass().getSimpleName(), "Backup done!");
      }
    }

    return tmp;
  }
}
