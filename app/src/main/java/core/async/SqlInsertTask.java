package core.async;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;

/**
 *
 */
public class SqlInsertTask extends AsyncTask<Void, Void, Long> {

  private static final String TAG = "SqlInsertTask";

  private SQLiteDatabase database;
  private String table;
  private String nullColumnHack;
  private ContentValues values;
  private SqlTaskCallback<Long> callback = null;

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
   */
  public SqlInsertTask(SQLiteDatabase db, String table, String nullColumnHack, ContentValues values, SqlTaskCallback<Long> callback) {
    this.database = db;
    this.table = table;
    this.nullColumnHack = nullColumnHack;
    this.values = values;
    this.callback = callback;
  }

  @Override
  protected Long doInBackground(Void... params) {
    try {
//      try {
//        Thread.sleep(3000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
      return database.insertOrThrow(table, nullColumnHack, values);
    } catch (Exception e) {
      Log.e(TAG, "Unable to insert data.", e);
      if(callback != null)
        callback.failed(e.getMessage());

      return null;
    }
  }

  @Override
  protected void onPostExecute(Long result) {
    super.onPostExecute(result);
    if (result == null || result <= 0)
      return;

    Log.i(TAG, "Successfully added row with id=" + result + " to table " + table);

    if (callback != null)
      callback.executed(result);
  }
}
