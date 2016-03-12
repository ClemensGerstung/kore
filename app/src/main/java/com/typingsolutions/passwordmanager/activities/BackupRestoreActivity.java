package com.typingsolutions.passwordmanager.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.click.DoBackupCallback;
import com.typingsolutions.passwordmanager.callbacks.click.ExpandCallback;
import com.typingsolutions.passwordmanager.callbacks.click.LoadBackupCallback;
import com.typingsolutions.passwordmanager.callbacks.click.ToolbarNavigationCallback;
import core.DatabaseProvider;
import core.Utils;
import core.data.Password;
import core.data.PasswordProvider;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.List;


public class BackupRestoreActivity extends AppCompatActivity {
  public static final int BACKUP_REQUEST_CODE = 36;
  public static final int RESTORE_REQUEST_CODE = 37;

  private Button doBackup;
  private Button loadBackup;
  private ImageButton expand;
  private TextInputLayout passwordWrapper;
  private EditText editText_password;
  private TextInputLayout repeatPasswordWrapper;
  private EditText editText_repeatPassword;
  private EditText editText_restorePassword;
  private TextView hint;
  private Toolbar toolbar_actionbar;

  private final TextWatcher passwordTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      if ((passwordWrapper.getVisibility() & repeatPasswordWrapper.getVisibility()) == View.GONE) {
        doBackup.setEnabled(true);
        return;
      }

      boolean enabled = editText_password.getText().toString().equals(editText_repeatPassword.getText().toString());
      doBackup.setEnabled(enabled);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };

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
    editText_password = (EditText) findViewById(R.id.backuprestorelayout_edittext_password);
    editText_repeatPassword = (EditText) findViewById(R.id.backuprestorelayout_edittext_repeatpassword);
    editText_restorePassword = (EditText) findViewById(R.id.backuprestorelayout_edittext_restorepassword);
    loadBackup = (Button) findViewById(R.id.backuprestorelayout_button_loadbackup);

    setSupportActionBar(toolbar_actionbar);
    toolbar_actionbar.setNavigationOnClickListener(new ToolbarNavigationCallback(this));

    expand.setOnClickListener(new ExpandCallback(this, this));
    doBackup.setOnClickListener(new DoBackupCallback(this));
    loadBackup.setOnClickListener(new LoadBackupCallback(this));

    editText_password.addTextChangedListener(passwordTextWatcher);
    editText_repeatPassword.addTextChangedListener(passwordTextWatcher);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
    // Backup database
    if (requestCode == BACKUP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      if (data == null) return;
      final Uri uri = data.getData();

      if (editText_password.length() > 0 && !Utils.isSafe(editText_password.getText().toString())) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle("Your backup's password doesn't seem to be safe!")
            .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                copyDatabase(uri);
              }
            })
            .setNegativeButton("CHANGE", null)
            .create();
        alertDialog.show();
        return;
      }

      copyDatabase(uri);
    }

    // restore database
    if (requestCode == RESTORE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      if (data == null) return;
      final Uri uri = data.getData();
      try {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");

        String path = getDatabasePath(DatabaseProvider.DATABASE_NAME).getPath();
        path = path.substring(0, path.lastIndexOf("/"));

        // temporary file to copy and change password.
        final File tmp = new File(path, "tmp.db");

        String password = editText_restorePassword.getText().toString();

        DatabaseProvider.OnOpenPathListener openPathListener = new DatabaseProvider.OnOpenPathListener() {
          @Override
          public void open(SQLiteDatabase database) {
            Cursor cursor = database.rawQuery(DatabaseProvider.GET_PASSWORDS, null);
            List<Password> passwords = PasswordProvider.getPasswords(cursor);

            PasswordProvider.getInstance(BackupRestoreActivity.this).merge(passwords);
            Snackbar
                .make(BackupRestoreActivity.this.toolbar_actionbar, "Restore successful", Snackbar.LENGTH_LONG)
                .show();

            //noinspection ResultOfMethodCallIgnored
            //tmp.delete();
            cursor.close();
            database.close();
          }

          @Override
          public void open() {

          }

          @Override
          public void refused() {
            //noinspection ResultOfMethodCallIgnored
            tmp.delete();
            Snackbar.make(toolbar_actionbar, "Couldn't load your backup", Snackbar.LENGTH_LONG).show();
          }
        };

        Utils.copyFile(parcelFileDescriptor.getFileDescriptor(), tmp, Utils.Flag.Restore);

        DatabaseProvider.openDatabase(tmp.getPath(), password, openPathListener);
      } catch (Exception e) {
        Snackbar.make(toolbar_actionbar, "Couldn't load your backup", Snackbar.LENGTH_LONG).show();
      }
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void copyDatabase(final Uri uri) {
    final int flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

    try {
      final ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "w");
      File source = getDatabasePath(DatabaseProvider.DATABASE_NAME);

      // temporary file to copy and change password.
      final File tmp = new File(source.getPath().substring(0, source.getPath().lastIndexOf("/")), "tmp.db");

      //noinspection ResultOfMethodCallIgnored
      tmp.createNewFile();


      DatabaseProvider.OnChangePasswordListener changePasswordListener
          = new DatabaseProvider.OnChangePasswordListener() {
        @Override
        public void changed() {
          // copy database with changed password to new location
          Utils.copyFile(tmp, parcelFileDescriptor.getFileDescriptor(), Utils.Flag.Backup);
          getContentResolver().takePersistableUriPermission(uri, flags);
          Snackbar.make(toolbar_actionbar, "Your backup and changing password of this was successful!", Snackbar.LENGTH_LONG).show();

          //noinspection ResultOfMethodCallIgnored
          tmp.delete();
        }

        @Override
        public void failed() {
          // copy database with unchanged password
          Utils.copyFile(tmp, parcelFileDescriptor.getFileDescriptor(), Utils.Flag.Backup);
          getContentResolver().takePersistableUriPermission(uri, flags);
          Snackbar.make(toolbar_actionbar, "Your backup was successful but the password is your current password!", Snackbar.LENGTH_LONG).show();

          //noinspection ResultOfMethodCallIgnored
          tmp.delete();
        }

        @Override
        public void open() {

        }

        @Override
        public void refused() {

        }
      };

      // if set new password to copied database
      if (editText_repeatPassword.length() > 0 && editText_password.length() > 0) {
        // copy database to tmp-database and change password of this...
        // unfortunately there is no better way to do this because I'm not able to access the recently copied file
        Utils.copyFile(source, tmp, Utils.Flag.Backup);

        String path = tmp.getPath();
        DatabaseProvider.changePassword(path, editText_password.getText().toString(), changePasswordListener);
      } else {
        // simply copy database with current master password
        Utils.copyFile(source, parcelFileDescriptor.getFileDescriptor(), Utils.Flag.Backup);

        getContentResolver().takePersistableUriPermission(uri, flags);
        Snackbar.make(toolbar_actionbar, "Backup of your passwords was successful!", Snackbar.LENGTH_LONG).show();

        //noinspection ResultOfMethodCallIgnored
        tmp.delete();
      }
    } catch (Exception e) {
      Snackbar.make(toolbar_actionbar, "Couldn't backup your passwords", Snackbar.LENGTH_LONG).show();
    }
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
