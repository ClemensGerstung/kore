package core;

import android.content.Context;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

public class DatabaseProvider extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "password.manager.database.db";

  public static final int VERSION = 0x02;

  private static final String INSTALL_PASSWORDS =
      "CREATE TABLE passwords(id INTEGER PRIMARY KEY, data TEXT);";
  private static final String INSTALL_HISTORY =
      "CREATE TABLE history(id INTEGER PRIMARY KEY, data TEXT);";

  @Deprecated
  public static final String DOES_USER_EXISTS = "SELECT COUNT(*) = 1 FROM users WHERE name=?;";

  @Deprecated
  public static final String GET_USER_ID = "SELECT id FROM users WHERE name=?;";

  @Deprecated
  public static final String CREATE_USER = "INSERT INTO users(name, passwordHash, salt) VALUES (?,?,?);";

  @Deprecated
  public static final String GET_SALT_AND_PASSWORDHASH_BY_ID = "SELECT salt, passwordHash FROM users WHERE id=?;";

  public static final String GET_PASSWORDIDS_FROM_USER = "SELECT passwords FROM users WHERE id = ?;";

  public static final String GET_PASSWORD_BY_ID = "SELECT id, data FROM passwords WHERE id = ?;";

  public static final String GET_HISTORYITEM_BY_ID = "SELECT data FROM history WHERE id = ?;";

  public static final String INSERT_NEW_PASSWORD = "INSERT INTO passwords(data) VALUES(?);";

  public static final String INSERT_NEW_HISTORY_ITEM = "INSERT INTO history(data) VALUES(?);";

  public static final String UPDATE_PASSWORDIDS_FOR_USER = "UPDATE users SET passwords=? WHERE id=?;";

  public static final String UPDATE_PASSWORD_BY_ID = "UPDATE passwords SET data = ? WHERE id = ?;";

  public static final String DELETE_PASSWORDHISTORY_BY_ID = "DELETE FROM history WHERE id = ?;";
  public static final String DELETE_PASSWORD_BY_ID = "DELETE FROM passwords WHERE id = ?;";

  private static DatabaseProvider INSTANCE;

  private Cursor lastCursor;
  private String password;
  private Context context;
  private OnSetupListener onSetupListener;

  public void setOnSetupListener(OnSetupListener onSetupListener) {
    this.onSetupListener = onSetupListener;
  }

  private DatabaseProvider(Context context, String password) {
    super(context, DATABASE_NAME, null, VERSION);
    this.lastCursor = null;
    this.context = context;
    this.password = password;
  }

  public static DatabaseProvider getConnection(Context context, String password) {
    if (INSTANCE == null) {
      SQLiteDatabase.loadLibs(context);
      INSTANCE = new DatabaseProvider(context, password);
    }

    return INSTANCE;
  }

  public static DatabaseProvider getConnection(Context context) {
    return getConnection(context, null);
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
    String password = onSetupListener.onSetup();

    SQLiteDatabase sqLiteDatabase = getWritableDatabase(password);
    boolean isOpen = sqLiteDatabase.isOpen();
    sqLiteDatabase.close();
    return isOpen;
  }

  public boolean tryOpen(String password) {
    try {
      SQLiteDatabase database = getReadableDatabase(password);
      boolean result = database != null;
      if(result)
        this.password = password;
      return result;
    } catch (Exception e) {
      return false;
    }
  }

  public Cursor query(String query, String... args) {
    SQLiteDatabase db = getReadableDatabase(password);

    lastCursor = db.rawQuery(query, args);

    return lastCursor;
  }

  public DatabaseProvider rawQuery(String query) {
    SQLiteDatabase db = getWritableDatabase(password);
    db.execSQL(query);
    return this;
  }

  Cursor getLastCursor() {
    return lastCursor;
  }

  public static void dismiss() {
    INSTANCE.close();
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
}
