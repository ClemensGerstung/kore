package core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

class DatabaseProvider extends SQLiteOpenHelper {

    public static final int VERSION = 1;

    private static final String INSTALL = "CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY, name TEXT, passwordHash TEXT, salt TEXT);" +
            "CREATE TABLE IF NOT EXISTS passwords(id INTEGER PRIMARY KEY, username TEXT, program TEXT, position INTEGER, userId INTEGER, FOREIGN KEY(userId) REFERENCES users(id));" +
            "CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY, value TEXT, dateChanged DATE, passwordId INTEGER, FOREIGN KEY(passwordId) REFERENCES passwords(id));";

    public static final String DOES_USER_EXISTS = "SELECT COUNT(*) = 1 FROM users WHERE name=?;";

    public static final String GET_USER_ID = "SELECT id FROM users WHERE name=? AND passwordHash=?";

    public static final String CREATE_USER = "INSERT INTO users(name, passwordHash, salt) VALUES (?,?,?);";

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
        db.execSQL(INSTALL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("Nope...no upgrade!");
    }
}
