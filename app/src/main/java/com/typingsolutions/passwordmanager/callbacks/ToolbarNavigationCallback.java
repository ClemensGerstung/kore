package com.typingsolutions.passwordmanager.callbacks;

import android.view.View;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseClickCallback;

public class ToolbarNavigationCallback extends BaseClickCallback<BaseActivity> {
  public ToolbarNavigationCallback(BaseActivity activity) {
    super(activity);
  }

  @Override
  public void onClick(View v) {
    mActivity.onBackPressed();
  }
}
