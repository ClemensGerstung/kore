package com.typingsolutions.kore.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimplePagerAdapter extends FragmentPagerAdapter {

  private Fragment[] mFragments;

  public SimplePagerAdapter(FragmentManager fragmentManager, Fragment[] fragments) {
    super(fragmentManager);
    mFragments = fragments;
  }

  @Override
  public int getCount() {
    return mFragments.length;
  }

  @Override
  public Fragment getItem(int position) {
    return mFragments[position];
  }
}
