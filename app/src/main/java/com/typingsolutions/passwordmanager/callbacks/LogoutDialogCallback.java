package com.typingsolutions.passwordmanager.callbacks;

import android.content.DialogInterface;
import com.typingsolutions.passwordmanager.BaseDialogCallback;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;

public class LogoutDialogCallback extends BaseDialogCallback<PasswordOverviewActivity> {
  public LogoutDialogCallback(PasswordOverviewActivity activity) {
    super(activity);
  }

  @Override
  public void OnPositiveButtonPressed(DialogInterface dialog) {
    mActivity.logout();
  }
}
