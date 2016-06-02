package com.typingsolutions.passwordmanager.callbacks;

import com.typingsolutions.passwordmanager.BaseAsyncTask;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;

public class OpenDatabaseAsyncCallback implements BaseAsyncTask.IExecutionCallback<Boolean> {
  private LoginActivity mActivity;

  public OpenDatabaseAsyncCallback(LoginActivity activity) {
    this.mActivity = activity;
  }

  @Override
  public void executed(Boolean aBoolean) {
    mActivity.startActivity(PasswordOverviewActivity.class, true);
    mActivity.hideWaiter();
  }

  @Override
  public void failed(int code, String message) {
    mActivity.makeSnackbar(message);
    mActivity.hideWaiter();
  }
}
