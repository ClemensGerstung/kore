package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.BaseCallback;
import core.DatabaseProvider;
import core.exceptions.LoginException;

public class LoginCallback extends BaseClickCallback<LoginActivity> {
  private String password;
  private boolean safeLogin;

  private final DatabaseProvider.OnOpenListener openListener = new DatabaseProvider.OnOpenListener() {
    @Override
    public void open() {
//      try {
//        loginActivity.hideWaiter();
//        Intent intent = new Intent(context, PasswordOverviewActivity.class);
//        intent.putExtra(LoginActivity.SAFELOGIN, safeLogin);
//        context.startActivity(intent);
//        loginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//
//        loginActivity.getLoginServiceRemote().resetTries();
//        ActivityCompat.finishAfterTransition(loginActivity);
//      } catch (RemoteException e) {
//        Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
//      }
    }

    @Override
    public void refused() {
//      try {
//        int remTries = loginActivity.getLoginServiceRemote().getRemainingTries();
//        loginActivity.hideWaiter();
//        Snackbar.make(loginActivity.getRootView(), String.format("Your password is wrong! %s tries left", remTries), Snackbar.LENGTH_LONG).show();
//        loginActivity.getLoginServiceRemote().increaseTries();
//        loginActivity.retypePassword();
//      } catch (RemoteException e) {
//        Snackbar.make(loginActivity.getRootView(), "Sorry, something went wrong", Snackbar.LENGTH_LONG).show();
//      }
    }
  };


  public LoginCallback(LoginActivity activity) {
    super(activity);
  }

  @Override
  public void onClick(View v) {
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
