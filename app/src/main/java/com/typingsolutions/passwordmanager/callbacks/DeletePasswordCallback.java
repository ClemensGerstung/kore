package com.typingsolutions.passwordmanager.callbacks;


import android.content.DialogInterface;
import android.view.View;
import com.typingsolutions.passwordmanager.AlertBuilder;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.BaseDialogCallback;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;

public class DeletePasswordCallback extends BaseClickCallback<PasswordDetailActivity> {
  private DeletePasswordDialogCallback mDialogCallback;

  public DeletePasswordCallback(PasswordDetailActivity passwordDetailActivity, PasswordContainer password) {
    super(passwordDetailActivity);
    mDialogCallback = new DeletePasswordDialogCallback(mActivity, password);
  }

  @Override
  public void onClick(View v) {
    AlertBuilder.create(mActivity)
        .setMessage("Delete Password?")
        .setPositiveButton("delete")
        .setNegativeButton("cancel")
        .setCallback(mDialogCallback)
        .show();

//        AlertDialog dialog = new AlertDialog.Builder(mActivity)
//                .setTitle("Delete mTextViewAsPassword")
//                .setMessage("Are you sure to delete this mTextViewAsPassword?")
//                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        PasswordProvider.getInstance(context).removePassword(mCurrentPassword);
//                        passwordDetailActivity.onBackPressed();
//                    }
//                })
//                .setNegativeButton("DISCARD", null)
//                .notify();
//        dialog.show();
  }
}
