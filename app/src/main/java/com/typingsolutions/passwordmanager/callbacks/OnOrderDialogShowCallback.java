package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import com.typingsolutions.passwordmanager.R;
import core.data.PasswordProvider;

public class OnOrderDialogShowCallback implements DialogInterface.OnShowListener, DialogInterface.OnClickListener {

  private int checkedId;
  private boolean invert;
  private Context context;

  public OnOrderDialogShowCallback(Context context) {
    this.context = context;
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    int order = (checkedId == R.id.orderlayout_radiobutton_password ? 0 :
            (checkedId == R.id.orderlayout_radiobutton_username ? 2 :
            (checkedId == R.id.orderlayout_radiobutton_program ? 3 : 0)))
        + (invert ? 1 : 0);

    PasswordProvider.getInstance(context).order(order);
  }

  @Override
  public void onShow(DialogInterface dialog) {
    if (!(dialog instanceof AlertDialog))
      return;

    AlertDialog alertDialog = (AlertDialog) dialog;

    RadioGroup group = (RadioGroup) alertDialog.findViewById(R.id.orderlayout_radiogroup_wrapper);
    checkedId = group.getCheckedRadioButtonId();
    CheckBox inverse = (CheckBox) alertDialog.findViewById(R.id.orderlayout_checkbox_inverse);
    invert = inverse.isChecked();

    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "order", this);
  }
}
