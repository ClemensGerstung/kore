package com.typingsolutions.kore.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SimpleViewFragment extends Fragment {
  private int mLayout;

  public static SimpleViewFragment create(int layout) {
    SimpleViewFragment fragment = new SimpleViewFragment();
    fragment.mLayout = layout;
    return fragment;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("layout", mLayout);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mLayout == 0 && savedInstanceState != null) {
      mLayout = savedInstanceState.getInt("layout");
    }

    return inflater.inflate(mLayout, container, false);
  }
}
