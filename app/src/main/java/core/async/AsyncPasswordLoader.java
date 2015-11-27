package core.async;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import core.DatabaseProvider;

public class AsyncPasswordLoader extends AsyncTask<String, Void, Void> {
  private Context context;

  public AsyncPasswordLoader(Context context) {
    super();
    this.context = context;
  }

  @Override
  protected Void doInBackground(String... params) {
    DatabaseProvider provider = DatabaseProvider.getConnection(context);
    try {
      Cursor cursor = provider.query(DatabaseProvider.GET_PASSWORDS);

      while (cursor.moveToNext()) {

      }

    } catch (Exception e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }

    return null;
  }
}