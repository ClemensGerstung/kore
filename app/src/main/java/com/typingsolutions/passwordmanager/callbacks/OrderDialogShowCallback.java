package com.typingsolutions.passwordmanager.callbacks;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import com.typingsolutions.passwordmanager.BaseDialogCallback;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.adapter.PasswordOverviewAdapter;

public class OrderDialogShowCallback extends BaseDialogCallback<PasswordOverviewActivity> {

  private CheckBox mCheckboxAsInverseOrder;
  private RadioGroup mRadioGroupAsOrderSelection;

  public OrderDialogShowCallback(PasswordOverviewActivity activity) {
    super(activity);
  }

  @Override
  public void onShow(DialogInterface dialog) {
    if (!(dialog instanceof AlertDialog))
      return;

    AlertDialog alertDialog = (AlertDialog) dialog;

    mRadioGroupAsOrderSelection = (RadioGroup) alertDialog.findViewById(R.id.orderlayout_radiogroup_wrapper);
    mCheckboxAsInverseOrder = (CheckBox) alertDialog.findViewById(R.id.orderlayout_checkbox_inverse);
  }

  @Override
  public void OnPositiveButtonPressed(DialogInterface dialog) {
    int checkedId = mRadioGroupAsOrderSelection.getCheckedRadioButtonId();
    boolean invert = mCheckboxAsInverseOrder.isChecked();

    switch (checkedId) {
      case R.id.orderlayout_radiobutton_password:
        if(invert) {
          mActivity.order(PasswordOverviewAdapter.OrderOptions.PasswordDescending);
        } else {
          mActivity.order(PasswordOverviewAdapter.OrderOptions.PasswordAscending);
        }
        break;
      case R.id.orderlayout_radiobutton_username:
        if(invert) {
          mActivity.order(PasswordOverviewAdapter.OrderOptions.UsernameDescending);
        } else {
          mActivity.order(PasswordOverviewAdapter.OrderOptions.UsernameAscending);
        }
        break;
      case R.id.orderlayout_radiobutton_program:
        if(invert) {
          mActivity.order(PasswordOverviewAdapter.OrderOptions.ProgramDescending);
        } else {
          mActivity.order(PasswordOverviewAdapter.OrderOptions.ProgramAscending);
        }
        break;
      case R.id.orderlayout_radiobutton_custom:
        mActivity.order(PasswordOverviewAdapter.OrderOptions.Custom);
        break;
    }
  }
}
