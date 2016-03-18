package core.async;

import com.typingsolutions.passwordmanager.BaseActivity;
import net.sqlcipher.database.SQLiteDatabase;

public class PasswordRemoveTask extends SqlAsyncTask<Long, BaseActivity> {
  public PasswordRemoveTask(BaseActivity activity, SQLiteDatabase db, String table, String selection, String[] selectionArgs, ISqlTaskCallback<Long> callback) {
    super(activity, db, table, selection, selectionArgs, callback);
  }

  public PasswordRemoveTask(BaseActivity activity, SQLiteDatabase db, String table, String selection, String[] selectionArgs) {
    super(activity, db, table, selection, selectionArgs);
  }

  @Override
  protected boolean querySuccessCondition(Long result) {
    return result > 0;
  }

  @Override
  protected Long doInBackground(Void... voids) {
    return super.doDeleteTask();
  }
}
