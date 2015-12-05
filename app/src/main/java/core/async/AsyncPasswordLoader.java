package core.async;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import core.DatabaseProvider;
import core.data.Password;
import core.data.PasswordProvider;

public class AsyncPasswordLoader extends AsyncTask<String, Void, Void> {
  private Context context;

  public AsyncPasswordLoader(Context context) {
    super();
    this.context = context;
  }

  /**
   * @param params @null
   * @return @null
   */
  @Override
  protected Void doInBackground(String... params) {
    DatabaseProvider provider = DatabaseProvider.getConnection(context);
    try {
      Cursor cursor = provider.query(DatabaseProvider.GET_PASSWORDS);

      if (!cursor.moveToNext()) return null;
      Password password = Password.getFromCursor(cursor);

      while (cursor.moveToNext()) {
        Password nextPassword = Password.getFromCursor(cursor);
        if (nextPassword.equals(password)) {
          password.merge(nextPassword);
        } else {
          PasswordProvider.getInstance(context).addPassword(password);
          if (cursor.moveToNext())
            password = Password.getFromCursor(cursor);
        }
      }

      PasswordProvider.getInstance(context).addPassword(password);
      cursor.close();

    } catch (Exception e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }

    return null;
  }
}