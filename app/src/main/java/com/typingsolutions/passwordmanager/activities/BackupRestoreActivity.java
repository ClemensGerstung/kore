package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.adapter.BackupViewPagerAdapter;


public class BackupRestoreActivity extends AppCompatActivity {
  private Toolbar toolbar_actionbar;
  private TabLayout tablayout_tabhost;
  private ViewPager viewpager_wrapper;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backup_restore_layout);

    toolbar_actionbar = (Toolbar) findViewById(R.id.backuprestorelayout_toolbar_actionbar);
    tablayout_tabhost = (TabLayout) findViewById(R.id.backuprestorelayout_tablayout_tabhost);
    viewpager_wrapper = (ViewPager) findViewById(R.id.backuprestorelayout_viewpager_wrapper);

    setSupportActionBar(toolbar_actionbar);

    BackupViewPagerAdapter adapter = new BackupViewPagerAdapter(this, getSupportFragmentManager());
    viewpager_wrapper.setAdapter(adapter);
    tablayout_tabhost.setupWithViewPager(viewpager_wrapper);

  }
}
