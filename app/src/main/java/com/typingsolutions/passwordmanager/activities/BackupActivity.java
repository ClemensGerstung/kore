package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.ToolbarNavigationCallback;


public class BackupActivity extends BaseDatabaseActivity {
  public static final int BACKUP_REQUEST_CODE = 36;
  public static final int RESTORE_REQUEST_CODE = 37;

  private Toolbar mToolbarAsActionbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backup_layout);

    mToolbarAsActionbar = findCastedViewById(R.id.backuprestorelayout_toolbar_actionbar);
    setSupportActionBar(mToolbarAsActionbar);
    mToolbarAsActionbar.setNavigationOnClickListener(new ToolbarNavigationCallback(this));
  }


  @Override
  protected View getSnackbarRelatedView() {
    return this.mToolbarAsActionbar;
  }

  @Override
  protected void onActivityChange() {

  }


}
