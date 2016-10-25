package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.fragments.BottomSheetViewerFragment;

public class RestoreActivity extends BaseDatabaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int selection = getIntent().getIntExtra("selection", -1);

    if (selection == 0) {

    } else if(selection == 1) {

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
