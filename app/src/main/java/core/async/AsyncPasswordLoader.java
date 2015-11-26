package core.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncPasswordLoader extends AsyncTask<String, Void, Void> {
  private Context context;

  public AsyncPasswordLoader(Context context) {
    super();
    this.context = context;
  }

  @Override
  protected Void doInBackground(String... params) {
    try {

    } catch (Exception e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }

    return null;
  }
}