package com.typingsolutions.passwordmanager;


import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.*;

public abstract class BaseDatabaseConnection extends SQLiteOpenHelper {
  protected int mVersion;
  protected String mName;

  private static final Comparator<Map.Entry<Integer, String[]>> ENTRY_COMPARATOR = new Comparator<Map.Entry<Integer, String[]>>() {
    @Override
    public int compare(Map.Entry<Integer, String[]> lhs, Map.Entry<Integer, String[]> rhs) {
      return lhs.getKey().compareTo(rhs.getKey());
    }
  };

  /**
   * Creates a DatabaseConnection to a SQLiteDatabase
   *
   * @param context for application context etc.
   * @param name    of the database
   * @param version of the database
   * @param hook    a database hook
   */
  protected BaseDatabaseConnection(Context context, String name, int version, SQLiteDatabaseHook hook) {
    super(context, name, null, version, hook);
    this.mVersion = version;
    this.mName = name;
  }

  protected BaseDatabaseConnection(Context context, String name, int version, int pim) {
    this(context, name, version, BaseDatabaseConnection.getDatabaseHook(pim));
  }


  protected abstract String[] getCreationSqlQueries();

  protected abstract HashMap<Integer, String[]> getUpdateSqlQueries();

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    for (String query : this.getCreationSqlQueries()) {
      sqLiteDatabase.execSQL(query);
    }

    this.onUpgrade(sqLiteDatabase, 0, this.mVersion);
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
