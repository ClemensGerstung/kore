package core.async;

import android.content.ContentValues;
import com.typingsolutions.passwordmanager.BaseActivity;
import net.sqlcipher.database.SQLiteDatabase;

public class PasswordUpdateTask extends SqlAsyncTask<Integer> {
  public PasswordUpdateTask(SQLiteDatabase db, String table, ContentValues values, String selection, String[] selectionArgs) {
    super(db, table, values, selection, selectionArgs);
  }

  public PasswordUpdateTask(SQLiteDatabase db, String table, ContentValues values, String selection, String[] selectionArgs, ISqlTaskCallback<Integer> callback) {
    super(db, table, values, selection, selectionArgs, callback);
  }

  @Override
  protected boolean querySuccessCondition(Integer result) {
    return result > 0;
  }

  @Override
  protected Integer doInBackground(Void... voids) {
    return super.doUpdateQuery();
  }
}
