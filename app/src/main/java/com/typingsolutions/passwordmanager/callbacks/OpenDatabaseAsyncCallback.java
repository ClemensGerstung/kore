package com.typingsolutions.passwordmanager.callbacks;

import com.typingsolutions.passwordmanager.BaseAsyncTask;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;

import java.lang.ref.WeakReference;

public class OpenDatabaseAsyncCallback implements BaseAsyncTask.IExecutionCallback<Boolean> {
  private WeakReference<LoginActivity> mActivity;

  public OpenDatabaseAsyncCallback(LoginActivity activity) {
    this.mActivity = new WeakReference<>(activity);
  }

  @Override
  public void executed(Boolean aBoolean) {
    mActivity.get().startActivity(PasswordOverviewActivity.class, true);
    mActivity.get().hideWaiter();
    mActivity.get().stopLoginService();
  }

  @Override
  public void failed(int code, String message) {
    mActivity.get().makeSnackbar(message + " " + mActivity.get().getRemainingTries() + " tries left.");
    mActivity.get().hideWaiter();
    mActivity.get().increaseTries();
  }
}
