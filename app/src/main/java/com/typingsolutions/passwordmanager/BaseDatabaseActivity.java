package com.typingsolutions.passwordmanager;

import android.support.annotation.Nullable;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDatabaseActivity extends BaseActivity {
  protected static BaseDatabaseConnection connection;
  private static List<IContainer> items = new ArrayList<>();

  public static boolean logout = true;

  public static void useEstablishedConnection(BaseDatabaseConnection connection) {
    BaseDatabaseActivity.connection = (BaseDatabaseConnection) connection.clone();
  }

  @Nullable
  public static SQLiteDatabase getDatabase() {
    return connection != null ? connection.getDatabase() : null;
  }

  public void addContainerItem(IContainer container) {
    items.add(container);

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
