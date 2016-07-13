package com.typingsolutions.passwordmanager.async;

import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseAsyncTask;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import core.Utils;
import core.data.Password;
import core.data.PasswordHistory;
import net.sqlcipher.Cursor;

import java.text.ParseException;
import java.util.Date;

public class LoadPasswordsTask extends BaseAsyncTask<Void, Void, Void> {

  private Cursor mCursor;

  @Override
  protected Void doInBackground(Void... params) {
    if (params == null) return null;

    try {
      mCursor = BaseDatabaseActivity.getDatabase().rawQuery(DatabaseConnection.GET_PASSWORDS, null);

      if (!mCursor.moveToNext())
        return null;

      Password password = getPassword();
      //Log.d(getClass().getSimpleName(), String.format("%s: %s - %s - %s", password.getId(), password.getProgram(), password.getUsername(), password.getFirstItem()));

      while (mCursor.moveToNext()) {
        Password nextPassword = getPassword();
        //Log.d(getClass().getSimpleName(), String.format("%s: %s - %s - %s", nextPassword.getId(), nextPassword.getProgram(), nextPassword.getUsername(), nextPassword.getFirstItem()));

        if (nextPassword.equals(password)) {
          password.merge(nextPassword);
        } else {
          // TODO: add password somewhere
          password = nextPassword;
        }
      }

    } catch (Exception e) {
      BaseActivity.showErrorLog(getClass(), e);
    } finally {
      if (mCursor != null) {
        mCursor.close();
        mCursor = null;
      }
    }

    return null;
  }

  private Password getPassword() {
    int id = mCursor.getInt(0);
    String username = mCursor.getString(1);
    String program = mCursor.getString(2);
    int position = mCursor.getInt(3);
    int historyId = mCursor.getInt(4);

    Password password = new Password(id, position, username, program);

    PasswordHistory history = getHistory();
    password.addPasswordHistoryItem(historyId, history);

    return password;
  }

  private PasswordHistory getHistory() {
    try {
      String value = mCursor.getString(5);
      String dateString = mCursor.getString(6);
      Date changed = Utils.getDateFromString(dateString);
      return new PasswordHistory(value, changed);
    } catch (ParseException e) {
      return null;
    }
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
    if (mCursor != null) {
      mCursor.close();
      mCursor = null;
    }
  }
}
