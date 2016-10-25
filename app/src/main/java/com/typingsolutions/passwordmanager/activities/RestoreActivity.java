package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.fragments.BottomSheetViewerFragment;
import com.typingsolutions.passwordmanager.fragments.GDriveRestoreBottomSheetFragment;

public class RestoreActivity extends BaseDatabaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int selection = getIntent().getIntExtra("selection", -1);
    logout = false;

    if (selection == 0) {

    } else if(selection == 1) {
      GDriveRestoreBottomSheetFragment gDriveRestoreBottomSheetFragment = new GDriveRestoreBottomSheetFragment();
      gDriveRestoreBottomSheetFragment.setOnDismissListener(fragment -> finish());
      gDriveRestoreBottomSheetFragment.show(getSupportFragmentManager(), "GDriveLoader");
    }
  }

  @Override
  protected View getSnackbarRelatedView() {
    return getWindow().getDecorView();
  }

  @Override
  protected void onActivityChange() {

  }
}
