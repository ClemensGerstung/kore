package com.typingsolutions.passwordmanager.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.typingsolutions.passwordmanager.BaseFragment;


public class SimpleViewFragment extends BaseFragment {
  private int mLayout;

  public static SimpleViewFragment create(int layout) {
    SimpleViewFragment fragment = new SimpleViewFragment();
    fragment.mLayout = layout;
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(mLayout, container, false);
  }
}
