package core.callback;

import android.content.ContentValues;
import android.content.Context;
import core.DatabaseProvider;
import core.Utils;
import core.async.ISqlTaskCallback;
import core.async.SqlInsertTask;
import core.data.Password;

public class AddPasswordCallback implements ISqlTaskCallback<Long> {

  private Context context;
  private String username;
  private String program;
  private String password;
  private int position;

  public AddPasswordCallback(Context context, String username, String program, String password, int position) {
    this.context = context;
    this.username = username;
    this.program = program;
    this.password = password;
    this.position = position;
  }

  @Override
  public void executed(Long result) {
    Password obj = new Password(result.intValue(), position, username, program);
    AddNewHistoryCallback callback = new AddNewHistoryCallback(context, this.password, obj);

    ContentValues values = new ContentValues(3);
    values.put("password", this.password);
    values.put("changed", Utils.getToday());
    values.put("passwordId", result);

    SqlInsertTask insertTask = new SqlInsertTask(DatabaseProvider.getConnection(context).getDatabase(), "history", "", values, callback);
    insertTask.execute();
  }

  @Override
  public void failed(String message) {

  }
}
