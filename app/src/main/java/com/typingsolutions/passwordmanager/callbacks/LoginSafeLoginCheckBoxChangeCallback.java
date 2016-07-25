package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CompoundButton;
import com.typingsolutions.passwordmanager.BaseCallback;
import com.typingsolutions.passwordmanager.activities.LoginActivity;

public class LoginSafeLoginCheckBoxChangeCallback extends BaseCallback<LoginActivity> implements CompoundButton.OnCheckedChangeListener {
  public LoginSafeLoginCheckBoxChangeCallback(LoginActivity activity) {
    super(activity);
  }

  @Override
  public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
    final SharedPreferences preferences = mActivity.getPreferences(Context.MODE_PRIVATE);
    preferences.edit().putBoolean(LoginActivity.SAFE_LOGIN, b).apply();
  }

  @Deprecated
  @Override
  public void setValues(Object... values) {

  }
}
