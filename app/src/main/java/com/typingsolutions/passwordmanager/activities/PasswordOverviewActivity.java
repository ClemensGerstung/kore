package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.typingsolutions.passwordmanager.R;

public class PasswordOverviewActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_list_layout);

  }

  @Override
  public void onBackPressed() {

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {


    return true;
  }
}
