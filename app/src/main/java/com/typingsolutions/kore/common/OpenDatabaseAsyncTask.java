package com.typingsolutions.kore.common;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import net.sqlcipher.database.SQLiteDatabase;

class OpenDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
  private DatabaseConnection mDatabaseConnection;
  private Handler mHandler;
  private int mExitCode;

  OpenDatabaseAsyncTask(@NonNull DatabaseConnection databaseConnection, Handler handler) {
    mDatabaseConnection = databaseConnection;
    mHandler = handler;
    mExitCode = 0;
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      SQLiteDatabase database = mDatabaseConnection.getWritableDatabase();
      if(database != null && database.isOpen()){
        mExitCode = 1;
      }
    } catch (Exception e) {
      // TODO: send extra code for wrong password?
      mExitCode = 2;
    }

    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if(mHandler != null)
      mHandler.sendEmptyMessage(mExitCode);
  }
}
