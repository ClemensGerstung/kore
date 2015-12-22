package core;

import android.content.Context;
import android.util.Log;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import java.io.File;

public class DatabaseProvider extends SQLiteOpenHelper {

  public static final String DATABASE_NAME = "password.manager.database.db";

  public static final int VERSION = 0x02;

  private static final String INSTALL_PASSWORDS =
      "CREATE TABLE passwords(id INTEGER PRIMARY KEY, username TEXT, program TEXT, position INT);";
  private static final String INSTALL_HISTORY =
      "CREATE TABLE history(id INTEGER PRIMARY KEY, password TEXT, changed DATE, passwordId INT, FOREIGN KEY(passwordId) REFERENCES passwords(id));";

  public static final String INSERT_NEW_PASSWORD = "INSERT INTO passwords(username, program, position) VALUES (?, ?, ?);";

  public static final String INSERT_NEW_HISTORY = "INSERT INTO history(password, changed, passwordId) VALUES (?, DATE('now'), ?);";

  public static final String GET_PASSWORDS = "SELECT p.id, p.username, p.program, p.position, h.id, h.password, h.changed FROM passwords p JOIN history h ON p.id = h.passwordId;";

  public static final String UPDATE_PASSWORD_BY_ID = "UPDATE passwords SET program = ?, username = ? WHERE id = ?;";

  public static final String REMOVE_PASSWORD_BY_ID = "DELETE FROM passwords WHERE id = ?;";

  public static final String REMOVE_HISTORY_BY_ID = "DELETE FROM history WHERE id = ?;";

  private static DatabaseProvider INSTANCE;

  private String password;
  private Context context;
  private OnSetupListener onSetupListener;

  public void setOnSetupListener(OnSetupListener onSetupListener) {
    this.onSetupListener = onSetupListener;
  }

  private DatabaseProvider(Context context, String password) {
    super(context, DATABASE_NAME, null, VERSION);
    this.context = context;
    this.password = password;
  }

  public static DatabaseProvider getConnection(Context context) {
    if (INSTANCE == null) {
      SQLiteDatabase.loadLibs(context);
      INSTANCE = new DatabaseProvider(context, null);
    }

    return INSTANCE;
  }

  public boolean needSetup() {
    try {
      File database = context.getDatabasePath(DATABASE_NAME);
      return !database.exists();
    } catch (Exception e) {
      return true;
    }
  }

  public boolean setup() {
    if (onSetupListener == null) return false;
    password = onSetupListener.onSetup();

    SQLiteDatabase sqLiteDatabase = getWritableDatabase(password);
    boolean isOpen = sqLiteDatabase.isOpen();
    sqLiteDatabase.close();
    return isOpen;
  }

  public void tryOpen(String password, final OnOpenListener onOpenListener) {

    final String userProvidedPassword = password;
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          SQLiteDatabase database = getReadableDatabase(userProvidedPassword);
          boolean result = database != null;
          if (result) {
            DatabaseProvider.this.password = userProvidedPassword;
            onOpenListener.open();
          } else {
            onOpenListener.refused();
          }
        } catch (Exception e) {
          onOpenListener.refused();
        }
      }
    });
    thread.start();
  }

  public Cursor query(String query, String... args) {
    SQLiteDatabase db = getReadableDatabase(password);
    return db.rawQuery(query, args);
  }

  public long insert(String query, Object... args) {
    SQLiteDatabase db = getWritableDatabase(password);

    db.beginTransaction();
    long id = -1;
    try {
      SQLiteStatement compiled = db.compileStatement(query);
      bindParams(compiled, args);
      id = compiled.executeInsert();
      //compiled.clearBindings();
      compiled.close();

      db.setTransactionSuccessful();
    } catch (Exception e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    } finally {
      db.endTransaction();
    }

    return id;
  }

  public long update(String query, Object... args) {
    SQLiteDatabase db = getWritableDatabase(password);

    db.beginTransaction();
    long affectedRows = -1;
    try {
      SQLiteStatement compiled = db.compileStatement(query);
      bindParams(compiled, args);
      affectedRows = (long) compiled.executeUpdateDelete();
      //compiled.clearBindings();
      compiled.close();

      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }

    return affectedRows;
  }

  private void bindParams(SQLiteStatement compiled, Object[] args) {
    for (int i = 0; i < args.length; i++) {
      Object argument = args[i];
      if (argument == null) {
        compiled.bindNull(i + 1);
      } else if (argument instanceof Long) {
        compiled.bindLong(i + 1, (Long) argument);
      } else if (argument instanceof String) {
        compiled.bindString(i + 1, (String) argument);
      } else if (argument instanceof Integer) {
        compiled.bindLong(i + 1, Long.valueOf((Integer) argument));
      }
    }
  }

  public void changePassword(String password) {
    getWritableDatabase(this.password).changePassword(password);
    this.password = password;
  }

  public long remove(String query, Object... args) {
    return update(query, args);
  }

  public static void logout() {
    INSTANCE.password = null;
    INSTANCE.close();
  }

  public static void changePassword(String path, String newPassword, OnChangePasswordListener changePasswordListener) {
    changePassword(path, INSTANCE.password, newPassword, changePasswordListener);
  }

  public static void changePassword(final String path, final String oldPassword, final String newPassword, final OnChangePasswordListener changePasswordListener) {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        SQLiteDatabase database = null;
        try {
          database =
              SQLiteDatabase.openDatabase(path, oldPassword, null, SQLiteDatabase.CONFLICT_IGNORE);
          if (database != null) {
            changePasswordListener.open();
          } else {
            changePasswordListener.refused();
          }
        } catch (Exception e) {
          changePasswordListener.refused();
        }

        try {
          database.changePassword(newPassword);
          changePasswordListener.changed();
        } catch (Exception e) {
          changePasswordListener.failed();
        }
      }
    });
    thread.start();
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(INSTALL_PASSWORDS);
    db.execSQL(INSTALL_HISTORY);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    throw new UnsupportedOperationException("Nope...no upgrade!");
  }

  public interface OnSetupListener {
    String onSetup();
  }

  public interface OnOpenListener {
    void open();

    void refused();
  }

  public interface OnChangePasswordListener extends OnOpenListener {
    void changed();

    void failed();
  }
}
