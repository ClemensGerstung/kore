package com.typingsolutions.kore.common;

import android.content.Context;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DatabaseConnection extends SQLiteOpenHelper {
  private static final String NAME = "database.db";
  private static final int VERSION = 1;


  private String mPassword;
  private int mPim;

  DatabaseConnection(Context context, String password, int pim) {
    super(context, NAME, null, VERSION, getDatabaseHook(pim));
    mPassword = password;
    mPim = pim;

  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {

  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

  }

  @Override
  public synchronized void close() {
    super.close();

    mPim = 0;

    // TODO: clear string pool
    mPassword = null;
  }

  public synchronized SQLiteDatabase getWritableDatabase() {
    if (mPassword == null || mPassword.length() == 0) return null;

    return super.getWritableDatabase(mPassword);
  }

  private static SQLiteDatabaseHook getDatabaseHook(int pim) {
    final String iterations = Integer.toString(pim);
    Log.d(DatabaseConnection.class.getSimpleName(), iterations);

    return new SQLiteDatabaseHook() {
      @Override
      public void preKey(SQLiteDatabase sqLiteDatabase) {
      }

      @Override
      public void postKey(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.rawExecSQL(String.format("PRAGMA kdf_iter = %s", iterations));
      }
    };
  }
}
