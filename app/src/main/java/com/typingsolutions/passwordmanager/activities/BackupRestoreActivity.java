package com.typingsolutions.passwordmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.click.DoBackupCallback;
import com.typingsolutions.passwordmanager.callbacks.click.ExpandCallback;
import core.DatabaseProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class BackupRestoreActivity extends AppCompatActivity {
  public static final int BACKUP_REQUEST_CODE = 36;

  private Button doBackup;
  private ImageButton expand;
  private TextInputLayout passwordWrapper;
  private EditText editText_password;
  private TextInputLayout repeatPasswordWrapper;
  private EditText editText_repeatPassword;
  private TextView hint;
  private Toolbar toolbar_actionbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backup_restore_layout);

    toolbar_actionbar = (Toolbar) findViewById(R.id.backuprestorelayout_toolbar_actionbar);

    doBackup = (Button) findViewById(R.id.backuprestorelayout_button_dobackup);
    expand = (ImageButton) findViewById(R.id.backuprestorelayout_imagebutton_expand);
    passwordWrapper = (TextInputLayout) findViewById(R.id.backuprestorelayout_textinputlayout_passwordwrapper);
    repeatPasswordWrapper = (TextInputLayout) findViewById(R.id.backuprestorelayout_textinputlayout_repeatpasswordwrapper);
    hint = (TextView) findViewById(R.id.backuprestorelayout_textview_hint);

    setSupportActionBar(toolbar_actionbar);

    expand.setOnClickListener(new ExpandCallback(this, this));
    doBackup.setOnClickListener(new DoBackupCallback(this));

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Backup database
    if(requestCode == BACKUP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      if(data == null) return;
      Uri uri = data.getData();

      try {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "w");
        File source = getDatabasePath(DatabaseProvider.DATABASE_NAME);

        FileInputStream is = new FileInputStream(source);
        FileOutputStream os = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
          os.write(buffer, 0, length);
        }
        is.close();
        os.close();

        final int flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            & data.getFlags();

        getContentResolver().takePersistableUriPermission(uri, flags);
      } catch (Exception e) {
        Snackbar.make(this.getWindow().getDecorView(), "Couldn't do your backup", Snackbar.LENGTH_LONG).show();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  public TextView getHint() {
    return hint;
  }

  public TextInputLayout getRepeatPasswordWrapper() {
    return repeatPasswordWrapper;
  }

  public TextInputLayout getPasswordWrapper() {
    return passwordWrapper;
  }
}
