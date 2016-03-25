package com.typingsolutions.passwordmanager;

import android.support.v4.app.Fragment;

public abstract class BaseFragment<TActivity extends BaseActivity> extends Fragment {
  private TActivity mActivity;

  protected TActivity getSupportActivity() {
    return (TActivity) getActivity();
  }
}
