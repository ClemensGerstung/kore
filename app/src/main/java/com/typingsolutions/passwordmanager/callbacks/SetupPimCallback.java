package com.typingsolutions.passwordmanager.callbacks;

import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.fragments.SetupPimFragment;

public class SetupPimCallback extends BaseClickCallback<SetupActivity> {
  private SetupPimFragment mFragment;

  public SetupPimCallback(SetupActivity activity, SetupPimFragment sender) {
    super(activity);
    this.mFragment = sender;
  }

  @Override
  public void onClick(View v) {
    mActivity.moveToNextPage();
  }
}
