package core.async;

import android.os.AsyncTask;
import android.support.v4.content.Loader;
import android.util.Log;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

/**
 *
 */
public class SqlDeleteTask extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = "SqlDeleteTask";

  private SQLiteDatabase mDb;
  private String mTable;
  private String mSelection;
  private String[] mSelectionArgs;
  private SqlTaskCallback callback = null;

  /**
   * Constructor if we don't need to notify a Loader of the change.
   *
   * @param db
   * @param table
   * @param selection
   * @param selectionArgs
   */
  public SqlDeleteTask(SQLiteDatabase db, String table,
                       String selection, String[] selectionArgs) {
    this(db,table,selection,selectionArgs,null);
  }

  /**
   * Constructor to add a Loader that should be notified, if the change has
   * been successful.
   *
   * @param db
   * @param table
   * @param selection
   * @param selectionArgs
   */
  public SqlDeleteTask(SQLiteDatabase db, String table,
                       String selection, String[] selectionArgs, SqlTaskCallback callback) {
    mDb = db;
    mTable = table;
    mSelection = selection;
    mSelectionArgs = selectionArgs;
    this.callback = callback;
  }

  @Override
  protected Integer doInBackground(Void... params) {
    try {
      return mDb.delete(mTable, mSelection, mSelectionArgs);
    } catch (Exception e) {
      Log.e(TAG, "Unable to delete data.", e);
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
      Log.i(TAG, "Successfully deleted "+result+" rows from table " + mTable);
      if (callback != null) {
        callback.executed(result);
      }
    } else {
      Log.i(TAG,"No rows deleted.");
    }

  }
}
