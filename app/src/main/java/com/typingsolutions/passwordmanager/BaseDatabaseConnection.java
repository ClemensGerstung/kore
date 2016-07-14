package com.typingsolutions.passwordmanager;


import android.content.Context;
import android.support.annotation.Nullable;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.*;

public abstract class BaseDatabaseConnection extends SQLiteOpenHelper
    implements Cloneable {
  protected int mVersion;
  protected int mPim;
  private String mPassword;
  protected String mName;
  private Context mContext;

  private static final Comparator<Map.Entry<Integer, String[]>> ENTRY_COMPARATOR = new Comparator<Map.Entry<Integer, String[]>>() {
    @Override
    public int compare(Map.Entry<Integer, String[]> lhs, Map.Entry<Integer, String[]> rhs) {
      return lhs.getKey().compareTo(rhs.getKey());
    }
  };

  protected BaseDatabaseConnection(Context context, String name, int version, String password, int pim) {
    super(context, name, null, pim, BaseDatabaseConnection.getDatabaseHook(pim));
    SQLiteDatabase.loadLibs(context);
    this.mVersion = version;
    this.mName = name;
    this.mContext = context;
    this.mPim = pim;
    this.mPassword = password;
  }

  protected abstract String[] getCreationSqlQueries();

  protected abstract HashMap<Integer, String[]> getUpdateSqlQueries();

  protected Context getContext() {
    return mContext;
  }

  protected String getPassword() {
    return mPassword;
  }

  protected int getPim() {
    return mPim;
  }

  public boolean hasPassword() {
    return mPassword != null;
  }

  @Nullable
  public SQLiteDatabase getDatabase() {
    if (mPassword == null || mPassword.length() == 0) return null;

    return getWritableDatabase(mPassword);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    for (String query : this.getCreationSqlQueries()) {
      sqLiteDatabase.execSQL(query);
    }

    this.onUpgrade(sqLiteDatabase, 0, mVersion);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    List<Map.Entry<Integer, String[]>> tmp =
        new ArrayList<>(getUpdateSqlQueries().entrySet());

    Collections.sort(tmp, ENTRY_COMPARATOR);

    for (Map.Entry<Integer, String[]> element : tmp) {
      if (element.getKey() <= oldVersion) continue;

      for (String query : element.getValue()) {
        sqLiteDatabase.execSQL(query);
      }
    }
  }

  @Override
  protected Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }

  public static SQLiteDatabaseHook getDatabaseHook(int pim) {
//    if(pim <= 485)
//      throw new IllegalArgumentException("pim must be greater than 485");

    final String iterations = Integer.toString(15000 + (pim * 1000));

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
