package com.typingsolutions.passwordmanager.async;

import android.util.Log;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseAsyncTask;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.dao.PasswordItem;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import core.Utils;
import core.data.Password;
import core.data.PasswordHistory;
import net.sqlcipher.Cursor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoadPasswordsTask extends BaseAsyncTask<Void, Void, PasswordContainer> {

  private Cursor mCursor;

  @Override
  protected PasswordContainer doInBackground(Void... params) {
    try {
      mCursor = BaseDatabaseActivity.getDatabase().rawQuery(DatabaseConnection.GET_PASSWORDS, null);

      if (!mCursor.moveToNext())
        return null;

      PasswordContainer password = getPassword();
      Log.d(getClass().getSimpleName(), String.format("%s: %s - %s - %s", password.getId(), password.getProgram(), password.getUsername(), password.getDefaultPassword()));

      while (mCursor.moveToNext()) {
        PasswordContainer nextPassword = getPassword();
        Log.d(getClass().getSimpleName(), String.format("%s: %s - %s - %s", nextPassword.getId(), nextPassword.getProgram(), nextPassword.getUsername(), nextPassword.getDefaultPassword()));

        if (nextPassword.equals(password)) {
          password.merge(nextPassword);
        } else {
          raiseCallbacks(password);
          password = nextPassword;
        }
      }

      raiseCallbacks(password); // add last read mTextViewAsPassword

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

  private PasswordContainer getPassword() {
    int id = mCursor.getInt(0);
    String username = mCursor.getString(1);
    String program = mCursor.getString(2);
    int position = mCursor.getInt(3);
    int historyId = mCursor.getInt(4);

    PasswordContainer password = new PasswordContainer(id, position, program, username);

    PasswordItem history = getHistory(historyId);
    password.addItem(history);

    return password;
  }

  private PasswordItem getHistory(int id) {
    try {
      String value = mCursor.getString(5);
      String dateString = mCursor.getString(6);
      Date changed = Utils.getDateFromString(dateString);
      return new PasswordItem(id, value, changed);
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
