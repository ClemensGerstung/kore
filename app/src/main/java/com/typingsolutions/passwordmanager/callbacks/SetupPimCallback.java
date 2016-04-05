package com.typingsolutions.passwordmanager.callbacks;

import android.app.AlertDialog;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.fragments.SetupPimFragment;

public class SetupPimCallback extends BaseClickCallback<SetupActivity> {
  private SetupPimFragment mFragment;
  private SetupPimDialogCallback mPimDialogCallback;


  public SetupPimCallback(SetupActivity activity, SetupPimFragment sender) {
    super(activity);
    this.mFragment = sender;
    this.mPimDialogCallback = new SetupPimDialogCallback(activity, sender);
  }

  @Override
  public void onClick(View v) {
    if (!mFragment.hasPim()) {
      mActivity.makeSnackbar("You must enter a number to continue!");
      return;
    }

    if (!mFragment.isPimHighEnough()) {
      AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
          .setTitle("Your number doesn't seem to be high enough")
          .setMessage("You should use a number somewhere above 485.\nDo You want to continue?")
          .setPositiveButton("YES", mPimDialogCallback)
          .setNegativeButton("CHANGE", mPimDialogCallback)
          .create();

      alertDialog.show();
    } else {
      mActivity.moveToNextPage();
      mFragment.copyPimToParentActivity();
    }
  }
}
