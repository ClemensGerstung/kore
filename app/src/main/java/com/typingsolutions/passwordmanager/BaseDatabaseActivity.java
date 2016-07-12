package com.typingsolutions.passwordmanager;

import android.content.Context;
import android.support.annotation.Nullable;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import net.sqlcipher.database.SQLiteDatabase;

public abstract class BaseDatabaseActivity extends BaseActivity {
  protected static BaseDatabaseConnection connection;

  public static boolean logout = true;

  @Nullable
  public static SQLiteDatabase getDatabase() {
    return connection != null ? connection.getDatabase() : null;
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (connection == null && logout) {
      this.startActivity(LoginActivity.class, true);
    }
  }

  @Override
  protected void onStop() {
    if (logout && connection != null) {
      connection.close();
      connection = null;
    }
    super.onStop();
  }
}
