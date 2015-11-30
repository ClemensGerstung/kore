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
      "CREATE TABLE passwords(id INTEGER PRIMARY KEY, username TEXT, program TEXT, position INT);";
  private static final String INSTALL_HISTORY =
      "CREATE TABLE history(id INTEGER PRIMARY KEY, password TEXT, changed DATE, passwordId INT, FOREIGN KEY(passwordId) REFERENCES passwords(id));";

  public static final String INSERT_NEW_PASSWORD = "INSERT INTO passwords(username, program, position) VALUES (@username, @program, @position);";

  public static final String INSERT_NEW_HISTORY = "INSERT INTO history(password, changed, passwordId) VALUES (@password, DATE('now'), @passwordId);";

  public static final String GET_PASSWORDS = "SELECT p.id, p.username, p.program, p.position, h.id, h.password, h.changed FROM passwords p JOIN history h ON p.id = h.passwordId;";

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
