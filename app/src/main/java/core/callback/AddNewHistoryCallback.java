package core.callback;

import android.content.Context;
import core.async.SqlTaskCallback;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.PasswordProvider;

import java.util.Date;

public class AddNewHistoryCallback implements SqlTaskCallback {
  private Context context;
  private String password;
  private Password object;

  public AddNewHistoryCallback(Context context, String password, Password object) {
    this.context = context;
    this.password = password;
    this.object = object;
  }

  @Override
  public void executed(int result) {
    PasswordHistory history = new PasswordHistory(this.password, new Date());
    this.object.addPasswordHistoryItem(result, history);

    PasswordProvider.getInstance(context).addPassword(this.object);
  }

  @Override
  public void failed(String message) {

  }
}
