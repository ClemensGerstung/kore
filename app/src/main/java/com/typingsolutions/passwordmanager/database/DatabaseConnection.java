package com.typingsolutions.passwordmanager.database;

import android.content.Context;
import com.typingsolutions.passwordmanager.BaseDatabaseConnection;

import java.util.HashMap;

public class DatabaseConnection extends BaseDatabaseConnection {
  public static final String DATABASE_NAME = "database.db";

  static final int VERSION = 0x01;

  static final String PASSWORDS_TABLE_NAME = "passwords";
  static final String HISTORY_TABLE_NAME = "history";
  static final String TABLE_ID = "id";
  static final String USERNAME = "username";
  static final String PROGRAM = "program";
  static final String POSITION = "position";
  static final String PASSWORD = "password";
  static final String CHANGED = "changed";
  static final String PASSWORD_ID = "passwordId";

  static final String CREATE_PASSWORDS_TABLE = "CREATE TABLE " + PASSWORDS_TABLE_NAME + " (" +
      TABLE_ID + " INTEGER PRIMARY KEY, " +
      USERNAME + " TEXT, " +
      PROGRAM + " TEXT, " +
      POSITION + " INT);";

  static final String CREATE_HISTORY_TABLE = "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
      TABLE_ID + " INT PRIMARY KEY, " +
      PASSWORD + " TEXT, " +
      CHANGED + " DATE, " +
      PASSWORD_ID + " INT, " +
      "FOREIGN KEY(" + PASSWORD_ID + ") REFERENCES " + PASSWORDS_TABLE_NAME + "(" + TABLE_ID + "));";

  public static final String GET_PASSWORDS = "SELECT p." + TABLE_ID +
      ", p." + USERNAME +
      ", p." + PROGRAM +
      ", p." + POSITION +
      ", h." + TABLE_ID +
      ", h." + PASSWORD +
      ", h." + CHANGED +
      " FROM " + PASSWORDS_TABLE_NAME + " p" +
      " JOIN " + HISTORY_TABLE_NAME + " h ON p." + TABLE_ID + " = h." + PASSWORD_ID +
      " ORDER BY p." + POSITION + ";";

  public DatabaseConnection(Context context, int pim) {
    this(context, null, pim);
  }

  public DatabaseConnection(Context context, String password, int pim) {
    super(context, DATABASE_NAME, VERSION, password, pim);
  }

  @Override
  protected String[] getCreationSqlQueries() {
    return new String[]{CREATE_PASSWORDS_TABLE, CREATE_HISTORY_TABLE};
  }

  @Override
  protected HashMap<Integer, String[]> getUpdateSqlQueries() {
    return new HashMap<>();
  }
}
