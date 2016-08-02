package com.typingsolutions.passwordmanager.callbacks;

import android.support.v4.app.Fragment;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseCallback;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.AboutActivity;
import com.typingsolutions.passwordmanager.activities.SetupActivity;
import com.typingsolutions.passwordmanager.fragments.LicenseFragment;

public class ShowLicensesClickCallback extends BaseClickCallback<AboutActivity> {

  public ShowLicensesClickCallback(AboutActivity activity) {
    super(activity);
  }

  @Override
  public void onClick(View view) {
    mActivity
        .getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.aboutlicenselayout_fragment_host, new LicenseFragment())
        .commit();
  }
}
