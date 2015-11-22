package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;

public class SetupCallback extends BaseCallback {
  private LoginActivity loginActivity;

  public SetupCallback(Context context, LoginActivity loginActivity) {
    super(context);
    this.loginActivity = loginActivity;
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public void onClick(View v) {
    if(loginActivity.setupDatabase()) {
      Intent intent = new Intent(context, PasswordOverviewActivity.class);
      context.startActivity(intent);
    }
  }
}
