package com.typingsolutions.passwordmanager.callbacks.click;

import android.content.Context;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.BaseCallback;

public class RetypePasswordCallback extends BaseCallback {
  private LoginActivity loginActivity;

  public RetypePasswordCallback(Context context, LoginActivity loginActivity) {
    super(context);
    this.loginActivity = loginActivity;
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public void onClick(View v) {
    loginActivity.retypePassword();
  }
}
