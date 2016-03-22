package com.typingsolutions.passwordmanager;

import android.view.View;

public abstract class BaseClickCallback<TActivity extends BaseActivity> extends BaseCallback<TActivity>
    implements View.OnClickListener {

  public BaseClickCallback(TActivity activity) {
    super(activity);
  }

  @Override
  public void setValues(Object... values) {

  }
}
