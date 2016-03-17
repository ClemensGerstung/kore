package com.typingsolutions.passwordmanager.receiver;

import android.content.Context;
import android.content.Intent;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseReceiver;
import com.typingsolutions.passwordmanager.activities.LoginActivity;

public class ScreenOffReceiver extends BaseReceiver<BaseActivity> {



  public ScreenOffReceiver(BaseActivity activity) {
    super(activity);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    mActivity.startActivity(LoginActivity.class, true);
  }
}
