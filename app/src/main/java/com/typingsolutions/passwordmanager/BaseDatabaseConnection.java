package com.typingsolutions.passwordmanager;


import core.Dictionary;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.Comparator;

public abstract class BaseDatabaseConnection<TActivity extends BaseActivity> extends SQLiteOpenHelper {
  protected TActivity mActivity;
  protected int mVersion;
  protected String mName;

  /**
   * Creates a DatabaseConnection to a SQLiteDatabase
   *
   * @param activity for application context etc.
   * @param name of the database
   * @param version of the database
   * @param hook to use PIM {@see BaseDatabaseConnection#getDatabaseHook(pim)}
   */
  protected BaseDatabaseConnection(TActivity activity, String name, int version, SQLiteDatabaseHook hook) {
    super(activity, name, null, version, hook);
    this.mActivity = activity;
    this.mVersion = version;
    this.mName = name;
  }

  protected abstract String[] getCreationSqlQueries();

  protected abstract Dictionary<Integer, String[]> getUpdateSqlQueries();

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    for (String query : this.getCreationSqlQueries()) {
      sqLiteDatabase.execSQL(query);
    }

    this.onUpgrade(sqLiteDatabase, 0, this.mVersion);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    Dictionary<Integer, String[]> tmp = this.getUpdateSqlQueries();
    tmp.sortByKey(new Comparator<Integer>() {
      @Override
      public int compare(Integer lhs, Integer rhs) {
        return lhs.compareTo(rhs);
      }
    });

    for (Dictionary.Element<Integer, String[]> element : tmp) {
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
      public void preKey(SQLiteDatabase sqLiteDatabase) { }

      @Override
      public void postKey(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.rawExecSQL(String.format("PRAGMA kdf_iter = %s", iterations));
      }
    };
  }
}
