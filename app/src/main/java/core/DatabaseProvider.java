package core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class DatabaseProvider extends SQLiteOpenHelper {

    public static final int VERSION = 0x02;

    private static final String INSTALL_USERS =
            "CREATE TABLE users(id INTEGER PRIMARY KEY, name TEXT UNIQUE, passwordHash TEXT, salt TEXT, passwords TEXT);";
    private static final String INSTALL_PASSWORDS =
            "CREATE TABLE passwords(id INTEGER PRIMARY KEY, data TEXT);";
    private static final String INSTALL_HISTORY =
            "CREATE TABLE history(id INTEGER PRIMARY KEY, data TEXT);";

    public static final String DOES_USER_EXISTS = "SELECT COUNT(*) = 1 FROM users WHERE name=?;";

    public static final String GET_USER_ID = "SELECT id FROM users WHERE name=?;";

    public static final String CREATE_USER = "INSERT INTO users(name, passwordHash, salt) VALUES (?,?,?);";

    public static final String GET_SALT_AND_PASSWORDHASH_BY_ID = "SELECT salt, passwordHash FROM users WHERE id=?;";

    public static final String GET_PASSWORDIDS_FROM_USER = "SELECT passwords FROM users WHERE id = ?;";

    public static final String GET_PASSWORD_BY_ID = "SELECT id, data, FROM passwords WHERE id = ?;";

    public static final String GET_HISTORYITEM_BY_ID = "SELECT id, data FROM history WHERE id = ?;";

    public static final String INSERT_NEW_PASSWORD = "INSERT INTO passwords(data) VALUES(?);";

    private static DatabaseProvider INSTANCE;

    private Cursor lastCursor;

    public DatabaseProvider(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        lastCursor = null;
    }

    public static DatabaseProvider getConnection(Context context) {
        if (INSTANCE == null) {
            try {
                INSTANCE = new DatabaseProvider(context, Utils.getHashedHostName(), null, VERSION);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
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
        int affectedRows = -1;
        try {
            SQLiteStatement compiled = db.compileStatement(query);
            compiled.bindAllArgsAsStrings(args);
            affectedRows = compiled.executeUpdateDelete();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return affectedRows;
    }

    public int remove(String query, String... args) {
        return update(query, args);
    }

    @Deprecated
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
        db.execSQL(INSTALL_USERS);
        db.execSQL(INSTALL_PASSWORDS);
        db.execSQL(INSTALL_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("Nope...no upgrade!");
    }
}
