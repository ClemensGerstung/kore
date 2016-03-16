package core.callback;

import android.content.Context;
import core.async.SqlTaskCallback;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.PasswordProvider;

import java.util.Date;

public class AddHistoryCallback implements SqlTaskCallback<Long> {
  private Context context;
  private String password;
  private Password object;
  private Date date;

  public AddHistoryCallback(Context context, String password, Password object) {
    this(context, password, object, new Date());
  }

  public AddHistoryCallback(Context context, String password, Password object, Date date) {
    this.context = context;
    this.password = password;
    this.object = object;
    this.date = date;
  }

  @Override
  public void executed(Long result) {
    PasswordHistory history = new PasswordHistory(this.password, this.date);
    this.object.addPasswordHistoryItem(result.intValue(), history);

    PasswordProvider.getInstance(context).editPassword(this.object);
  }

  @Override
  public void failed(String message) {

  }
}
