package com.typingsolutions.passwordmanager.callbacks;

import android.content.DialogInterface;
import com.typingsolutions.passwordmanager.BaseDialogCallback;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.fragments.SetupPasswordFragment;

public class SetupPasswordDialogCallback extends BaseDialogCallback<SetupActivity> {

  private SetupPasswordFragment mFragment;

  public SetupPasswordDialogCallback(SetupActivity activity, SetupPasswordFragment fragment) {
    super(activity);
    this.mFragment = fragment;
  }

  @Override
  public void OnNegativeButtonPressed(DialogInterface dialog) {
    dialog.dismiss();
    mFragment.retypePassword();
  }

  @Override
  public void OnPositiveButtonPressed(DialogInterface dialog) {
    dialog.dismiss();
    mActivity.moveToNextPage();
    mFragment.copyPasswordToParentActivity();
  }
}
