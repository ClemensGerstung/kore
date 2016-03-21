package com.typingsolutions.passwordmanager;

import android.content.DialogInterface;

public class BaseDialogCallback<TActivity extends BaseActivity> extends BaseCallback<TActivity>
    implements DialogInterface.OnClickListener, DialogInterface.OnShowListener {

  public BaseDialogCallback(TActivity activity) {
    super(activity);
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {

  }

  @Override
  public void onShow(DialogInterface dialog) {

  }

  @Override
  public void setValues(Object... values) {

  }
}
