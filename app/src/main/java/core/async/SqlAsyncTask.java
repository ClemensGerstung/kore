package core.async;

import android.content.ContentValues;
import android.os.AsyncTask;
import com.typingsolutions.passwordmanager.BaseActivity;
import net.sqlcipher.database.SQLiteDatabase;

public abstract class SqlAsyncTask<TResult> extends AsyncTask<Void, Void, TResult> {
  protected SQLiteDatabase mDatabase;
  protected String mTable;
  protected ContentValues mValues;
  protected String mSelection;
  protected String[] mSelectionArgs;
  protected String mNullColumnHack;
  protected ISqlTaskCallback<TResult> mCallback;

  /**
   * Constructor to insert data if we don't need to notify a GDriveBackupFileMetaLoader of the change.
   *
   * @param db
   * @param table
   * @param nullColumnHack
   * @param values
   */
  public SqlAsyncTask(SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
    this(db, table, nullColumnHack, values, null);
  }

  /**
   * Constructor to insert data and add a GDriveBackupFileMetaLoader that should be notified, if the change has been successful.
   *
   * @param db
   * @param table
   * @param nullColumnHack
   * @param values
   */
  public SqlAsyncTask(SQLiteDatabase db, String table, String nullColumnHack, ContentValues values, ISqlTaskCallback<TResult> callback) {
    this(db, table, values, null, null, callback);
    this.mNullColumnHack = nullColumnHack;
  }

  /**
   * Constructor to delete data if we don't need to notify a GDriveBackupFileMetaLoader of the change.
   *
   * @param db
   * @param table
   * @param selection
   * @param selectionArgs
   */
  public SqlAsyncTask(SQLiteDatabase db, String table, String selection, String[] selectionArgs) {
    this(db, table, selection, selectionArgs, null);
  }

  /**
   * Constructor to delete data and add a GDriveBackupFileMetaLoader that should be notified, if the change has been successful.
   *
   * @param db
   * @param table
   * @param selection
   * @param selectionArgs
   */
  public SqlAsyncTask(SQLiteDatabase db, String table, String selection, String[] selectionArgs, ISqlTaskCallback<TResult> callback) {
    this(db, table, null, selection, selectionArgs, callback);
  }

  /**
   * Constructor to update data if we don't need to notify a GDriveBackupFileMetaLoader of the change.
   *
   * @param db
   * @param table
   * @param selection
   * @param selectionArgs
   * @param values
   */
  public SqlAsyncTask(SQLiteDatabase db, String table, ContentValues values, String selection, String[] selectionArgs) {
    this(db, table, values, selection, selectionArgs, null);
  }

  /**
   * Constructor to update data and add a GDriveBackupFileMetaLoader that should be notified, if the change has been successful.
   *
   * @param db
   * @param table
   * @param selection
   * @param selectionArgs
   * @param values
   */
  public SqlAsyncTask(SQLiteDatabase db, String table, ContentValues values, String selection, String[] selectionArgs, ISqlTaskCallback<TResult> callback) {
    this.mDatabase = db;
    this.mTable = table;
    this.mValues = values;
    this.mSelection = selection;
    this.mSelectionArgs = selectionArgs;
    this.mCallback = callback;
  }

  protected int doUpdateQuery() {
    try {
      return this.mDatabase.update(mTable, mValues, mSelection, mSelectionArgs);
    } catch (Exception e) {
      if (this.mCallback != null) {
        this.mCallback.failed(e.getMessage());
      }

      return -1;
    }
  }

  protected long doInsertQuery() {
    try {
      return this.mDatabase.insertOrThrow(mTable, mNullColumnHack, mValues);
    } catch (Exception e) {
      if (this.mCallback != null) {
        this.mCallback.failed(e.getMessage());
      }

      return -1;
    }
  }

  protected long doDeleteTask() {
    try {
      return this.mDatabase.delete(mTable, mSelection, mSelectionArgs);
    } catch (Exception e) {
      if (this.mCallback != null) {
        this.mCallback.failed(e.getMessage());
      }

      return -1;
    }
  }

  @Override
  protected void onPostExecute(TResult result) {
    super.onPostExecute(result);

    if (result == null || !this.querySuccessCondition(result)) {
      if (this.mCallback == null) return;

      this.mCallback.failed("querySuccessCondition returned false");
    } else {
      if (this.mCallback == null) return;

      this.mCallback.executed(result);
    }
  }

  protected abstract boolean querySuccessCondition(TResult result);
}
