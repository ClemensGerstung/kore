package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.typingsolutions.passwordmanager.R;


public class AboutActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.about_license_layout);
    Toolbar toolbar_actionbar = (Toolbar) findViewById(R.id.aboutlayout_toolbar_actionbar);
    setSupportActionBar(toolbar_actionbar);


  }
}
