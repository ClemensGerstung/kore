package core.async;


import android.content.ContentValues;
import com.typingsolutions.passwordmanager.BaseActivity;
import net.sqlcipher.database.SQLiteDatabase;

public class PasswordInsertTask extends SqlAsyncTask<Long, BaseActivity> {

  public PasswordInsertTask(BaseActivity activity, SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
    super(activity, db, table, nullColumnHack, values);
  }

  public PasswordInsertTask(BaseActivity activity, SQLiteDatabase db, String table, String nullColumnHack, ContentValues values, ISqlTaskCallback<Long> callback) {
    super(activity, db, table, nullColumnHack, values, callback);
  }

  @Override
  protected boolean querySuccessCondition(Long result) {
    return result > 0;
  }

  @Override
  protected Long doInBackground(Void... params) {
    return super.doInsertQuery();
  }
}
