package com.typingsolutions.passwordmanager.async;

import android.util.Log;
import com.typingsolutions.passwordmanager.BaseAsyncTask;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import net.sqlcipher.database.SQLiteDatabase;

public class OpenDatabaseTask extends BaseAsyncTask<Void, Boolean> {
  @Override
  protected Boolean doInBackground(DatabaseConnection... params) {
    if(params.length == 0 || params[0] == null) return false;

    DatabaseConnection connection = params[0];
    boolean result = false;
    try {
      SQLiteDatabase database = connection.getDatabase();
      if (database == null) return false;

      result = database.isOpen();
      database.close();
    } catch (Exception e) {
      result = false;
    }

    return result;
  }

  @Override
  protected void onPostExecute(Boolean result) {
    if(result) {
      raiseCallbacks(result);
    } else {
      raiseCallbacks(1, "Could not open database");
    }
  }
}
