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
  private net.sqlcipher.Cursor cursor;

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
      cursor = provider.query(DatabaseProvider.GET_PASSWORDS);

      if (!cursor.moveToNext())
        return null;
      Password password = Password.getFromCursor(cursor);
      Log.d(getClass().getSimpleName(), String.format("%s: %s - %s - %s", password.getId(), password.getProgram(), password.getUsername(), password.getFirstItem()));

      while (cursor.moveToNext()) {
        Password nextPassword = Password.getFromCursor(cursor);
        Log.d(getClass().getSimpleName(), String.format("%s: %s - %s - %s", nextPassword.getId(), nextPassword.getProgram(), nextPassword.getUsername(), nextPassword.getFirstItem()));
        if (nextPassword.equals(password)) {
          password.merge(nextPassword);
        } else {
          PasswordProvider.getInstance(context).addPassword(password);
          password = nextPassword;
        }
      }

      PasswordProvider.getInstance(context).addPassword(password);
      cursor.close();
      provider.close();

    } catch (Exception e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }

    return null;
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
    cursor.close();
  }
}