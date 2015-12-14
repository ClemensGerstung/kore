package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.adapter.AboutViewPagerAdapter;


public class AboutActivity extends AppCompatActivity {
  private Toolbar toolbar_actionbar;
  private TabLayout tabLayout_navigation;
  private ViewPager viewpager_content;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.about_license_layout);
    toolbar_actionbar = (Toolbar) findViewById(R.id.aboutlayout_toolbar_actionbar);
    tabLayout_navigation = (TabLayout) findViewById(R.id.aboutlayout_tablayout_navigation);
    viewpager_content = (ViewPager) findViewById(R.id.aboutlayout_viewpager_content);

    setSupportActionBar(toolbar_actionbar);
    toolbar_actionbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    AboutViewPagerAdapter adapter = new AboutViewPagerAdapter(this, getSupportFragmentManager());

    viewpager_content.setAdapter(adapter);
    tabLayout_navigation.setupWithViewPager(viewpager_content);

    tabLayout_navigation.getTabAt(0).setIcon(R.mipmap.info);
    tabLayout_navigation.getTabAt(1).setIcon(R.mipmap.copyright);
  }
}
