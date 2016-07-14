package com.typingsolutions.passwordmanager;

import android.content.BroadcastReceiver;

public abstract class BaseReceiver<TActivity extends BaseActivity> extends BroadcastReceiver {
  protected TActivity mActivity;

  public BaseReceiver(TActivity mActivity) {
    this.mActivity = mActivity;
  }

  public BaseReceiver() {
    this.mActivity = null;
  }
}
