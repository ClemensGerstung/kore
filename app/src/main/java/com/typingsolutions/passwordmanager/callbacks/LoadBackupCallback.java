package com.typingsolutions.passwordmanager.callbacks;

import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.BackupActivity;


public class LoadBackupCallback extends BaseClickCallback<BackupActivity> {

  public LoadBackupCallback(BackupActivity activity) {
    super(activity);
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public void onClick(View v) {
//    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//    intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//    intent.setType("*/*");
//    activity.startActivityForResult(intent, BackupActivity.RESTORE_REQUEST_CODE);
  }
}
