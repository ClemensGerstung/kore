package com.typingsolutions.passwordmanager.callbacks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.fragments.SetupPasswordFragment;

public class SetupPasswordCallback extends BaseClickCallback<SetupActivity> {

  private SetupPasswordFragment mSender;
  private SetupPasswordDialogCallback mPasswordDialogCallback;

  public SetupPasswordCallback(SetupActivity activity, SetupPasswordFragment sender) {
    super(activity);
    this.mSender = sender;
    this.mPasswordDialogCallback = new SetupPasswordDialogCallback(activity, sender);
  }

  @Override
  public void onClick(View v) {
    if(!this.mSender.arePasswordsEntered()){
      mActivity.makeSnackbar("You must enter a password to continue!");
      return;
    }

    if (!this.mSender.checkPasswordsMatch()) {
      mActivity.makeSnackbar("The entered password don't match!");
      return;
    }

    if(!this.mSender.checkPasswordSafety()) {
      AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
          .setTitle("Your password doesn't seem to be safe")
          .setMessage("We recommend to use lower and upper letters, digits, some special characters and at least 8 characters. Do you want to keep it anyway?")
          .setPositiveButton("GOT IT", mPasswordDialogCallback)
          .setNegativeButton("CHANGE", mPasswordDialogCallback)
          .create();

      alertDialog.show();
    } else {
      mActivity.moveToNextPage();
      mSender.copyPasswordToParentActivity();
    }
  }
}
