package com.typingsolutions.passwordmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
  protected BaseActivity mActivity;

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if(this.mActivity != null) return;
    this.mActivity = (BaseActivity) this.getActivity();
  }
}
