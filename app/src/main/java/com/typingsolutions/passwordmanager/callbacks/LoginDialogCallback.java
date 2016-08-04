package com.typingsolutions.passwordmanager.callbacks;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.BaseDialogCallback;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;

public class LoginDialogCallback extends BaseDialogCallback<LoginActivity> {
  public LoginDialogCallback(LoginActivity activity) {
    super(activity);
  }

  @Override
  public void OnPositiveButtonPressed(DialogInterface dialog) {
    AlertDialog alert = (AlertDialog) dialog;
    EditText editText = (EditText) alert.findViewById(R.id.loginlayout_edittext_pim);

    mActivity.login(editText.getText().toString());
  }

  @Override
  public void onShow(DialogInterface dialog) {
    AlertDialog alert = (AlertDialog) dialog;
    EditText editText = (EditText) alert.findViewById(R.id.loginlayout_edittext_pim);
    editText.requestFocus();
  }
}
