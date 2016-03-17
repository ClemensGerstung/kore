package com.typingsolutions.passwordmanager;

public abstract class BaseCallback {
  protected BaseActivity mActivity;

  public BaseCallback(BaseActivity activity) {
    this.mActivity = activity;
  }

  @Deprecated
  public abstract void setValues(Object... values);
}
