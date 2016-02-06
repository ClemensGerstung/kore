package com.typingsolutions.passwordmanager.callbacks.click;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import core.Utils;

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
    if (!loginActivity.isPasswordSafe()) {
      AlertDialog alertDialog = new AlertDialog.Builder(context)
          .setTitle("Your password doesn't seem to be safe")
          .setMessage("We recommend to use lower and upper letters, digits, some special characters and at least 8 characters. Do you want to keep it anyway?")
          .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
              setup();
            }
          })
          .setNegativeButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
              loginActivity.retypePassword();
            }
          })
          .create();

      alertDialog.show();
      return;
    }

    setup();
  }

  private void setup() {
    if(loginActivity.setupDatabase()) {
      Intent intent = new Intent(context, PasswordOverviewActivity.class);
      context.startActivity(intent);
      ActivityCompat.finishAfterTransition(loginActivity);
    }
  }
}
