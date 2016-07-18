package com.typingsolutions.passwordmanager.dao;

import android.content.ContentValues;
import android.util.SparseArray;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.IContainer;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import net.sqlcipher.database.SQLiteDatabase;

public class PasswordContainer implements IContainer {
  public static int HighestPosition = 0;

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

  private PasswordContainer(PasswordContainer other)  {
    mId = other.mId;
    mProgram = other.mProgram;
    mUsername = other.mUsername;
    mPosition = other.mPosition;
    mPasswordItems = other.mPasswordItems.clone();
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
      PasswordItem current = other.mPasswordItems.valueAt(i);
      mPasswordItems.append(current.getId(), current);
    }
  }

  public void addItem(String password) {
    PasswordItem item = PasswordItem.create(password, mId);
    if (item == null) return;
    mPasswordItems.append(item.getId(), item);
  }

  public void addItem(PasswordItem item) {
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

  public void update(int position, String program, String username) {
    SQLiteDatabase database = BaseDatabaseActivity.getDatabase();
    if (database == null) return;

    String[] where = {"" + mId};
    ContentValues values = new ContentValues();
    if (position >= 0 && position != mPosition) {
      mPosition = position;
      values.put(DatabaseConnection.POSITION, position);
    }
    if (program != null && !program.equals(mProgram)) {
      mProgram = program;
      values.put(DatabaseConnection.PROGRAM, program);
    }
    if (username != null && !username.equals(mUsername)) {
      mUsername = username;
      values.put(DatabaseConnection.USERNAME, username);
    }

    try {
      database.beginTransaction();

      database.update(DatabaseConnection.PASSWORDS_TABLE_NAME, values, "id", where);

      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
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

  public int getId() {
    return mId;
  }

  public String getProgram() {
    return mProgram;
  }

  public void setProgram(String program) {
    mProgram = program;
    update(Integer.MIN_VALUE, program, null);
  }

  public String getUsername() {
    return mUsername;
  }

  public void setUsername(String username) {
    this.mUsername = username;
    update(Integer.MIN_VALUE, null, username);
  }

  public int getPosition() {
    return mPosition;
  }

  public void setPosition(int position) {
    this.mPosition = position;
    update(position, null, null);
  }

  public SparseArray<PasswordItem> getPasswordItems() {
    return mPasswordItems;
  }

  public String getDefaultPassword() {
    return mPasswordItems.valueAt(mPasswordItems.size() - 1).getPassword();
  }

  @SuppressWarnings("CloneDoesntCallSuperClone")
  @Override
  public IContainer clone() {
    return new PasswordContainer(this);
  }
}
