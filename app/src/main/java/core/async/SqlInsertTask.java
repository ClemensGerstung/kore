package core.async;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v4.content.Loader;
import android.util.Log;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

/**
 *
 */
public class SqlInsertTask extends AsyncTask<Void, Void, Long> {

  private static final String TAG = "SqlInsertTask";

  private SQLiteDatabase mDb;
  private String mTable;
  private String mNullColumnHack;
  private ContentValues mValues;
  private Loader<Cursor> mLoader = null;

  /**
   * Constructor if we don't need to notify a Loader of the change.
   *
   * @param db
   * @param table
   * @param nullColumnHack
   * @param values
   */
  public SqlInsertTask(SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
    this(db, table, nullColumnHack, values, null);
  }


  /**
   * Constructor to add a Loader that should be notified, if the change has been successful.
   *
   * @param db
   * @param table
   * @param nullColumnHack
   * @param values
   * @param loader
   */
  public SqlInsertTask(SQLiteDatabase db, String table, String nullColumnHack, ContentValues values, Loader<Cursor> loader) {
    this.mDb = db;
    this.mTable = table;
    this.mNullColumnHack = nullColumnHack;
    this.mValues = values;
    this.mLoader = loader;
  }

  @Override
  protected Long doInBackground(Void... params) {
    try {
//      try {
//        Thread.sleep(3000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
      return mDb.insertOrThrow(mTable, mNullColumnHack, mValues);
    } catch (Exception e) {
      Log.e(TAG, "Unable to insert data.", e);
      return null;
    }
  }

  @Override
  protected void onPostExecute(Long result) {
    super.onPostExecute(result);
    if (result != null && result > 0) {
      Log.i(TAG, "Successfully added row with id=" + result + " to table " + mTable);

      if (mLoader != null) {
        mLoader.onContentChanged();
      }
    }
  }
}
