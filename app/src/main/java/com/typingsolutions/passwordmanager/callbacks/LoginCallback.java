package com.typingsolutions.passwordmanager.callbacks;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
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

    AlertDialog dialog = new AlertDialog.Builder(mActivity, R.style.Base_AlertDialog_LoginStyle)
        .setView(R.layout.pim_login_layout)
        .setPositiveButton("Login", dialogCallback)
        .setNegativeButton("Cancel", null)
        .create();

    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    dialog.show();
  }
}
