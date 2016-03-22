package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.R;

public class SetupActivity extends BaseActivity {
  private CoordinatorLayout mCoordinatorLayoutAsRootLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_main_layout);

    mCoordinatorLayoutAsRootLayout = findCastedViewById(R.id.setuplayout_coordinatorlayout_root);
  }

  @Override
  protected View getSnackbarRelatedView() {
    return mCoordinatorLayoutAsRootLayout;
  }
}
