package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.BaseCallback;

public class RetypePasswordCallback extends BaseClickCallback<LoginActivity> {

  public RetypePasswordCallback(LoginActivity loginActivity) {
    super(loginActivity);
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public void onClick(View v) {
//    loginActivity.retypePassword();
  }
}
