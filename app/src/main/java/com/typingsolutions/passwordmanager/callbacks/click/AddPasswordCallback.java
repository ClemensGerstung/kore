package com.typingsolutions.passwordmanager.callbacks.click;

import android.content.Intent;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.CreatePasswordActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;


public class AddPasswordCallback extends BaseCallback {
  private PasswordOverviewActivity passwordOverviewActivity;
  public AddPasswordCallback(PasswordOverviewActivity passwordOverviewActivity) {
    super(passwordOverviewActivity);
    this.passwordOverviewActivity = passwordOverviewActivity;
  }

  @Override
  public void setValues(Object... values) {
  }

  @Override
  public void onClick(View view) {
    passwordOverviewActivity.doNotLogout();
    Intent intent = new Intent(context, CreatePasswordActivity.class);
    context.startActivity(intent);
  }
}
