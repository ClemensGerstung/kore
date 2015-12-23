package com.typingsolutions.passwordmanager.callbacks.click;

import android.app.Activity;
import android.view.View;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;

public class ToolbarNavigationCallback extends BaseCallback {

  private Activity activity;

  public ToolbarNavigationCallback(Activity activity) {
    super(activity);
    this.activity = activity;
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public void onClick(View v) {
    activity.onBackPressed();
  }
}
