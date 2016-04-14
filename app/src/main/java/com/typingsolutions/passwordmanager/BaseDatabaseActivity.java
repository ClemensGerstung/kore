package com.typingsolutions.passwordmanager;

import com.typingsolutions.passwordmanager.activities.LoginActivity;

public abstract class BaseDatabaseActivity extends BaseActivity {
  protected static BaseDatabaseConnection connection;

  public static boolean logout = true;

  @Override
  protected void onResume() {
    super.onResume();

    if (connection == null) {
      this.startActivity(LoginActivity.class, true);
      return;
    }

    logout = true;
  }

  @Override
  protected void onStop() {
    if (logout && connection != null) {
      connection.close();
      connection = null;

      finish();
    }
    super.onStop();
  }
}
