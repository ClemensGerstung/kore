package com.typingsolutions.passwordmanager.callbacks;

import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.activities.SetupActivity;

public class SwitchFragmentCallback extends BaseClickCallback<SetupActivity> {
  private BaseFragment<SetupActivity> mFragment;
  private int mOutAnim;
  private int mInAnim;
  private int mOldFragment;

  public SwitchFragmentCallback(SetupActivity activity, @IdRes int oldFragment, BaseFragment<SetupActivity> fragment, @AnimRes int inAnim, @AnimRes int outAnim) {
    super(activity);
    this.mFragment = fragment;
    this.mInAnim = inAnim;
    this.mOutAnim = outAnim;
    this.mOldFragment = oldFragment;
  }

  @Override
  public void onClick(View v) {
    mActivity.getSupportFragmentManager()
        .beginTransaction()
        .setCustomAnimations(mInAnim, mOutAnim)
        .replace(mOldFragment, mFragment)
        .commit();
  }
}
