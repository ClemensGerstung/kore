package com.typingsolutions.passwordmanager.callbacks;

import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.BaseFragment;

public class SwitchFragmentCallback extends BaseClickCallback<BaseActivity> {
  private BaseFragment mFragment;
  private int mOutAnim;
  private int mInAnim;
  private int mOldFragment;

  public SwitchFragmentCallback(BaseActivity activity, @IdRes int oldFragment, BaseFragment fragment, @AnimRes int inAnim, @AnimRes int outAnim) {
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
