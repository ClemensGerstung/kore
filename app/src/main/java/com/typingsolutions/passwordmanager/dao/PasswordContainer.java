package com.typingsolutions.passwordmanager.dao;

import android.content.ContentValues;
import android.util.SparseArray;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import net.sqlcipher.database.SQLiteDatabase;

public class PasswordContainer {
  public static int HighestPosition = 1;

  private int mId;
  private String mProgram;
  private String mUsername;
  private int mPosition;
  private SparseArray<PasswordItem> mPasswordItems;

  public PasswordContainer(int id, int position, String program, String username) {
    mId = id;
    mProgram = program;
    mUsername = username;
    mPosition = position;
    mPasswordItems = new SparseArray<>();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PasswordContainer that = (PasswordContainer) o;

    if (mProgram != null ? !mProgram.equals(that.mProgram) : that.mProgram != null) return false;
    return mUsername != null ? mUsername.equals(that.mUsername) : that.mUsername == null;
  }

  @Override
  public int hashCode() {
    int result = mProgram != null ? mProgram.hashCode() : 0;
    result = 31 * result + (mUsername != null ? mUsername.hashCode() : 0);
    return result;
  }

  public void merge(PasswordContainer other) {
    for (int i = 0; i < other.mPasswordItems.size(); i++) {
      int key = other.mPasswordItems.keyAt(i);
      PasswordItem current = other.mPasswordItems.get(key);
      mPasswordItems.append(key, current);
    }
  }

  public void addItem(String password) {
    PasswordItem item = PasswordItem.create(password, mId);
    if (item == null) return;
    mPasswordItems.append(item.getId(), item);
  }

  public static PasswordContainer create(String program, String username, String password) {
    SQLiteDatabase database = BaseDatabaseActivity.getDatabase();
    if (database == null) return null;

    long id = -1;
    HighestPosition++;
    int position = HighestPosition;

    try {
      database.beginTransaction();

      ContentValues values = new ContentValues(3);
      values.put(DatabaseConnection.PROGRAM, program);
      values.put(DatabaseConnection.USERNAME, username);
      values.put(DatabaseConnection.POSITION, position);

      id = database.insertOrThrow(DatabaseConnection.PASSWORDS_TABLE_NAME, "", values);

      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }

    if (id == -1) return null;

    PasswordContainer container = new PasswordContainer((int) id, position, program, username);
    container.addItem(password);
    return container;
  }

  public boolean delete() {
    SQLiteDatabase database = BaseDatabaseActivity.getDatabase();
    if (database == null) return false;
    String[] where = {"" + mId};
    boolean result = true;

    try {
      for (int i = 0; i < mPasswordItems.size(); i++) {
        result &= mPasswordItems.valueAt(i).delete();
      }

      database.beginTransaction();
      database.delete(DatabaseConnection.PASSWORDS_TABLE_NAME, "id", where);
      database.setTransactionSuccessful();
    } catch (Exception e) {
      result = false;
    } finally {
      database.endTransaction();
    }

    mId = -1;
    mPasswordItems.clear();
    mPosition = -1;
    mProgram = null;
    mUsername = null;

    return result;
  }
}
