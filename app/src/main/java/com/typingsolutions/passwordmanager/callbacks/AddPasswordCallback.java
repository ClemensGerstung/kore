package com.typingsolutions.passwordmanager.callbacks;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.activities.CreatePasswordActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.BaseCallback;


public class AddPasswordCallback extends BaseClickCallback<PasswordOverviewActivity> {

  public AddPasswordCallback(PasswordOverviewActivity passwordOverviewActivity) {
    super(passwordOverviewActivity);
  }

  @Override
  public void onClick(View view) {
    BaseDatabaseActivity.logout = false;
    mActivity.startActivity(CreatePasswordActivity.class);
  }
}
