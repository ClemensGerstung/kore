package com.typingsolutions.passwordmanager;


import core.Dictionary;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.Comparator;

public abstract class BaseDatabaseConnection<TActivity extends BaseActivity> extends SQLiteOpenHelper {
  protected TActivity mActivity;

  protected BaseDatabaseConnection(TActivity activity, String name, int version) {
    super(activity, name, null, version);
    this.mActivity = activity;
  }

  protected abstract String[] getCreationSqlQueries();

  protected abstract Dictionary<Integer, String[]> getUpdateSqlQueries();

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    for (String query : this.getCreationSqlQueries()) {
      sqLiteDatabase.execSQL(query);
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    Dictionary<Integer, String[]> tmp = getUpdateSqlQueries();
    tmp.sortByKey(new Comparator<Integer>() {
      @Override
      public int compare(Integer lhs, Integer rhs) {
        return lhs.compareTo(rhs);
      }
    });

    for (Dictionary.Element<Integer, String[]> element : tmp) {
      if (element.getKey() <= oldVersion) return;

      for (String query : element.getValue()) {
        sqLiteDatabase.execSQL(query);
      }
    }
  }
}
