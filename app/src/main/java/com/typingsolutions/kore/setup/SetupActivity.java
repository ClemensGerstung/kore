package com.typingsolutions.kore.setup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.typingsolutions.kore.R;
import com.typingsolutions.kore.common.DatabaseConnection;
import com.typingsolutions.kore.common.KoreApplication;
import net.sqlcipher.database.SQLiteDatabase;

public class SetupActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_layout);

    KoreApplication app = (KoreApplication) getApplicationContext();

    app.setOnDatabaseOpened((sender, e) -> Log.d(getClass().getSimpleName(), "" + e.getData()));
  }


}

class SetupPagerAdapter extends FragmentPagerAdapter {

  private Fragment[] mFragments;

  public SetupPagerAdapter(FragmentManager fragmentManager, Fragment[] fragments) {
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
