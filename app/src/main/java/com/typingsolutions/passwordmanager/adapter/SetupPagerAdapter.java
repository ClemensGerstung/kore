package com.typingsolutions.passwordmanager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.typingsolutions.passwordmanager.BaseFragment;


public class SetupPagerAdapter extends FragmentPagerAdapter {

  private BaseFragment[] mFragments;

  public SetupPagerAdapter(FragmentManager fragmentManager, BaseFragment[] fragments) {
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
