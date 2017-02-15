package com.typingsolutions.kore.setup;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;


class EnableSetupTextWatcher implements TextWatcher {
  private SetupActivity mActivity;
  private Fragment mFragment;
  private TextInputEditText mCounterpart;
  private boolean mRefreshPim;

  EnableSetupTextWatcher(SetupActivity activity, TextInputEditText counterpart) {
    this(activity, null, counterpart);
  }

  EnableSetupTextWatcher(SetupActivity activity, Fragment fragment, TextInputEditText counterpart) {
    this(activity, fragment, counterpart, false);
  }

  EnableSetupTextWatcher(SetupActivity activity, Fragment fragment, TextInputEditText counterpart, boolean refreshPim) {
    mActivity = activity;
    mFragment = fragment;
    mCounterpart = counterpart;
    mRefreshPim = refreshPim;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    if(mFragment instanceof ExtendSetupFragment && mRefreshPim) {
      ((ExtendSetupFragment)mFragment).setCurrentPIM();
    }

    mActivity.enableSetupButton(s.length() > 0 && mCounterpart.length() > 0);
  }
}
