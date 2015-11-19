package core;

import android.content.Context;
import android.database.Cursor;
import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

public class DatabaseProvider extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";

    public static final int VERSION = 0x02;

    @Deprecated
    private static final String INSTALL_USERS =
            "CREATE TABLE users(id INTEGER PRIMARY KEY, name TEXT UNIQUE, passwordHash TEXT, salt TEXT, passwords TEXT);";
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

    private DatabaseProvider(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        lastCursor = null;
    }

    public static DatabaseProvider getConnection(Context context, String password) {
        if (INSTANCE == null) {
            SQLiteDatabase.loadLibs(context);
            INSTANCE = new DatabaseProvider(context);
            INSTANCE.password = password;
        }

        return INSTANCE;
    }

    public Cursor query(String query, String... args) {
        SQLiteDatabase db = getReadableDatabase();

        lastCursor = db.rawQuery(query, args);

        return lastCursor;
    }

    public long insert(String query, String... args) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        long id = -1;
        try {


            SQLiteStatement compiled = db.compileStatement(query);
            compiled.bindAllArgsAsStrings(args);

            id = compiled.executeInsert();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return id;
    }

    public int update(String query, String... args) {
        SQLiteDatabase db = getWritableDatabase();


        db.beginTransaction();
        long affectedRows = -1;
        try {
            SQLiteStatement compiled = db.compileStatement(query);
            compiled.bindAllArgsAsStrings(args);

            affectedRows = compiled.executeUpdateDelete();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return (int) affectedRows;
    }

    public int remove(String query, String... args) {
        return update(query, args);
    }

    public DatabaseProvider rawQuery(String query) {
        SQLiteDatabase db = getReadableDatabase();
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
        //db.execSQL(INSTALL_USERS);
        db.execSQL(INSTALL_PASSWORDS);
        db.execSQL(INSTALL_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("Nope...no upgrade!");
    }
}
