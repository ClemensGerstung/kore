package com.typingsolutions.passwordmanager.callbacks;

import android.support.design.widget.Snackbar;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.fragments.SetupLoginFragment;

public class SetupLoginCallback extends BaseClickCallback<SetupActivity> {
  private SetupLoginFragment mFragment;

  public SetupLoginCallback(SetupActivity activity, SetupLoginFragment fragment) {
    super(activity);
    mFragment = fragment;
  }

  @Override
  public void onClick(View v) {
    if(!mFragment.checkPassword()) {
      Snackbar.make(v, "Your passwords don't match", Snackbar.LENGTH_LONG).show();
      return;
    }

    if(!mFragment.checkPim()) {
      Snackbar.make(v, "Your secret number doesn't match", Snackbar.LENGTH_LONG).show();
      return;
    }

    mFragment.copyPimToParentActivity();
    mActivity.setupDatabase();
    mActivity.startActivity(PasswordOverviewActivity.class, true);
  }
}
