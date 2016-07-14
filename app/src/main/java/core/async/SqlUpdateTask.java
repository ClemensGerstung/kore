package core.async;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;

/**
 *
 */
public class SqlUpdateTask extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = "SqlUpdateTask";

  private SQLiteDatabase mDb;
  private String mTable;
  private ContentValues mValues;
  private String mSelection;
  private String[] mSelectionArgs;
  private ISqlTaskCallback callback = null;

  /**
   * Constructor if we don't need to notify a Loader of the change.
   *
   * @param db            A writable db connection.
   * @param table
   * @param selection
   * @param selectionArgs
   * @param values
   */
  public SqlUpdateTask(SQLiteDatabase db, String table, ContentValues values, String selection, String[] selectionArgs) {
    this(db, table, values, selection, selectionArgs, null);
  }

  /**
   * Constructor to add a Loader that should be notified, if the change has been successful.
   *
   * @param db
   * @param table
   * @param selection
   * @param selectionArgs
   * @param values
   */
  public SqlUpdateTask(SQLiteDatabase db, String table, ContentValues values, String selection, String[] selectionArgs, ISqlTaskCallback callback) {
    mDb = db;
    mTable = table;
    mValues = values;
    mSelection = selection;
    mSelectionArgs = selectionArgs;
    this.callback = callback;
  }



  @Override
  protected Integer doInBackground(Void... params) {
    try {
      return mDb.update(mTable, mValues, mSelection, mSelectionArgs);
    } catch (Exception e) {
      Log.e(TAG, "Unable to update data.", e);

      if (callback != null) {
        callback.failed(e.getMessage());
      }

      return null;
    }
  }

  @Override
  protected void onPostExecute(Integer result) {
    super.onPostExecute(result);
    if (result != null && result > 0) {
      Log.i(TAG, "Successfully changed " + result + " rows in table " + mTable);
      if (callback != null) {
        callback.executed(result);
      }
    } else {
      Log.i(TAG, "No rows changed.");
    }
  }
}