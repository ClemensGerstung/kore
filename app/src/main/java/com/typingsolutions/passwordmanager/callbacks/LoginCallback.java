package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import core.DatabaseProvider;
import core.exceptions.LoginException;

public class LoginCallback extends BaseCallback {
  private LoginActivity loginActivity;
  private String password;


  public LoginCallback(Context context, LoginActivity activity) {
    super(context);
    this.loginActivity = activity;
  }

  @Override
  public void onClick(View v) {
    try {
      final SharedPreferences preferences = loginActivity.getPreferences(Context.MODE_PRIVATE);
      final boolean checked = preferences.getBoolean(LoginActivity.SAFELOGIN, false);

      DatabaseProvider provider = DatabaseProvider.getConnection(context);
      if (!provider.tryOpen(password)) {
        Snackbar.make(v, "Your password is wrong!", Snackbar.LENGTH_LONG).show();
        loginActivity.getLoginServiceRemote().increaseTries();
        return;
      }

      Intent intent = new Intent(context, PasswordOverviewActivity.class);
      context.startActivity(intent);
      loginActivity.finish();
    } catch (Exception e) {
      Snackbar.make(v, "Sorry, something went wrong", Snackbar.LENGTH_LONG).show();
    }
  }

  @Override
  public void setValues(Object... values) {
    if (values[0] instanceof String) {
      password = (String) values[0];
    }
  }
}
