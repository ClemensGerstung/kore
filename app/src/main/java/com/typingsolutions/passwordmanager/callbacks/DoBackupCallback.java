package com.typingsolutions.passwordmanager.callbacks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.BackupRestoreActivity;
import com.typingsolutions.passwordmanager.BaseCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DoBackupCallback extends BaseClickCallback {


  public DoBackupCallback(BaseActivity baseActivity) {
    super(baseActivity);
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public void onClick(View v) {
//    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//    intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//    String fileName = String.format("password-manager-backup-%s.encrypt", dateFormat.format(new Date()));
//    intent.putExtra(Intent.EXTRA_TITLE, fileName);
//    intent.setType("*/*");
//    ((Activity) context).startActivityForResult(intent, BackupRestoreActivity.BACKUP_REQUEST_CODE);
  }
}
