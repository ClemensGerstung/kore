package com.typingsolutions.passwordmanager;

public abstract class BaseCallback<TActivity extends BaseActivity> {
  protected TActivity mActivity;

  public BaseCallback(TActivity activity) {
    this.mActivity = activity;
  }

  @Deprecated
  public abstract void setValues(Object... values);
}
