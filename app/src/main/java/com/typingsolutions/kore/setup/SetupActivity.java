package com.typingsolutions.kore.setup;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.typingsolutions.kore.R;
import com.typingsolutions.kore.common.KoreApplication;
import com.typingsolutions.kore.common.SimplePagerAdapter;
import com.typingsolutions.kore.common.SimpleViewFragment;

public class SetupActivity extends AppCompatActivity {
  private ViewPager mViewPagerAsContentWrapper;
  private SimplePagerAdapter mSetupPageAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_layout);

    if(Build.VERSION.SDK_INT >= 21) {
      getWindow().setStatusBarColor(0x44000000);
    }

    mViewPagerAsContentWrapper = (ViewPager) findViewById(R.id.setuplayout_viewpager_contenthost);



    KoreApplication app = (KoreApplication) getApplicationContext();
    app.setOnDatabaseOpened((sender, e) -> Log.d(getClass().getSimpleName(), "" + e.getData()));
  }

  @Override
  protected void onResume() {
    super.onResume();

    mSetupPageAdapter = new SimplePagerAdapter(getSupportFragmentManager(), new Fragment[]
        {
            SimpleViewFragment.create(R.layout.setup_fragment_1),
            SimpleViewFragment.create(R.layout.setup_fragment_1)
        });

    mViewPagerAsContentWrapper.setAdapter(mSetupPageAdapter);
  }
}

