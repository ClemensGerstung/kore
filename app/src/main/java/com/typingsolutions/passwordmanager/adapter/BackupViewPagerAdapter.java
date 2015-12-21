package com.typingsolutions.passwordmanager.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.typingsolutions.passwordmanager.fragments.BackupFragment;
import com.typingsolutions.passwordmanager.fragments.RestoreFragment;


public class BackupViewPagerAdapter extends FragmentPagerAdapter {
  private final String[] titles = {"Backup", "Restore"};
  private Fragment[] contents = new Fragment[2];
  protected Context context;

  public BackupViewPagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    this.context = context;
    contents[0] = new BackupFragment();
    contents[1] = new RestoreFragment();
  }

  @Override
  public Fragment getItem(int position) {
    return contents[position];
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return titles[position];
  }
}
