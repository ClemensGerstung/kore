package com.typingsolutions.passwordmanager.callbacks;

import android.view.View;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseClickCallback;

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
//    String fileName = String.format("mTextViewAsPassword-manager-backup-%s.encrypt", dateFormat.format(new Date()));
//    intent.putExtra(Intent.EXTRA_TITLE, fileName);
//    intent.setType("*/*");
//    ((Activity) context).startActivityForResult(intent, BackupActivity.BACKUP_REQUEST_CODE);
  }
}
