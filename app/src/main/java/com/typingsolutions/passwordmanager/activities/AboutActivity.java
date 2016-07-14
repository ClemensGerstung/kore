package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.R;


public class AboutActivity extends BaseActivity {

  private Toolbar mToolbarAsActionbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.about_license_layout);

    mToolbarAsActionbar = findCastedViewById(R.id.aboutlayout_toolbar_actionbar);
    setSupportActionBar(mToolbarAsActionbar);
  }

  @Override
  protected View getSnackbarRelatedView() {
    return this.mToolbarAsActionbar;
  }

  @Override
  protected void onActivityChange() {

  }
}
