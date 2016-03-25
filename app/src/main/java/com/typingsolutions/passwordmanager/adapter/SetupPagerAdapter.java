package com.typingsolutions.passwordmanager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.fragments.SetupLoginFragment;
import com.typingsolutions.passwordmanager.fragments.SetupPasswordFragment;
import com.typingsolutions.passwordmanager.fragments.SetupPimFragment;
import com.typingsolutions.passwordmanager.fragments.SetupWelcomeFragment;


public class SetupPagerAdapter extends FragmentPagerAdapter {

  private final BaseFragment[] mFragments = {new SetupWelcomeFragment(), new SetupPasswordFragment(), new SetupPimFragment(), new SetupLoginFragment()};

  public SetupPagerAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
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
