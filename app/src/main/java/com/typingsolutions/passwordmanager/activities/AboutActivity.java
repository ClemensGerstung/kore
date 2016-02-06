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
import com.typingsolutions.passwordmanager.callbacks.click.ToolbarNavigationCallback;


public class AboutActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.about_license_layout);
    Toolbar toolbar_actionbar = (Toolbar) findViewById(R.id.aboutlayout_toolbar_actionbar);
    setSupportActionBar(toolbar_actionbar);


  }
}
