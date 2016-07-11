package com.typingsolutions.passwordmanager.callbacks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import com.typingsolutions.passwordmanager.AlertBuilder;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;

public class LoginCallback extends BaseClickCallback<LoginActivity> {

  private LoginDialogCallback dialogCallback = new LoginDialogCallback(mActivity);

  public LoginCallback(LoginActivity activity) {
    super(activity);
  }

  @Override
  public void onClick(View v) {
    AlertBuilder
        .create(mActivity, R.style.Base_AlertDialog_LoginStyle)
        .setView(R.layout.pim_login_layout)
        .setPositiveButton("Login")
        .setNegativeButton("Cancel")
        .setSecurityFlags()
        .setCallback(dialogCallback)
        .show();
  }
}
