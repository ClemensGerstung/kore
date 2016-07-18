package com.typingsolutions.passwordmanager.database;

import android.content.Context;
import com.typingsolutions.passwordmanager.BaseDatabaseConnection;

import java.util.HashMap;

public class DatabaseConnection extends BaseDatabaseConnection {
  public static final String DATABASE_NAME = "database.db";

  static final int VERSION = 0x01;

  public static final String PASSWORDS_TABLE_NAME = "passwords";
  public static final String HISTORY_TABLE_NAME = "history";
  public static final String TABLE_ID = "id";
  public static final String USERNAME = "username";
  public static final String PROGRAM = "program";
  public static final String POSITION = "position";
  public static final String PASSWORD = "password";
  public static final String CHANGED = "changed";
  public static final String PASSWORD_ID = "passwordId";

  static final String CREATE_PASSWORDS_TABLE = "CREATE TABLE " + PASSWORDS_TABLE_NAME + " (" +
      TABLE_ID + " INTEGER PRIMARY KEY, " +
      USERNAME + " TEXT, " +
      PROGRAM + " TEXT, " +
      POSITION + " INT);";

  static final String CREATE_HISTORY_TABLE = "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
      TABLE_ID + " INTEGER PRIMARY KEY, " +
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
      " ORDER BY p." + POSITION + ", p." + TABLE_ID + ", h." + CHANGED;

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
