package com.typingsolutions.passwordmanager.callbacks;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;

public class LoginCallback extends BaseClickCallback<LoginActivity> {
  private String password;
  private boolean safeLogin;

  public LoginCallback(LoginActivity activity) {
    super(activity);
  }

  @Override
  public void onClick(View v) {

    AlertDialog dialog = new AlertDialog.Builder(mActivity, R.style.Base_AlertDialog_LoginStyle)
        .setView(R.layout.pim_login_layout)
        .setPositiveButton("Login", null)
        .create();

    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    dialog.show();

//    try {
//      if (loginActivity.getLoginServiceRemote().isBlocked())
//        throw new LoginException("Sorry, you're blocked", LoginException.BLOCKED);
//
//      v.setEnabled(false);
//      loginActivity.showWaiter();
//      DatabaseProvider provider = DatabaseProvider.getConnection(context);
//      provider.tryOpen(password, openListener);
//      //Log.d(getClass().getSimpleName(), password);
//
//    } catch (Exception e) {
//      if (e instanceof LoginException
//          && ((LoginException) e).getState() == LoginException.BLOCKED) {
//        Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();
//      } else {
//        Snackbar.make(v, "Sorry, something went wrong", Snackbar.LENGTH_LONG).show();
//      }
//    }
  }

  @Override
  public void setValues(Object... values) {
    if (values.length == 0) return;
    if (values[0] instanceof String) {
      password = (String) values[0];
    }
    if (values.length == 1) return;
    if (values[1] instanceof Boolean) {
      safeLogin = (boolean) values[1];
    }
  }
}
