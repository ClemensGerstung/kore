package com.typingsolutions.passwordmanager.dao;

import android.content.ContentValues;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import core.Utils;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.Date;

public class PasswordItem {
  private int mId;
  private String mPassword;
  private Date mDate;

  public PasswordItem(int id, String password, Date date) {
    mId = id;
    mPassword = password;
    mDate = date;
  }

  public static PasswordItem create(String password, int parentId) {
    SQLiteDatabase database = BaseDatabaseActivity.getDatabase();
    if (database == null) return null;

    Date date = new Date();
    long id = -1;

    try {
      database.beginTransaction();
      ContentValues values = new ContentValues(3);
      values.put(DatabaseConnection.PASSWORD, password);
      values.put(DatabaseConnection.CHANGED, Utils.getStringFromDate(date));
      values.put(DatabaseConnection.PASSWORD_ID, parentId);
      id = database.insertOrThrow(DatabaseConnection.HISTORY_TABLE_NAME, "", values);
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }

    if (id == -1L) return null;

    return new PasswordItem((int) id, password, date);
  }

  public int getId() {
    return mId;
  }

  public String getPassword() {
    return mPassword;
  }

  public Date getDate() {
    return mDate;
  }

  public boolean delete() {
    SQLiteDatabase database = BaseDatabaseActivity.getDatabase();
    if (database == null) return false;
    String[] where = {"" + mId};

    boolean result = false;

    try {
      database.beginTransaction();
      database.delete(DatabaseConnection.HISTORY_TABLE_NAME, "id=?", where);
      database.setTransactionSuccessful();
      result = true;
    } finally {
      database.endTransaction();
    }

    mId = -1;
    mDate = null;
    mPassword = null;

    return result;
  }
}
