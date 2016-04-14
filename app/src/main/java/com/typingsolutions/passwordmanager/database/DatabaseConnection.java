package com.typingsolutions.passwordmanager.database;

import android.content.Context;
import com.typingsolutions.passwordmanager.BaseDatabaseConnection;

import java.util.HashMap;

public class DatabaseConnection extends BaseDatabaseConnection {
  protected DatabaseConnection(Context context, String name, int version, int pim) {
    super(context, name, version, pim);
  }

  @Override
  protected String[] getCreationSqlQueries() {
    return new String[0];
  }

  @Override
  protected HashMap<Integer, String[]> getUpdateSqlQueries() {
    return new HashMap<>();
  }
}
