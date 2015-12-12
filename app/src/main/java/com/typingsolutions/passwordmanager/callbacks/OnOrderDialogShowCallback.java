package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import com.typingsolutions.passwordmanager.R;
import core.data.PasswordProvider;

public class OnOrderDialogShowCallback implements DialogInterface.OnShowListener, DialogInterface.OnClickListener {

  private Context context;
  private CheckBox inverse;
  private RadioGroup group;

  public OnOrderDialogShowCallback(Context context) {
    this.context = context;
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    int checkedId = group.getCheckedRadioButtonId();
    boolean invert = inverse.isChecked();
    int order = (checkedId == R.id.orderlayout_radiobutton_password ? 2 :
            (checkedId == R.id.orderlayout_radiobutton_username ? 0 :
            (checkedId == R.id.orderlayout_radiobutton_program ? 4 : 0)))
        + (invert ? 1 : 0);

    PasswordProvider.getInstance(context).order(order);
  }

  @Override
  public void onShow(DialogInterface dialog) {
    if (!(dialog instanceof AlertDialog))
      return;

    AlertDialog alertDialog = (AlertDialog) dialog;

    group = (RadioGroup) alertDialog.findViewById(R.id.orderlayout_radiogroup_wrapper);
    inverse = (CheckBox) alertDialog.findViewById(R.id.orderlayout_checkbox_inverse);

    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "order", this);
  }
}
