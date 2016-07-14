package com.typingsolutions.passwordmanager.callbacks;

import android.content.DialogInterface;
import com.typingsolutions.passwordmanager.BaseDialogCallback;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.fragments.SetupPimFragment;

public class SetupPimDialogCallback extends BaseDialogCallback<SetupActivity> {
  private SetupPimFragment mFragment;

  public SetupPimDialogCallback(SetupActivity activity, SetupPimFragment fragment) {
    super(activity);
    this.mFragment = fragment;
  }

  @Override
  public void OnNegativeButtonPressed(DialogInterface dialog) {
    dialog.dismiss();
    mFragment.retypePim();
  }

  @Override
  public void OnPositiveButtonPressed(DialogInterface dialog) {
    dialog.dismiss();
    mActivity.moveToNextPage();
    mFragment.copyPimToParentActivity();
  }
}
