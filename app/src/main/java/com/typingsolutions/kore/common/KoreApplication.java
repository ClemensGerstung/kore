package com.typingsolutions.kore.common;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;


public class KoreApplication extends Application {

  private DatabaseConnection mDatabaseConnection;
  private IEvent<Integer> mOnDatabaseOpened;
  private OpenDatabaseHandler mHandler = new OpenDatabaseHandler(mOnDatabaseOpened);
  private OpenDatabaseAsyncTask mOpenDatabaseTask = new OpenDatabaseAsyncTask(mDatabaseConnection, mHandler);

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(getClass().getSimpleName(), "onCreate - Application");
    SQLiteDatabase.loadLibs(this);
  }

  @Override
  public void onTerminate() {
    super.onTerminate();

    if(mOpenDatabaseTask.getStatus() == AsyncTask.Status.RUNNING) {
      mOpenDatabaseTask.cancel(true);
    }

    if(mDatabaseConnection != null) {
      mDatabaseConnection.close();
    }
  }

  public DatabaseConnection getDatabaseConnection() {
    return mDatabaseConnection;
  }

  public void openDatabaseConnection(String password, int pim) {
    if(mDatabaseConnection != null)
      return;

    mDatabaseConnection = new DatabaseConnection(this, "", 0, password, pim);

    mOpenDatabaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  public void setOnDatabaseOpened(IEvent<Integer> onDatabaseOpened) {
    mOnDatabaseOpened = onDatabaseOpened;
  }
}



