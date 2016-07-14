package com.typingsolutions.passwordmanager;

import android.content.DialogInterface;

public abstract class BaseDialogCallback<TActivity extends BaseActivity> extends BaseCallback<TActivity>
    implements DialogInterface.OnClickListener, DialogInterface.OnShowListener {

  public BaseDialogCallback(TActivity activity) {
    super(activity);
  }

  @Override
  public final void onClick(DialogInterface dialog, int which) {
    switch (which) {
      case DialogInterface.BUTTON_POSITIVE:
        OnPositiveButtonPressed(dialog);
        break;
      case DialogInterface.BUTTON_NEGATIVE:
        OnNegativeButtonPressed(dialog);
        break;
      case DialogInterface.BUTTON_NEUTRAL:
        OnNeutralButtonPressed(dialog);
        break;
      default:
        mActivity.makeSnackbar("Unknown Dialog Button pressed!");
        break;
    }
  }

  @Override
  public void onShow(DialogInterface dialog) {

  }

  @Override
  public void setValues(Object... values) {

  }

  public abstract void OnPositiveButtonPressed(DialogInterface dialog);

  public void OnNegativeButtonPressed(DialogInterface dialog) {  }

  public void OnNeutralButtonPressed(DialogInterface dialog) {  }
}
