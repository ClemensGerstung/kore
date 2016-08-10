package com.typingsolutions.passwordmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import com.typingsolutions.passwordmanager.BaseDatabaseConnection;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.dao.PasswordItem;
import core.Utils;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.SortedMap;
import java.util.TreeMap;

public class BackupDatabaseConnection extends BaseDatabaseConnection {
  public static final String NAME = "backup.db";
  private static final int VERSION = 0x1;

  public BackupDatabaseConnection(Context context, String password, int pim) {
    super(context, NAME, VERSION, password, pim);
  }

  public void load(PasswordContainer[] items) {
    SQLiteDatabase database = getDatabase();
    if(database == null) return;

    try {
      database.beginTransaction();
      ContentValues container = new ContentValues();
      ContentValues passwordValues = new ContentValues();

      for (int i = 0; i < items.length; i++) {
        PasswordContainer password = items[i];
        container.put(DatabaseConnection.PROGRAM, password.getProgram());
        container.put(DatabaseConnection.USERNAME, password.getUsername());
        container.put(DatabaseConnection.POSITION, password.getPosition());

        database.insert(DatabaseConnection.PASSWORDS_TABLE_NAME, "", container);

        for (int j = 0; j < password.getPasswordItems().size(); j++) {
          PasswordItem item = password.getPasswordItems().valueAt(j);
          passwordValues.put(DatabaseConnection.PASSWORD, item.getPassword());
          passwordValues.put(DatabaseConnection.CHANGED, Utils.getStringFromDate(item.getDate()));
          passwordValues.put(DatabaseConnection.PASSWORD, item.getPassword());

          database.insert(DatabaseConnection.HISTORY_TABLE_NAME, "", passwordValues);
        }
      }

      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  @Override
  @NonNull
  protected String[] getCreationSqlQueries() {
    return new String[]{DatabaseConnection.CREATE_PASSWORDS_TABLE, DatabaseConnection.CREATE_HISTORY_TABLE};
  }

  @Override
  @NonNull
  protected SortedMap<Integer, String[]> getUpdateSqlQueries() {
    return new TreeMap<>();
  }
}
