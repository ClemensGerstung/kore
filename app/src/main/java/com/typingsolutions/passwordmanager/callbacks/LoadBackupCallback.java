package com.typingsolutions.passwordmanager.callbacks;

import android.content.Intent;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.BackupRestoreActivity;
import com.typingsolutions.passwordmanager.BaseCallback;


public class LoadBackupCallback extends BaseClickCallback<BackupRestoreActivity> {

  public LoadBackupCallback(BackupRestoreActivity activity) {
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
//    activity.startActivityForResult(intent, BackupRestoreActivity.RESTORE_REQUEST_CODE);
  }
}
