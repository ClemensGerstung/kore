package core.async;

import android.content.Context;
import android.os.AsyncTask;
import core.data.PasswordProvider;

public class AsyncPasswordWriter extends AsyncTask<String, Void, Void> {
  private Context context;

  public AsyncPasswordWriter(Context context) {
    this.context = context;
  }

  @Override
  protected Void doInBackground(String... strings) {
    PasswordProvider provider = PasswordProvider.getInstance(context);
    provider.getTransactions().commitAllTransactions(context);
    return null;
  }
}
