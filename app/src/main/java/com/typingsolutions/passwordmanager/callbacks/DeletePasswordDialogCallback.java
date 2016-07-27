package com.typingsolutions.passwordmanager.callbacks;

import android.content.DialogInterface;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.BaseDialogCallback;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;

public class DeletePasswordDialogCallback extends BaseDialogCallback<BaseDatabaseActivity> {
  private PasswordContainer mPasswordContainer;


  public DeletePasswordDialogCallback(BaseDatabaseActivity activity, PasswordContainer container) {
    super(activity);
    mPasswordContainer = container;
  }

  @Override
  public void OnPositiveButtonPressed(DialogInterface dialog) {
    mActivity.removeContainerItem(mPasswordContainer);
    if (!(mActivity instanceof PasswordOverviewActivity)) {
      mActivity.onBackPressed();
    }
  }
}
