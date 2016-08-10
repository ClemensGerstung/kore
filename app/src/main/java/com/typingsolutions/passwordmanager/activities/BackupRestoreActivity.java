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
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.LoginSafeLoginCheckBoxChangeCallback;
import core.DatabaseProvider;
import core.Utils;
import core.data.Password;
import core.data.PasswordProvider;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.List;


public class BackupRestoreActivity extends BaseDatabaseActivity {
  public static final int BACKUP_REQUEST_CODE = 36;
  public static final int RESTORE_REQUEST_CODE = 37;

  private Button mButtonAsBackup;
  private Button mButtonAsRestore;
  private ImageButton mImageButtonAsExpandBackupCard;
  private TextInputLayout mTextInputLayoutAsWrapperForEditTextAsBackupPassword;
  private EditText mEditTextAsBackupPassword;
  private TextInputLayout mTextInputLayoutAsWrapperForEditTextAsRepeatBackupPassword;
  private EditText mEditTextAsRepeatedBackupPassword;
  private EditText mEditTextAsRestorePassword;
  private TextView mTextViewAsHintForBackupPassword;
  private Toolbar mToolbarAsActionbar;
  private SwitchCompat mSwitchAsSetPasswordForBackup;
  private ExpandableLinearLayout mExpandableLinearLayoutAsInputWrapper;
  private SwitchCompat mSwitchAsScheduleBackup;
  private ExpandableLinearLayout mExpandableLinearLayoutAsScheduleWrapper;

  // TODO: extract to callbackclass
  private final TextWatcher passwordTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      if ((mTextInputLayoutAsWrapperForEditTextAsBackupPassword.getVisibility() & mTextInputLayoutAsWrapperForEditTextAsRepeatBackupPassword.getVisibility()) == View.GONE) {
        mButtonAsBackup.setEnabled(true);
        return;
      }

      boolean enabled = mEditTextAsBackupPassword.getText().toString().equals(mEditTextAsRepeatedBackupPassword.getText().toString());
      mButtonAsBackup.setEnabled(enabled);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backup_restore_layout);

    mToolbarAsActionbar = findCastedViewById(R.id.backuprestorelayout_toolbar_actionbar);

    mEditTextAsRestorePassword = findCastedViewById(R.id.backuprestorelayout_edittext_restorepassword);
    mButtonAsRestore = findCastedViewById(R.id.backuprestorelayout_button_loadbackup);
    mSwitchAsScheduleBackup = findCastedViewById(R.id.backuplayout_switch_schedulebackup);
    mExpandableLinearLayoutAsScheduleWrapper = findCastedViewById(R.id.backuplayout_expandablelayout_schedulerwrapper);

    mSwitchAsScheduleBackup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mExpandableLinearLayoutAsScheduleWrapper.toggle();
      }
    });

    setSupportActionBar(mToolbarAsActionbar);
//    mToolbarAsActionbar.setNavigationOnClickListener(new ToolbarNavigationCallback(this));
//
//    mImageButtonAsExpandBackupCard.setOnClickListener(new ExpandCallback(this, this));
//    mButtonAsBackup.setOnClickListener(new DoBackupCallback(this));
//    mButtonAsRestore.setOnClickListener(new LoadBackupCallback(this));

//    mEditTextAsBackupPassword.addTextChangedListener(passwordTextWatcher);
//    mEditTextAsRepeatedBackupPassword.addTextChangedListener(passwordTextWatcher);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
    // Backup database
    if (requestCode == BACKUP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      if (data == null) return;
      final Uri uri = data.getData();

      if (mEditTextAsBackupPassword.length() > 0 && !Utils.isSafe(mEditTextAsBackupPassword.getText().toString())) {
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

        // temporary file to copy and change mTextViewAsPassword.
        final File tmp = new File(path, "tmp.db");

        String password = mEditTextAsRestorePassword.getText().toString();

        DatabaseProvider.OnOpenPathListener openPathListener = new DatabaseProvider.OnOpenPathListener() {
          @Override
          public void open(SQLiteDatabase database) {
            Cursor cursor = database.rawQuery(DatabaseProvider.GET_PASSWORDS, null);
            List<Password> passwords = PasswordProvider.getPasswords(cursor);

            PasswordProvider.getInstance(BackupRestoreActivity.this).merge(passwords);
            Snackbar
                .make(BackupRestoreActivity.this.mToolbarAsActionbar, "Restore successful", Snackbar.LENGTH_LONG)
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
            Snackbar.make(mToolbarAsActionbar, "Couldn't load your backup", Snackbar.LENGTH_LONG).show();
          }
        };

        Utils.copyFile(parcelFileDescriptor.getFileDescriptor(), tmp, Utils.Flag.Restore);

        DatabaseProvider.openDatabase(tmp.getPath(), password, openPathListener);
      } catch (Exception e) {
        Snackbar.make(mToolbarAsActionbar, "Couldn't load your backup", Snackbar.LENGTH_LONG).show();
      }
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void copyDatabase(final Uri uri) {
    final int flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

    try {
      final ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "w");
      File source = getDatabasePath(DatabaseProvider.DATABASE_NAME);

      // temporary file to copy and change mTextViewAsPassword.
      final File tmp = new File(source.getPath().substring(0, source.getPath().lastIndexOf("/")), "tmp.db");

      //noinspection ResultOfMethodCallIgnored
      tmp.createNewFile();


      DatabaseProvider.OnChangePasswordListener changePasswordListener
          = new DatabaseProvider.OnChangePasswordListener() {
        @Override
        public void changed() {
          // copy database with changed mTextViewAsPassword to new location
          Utils.copyFile(tmp, parcelFileDescriptor.getFileDescriptor(), Utils.Flag.Backup);
          getContentResolver().takePersistableUriPermission(uri, flags);
          Snackbar.make(mToolbarAsActionbar, "Your backup and changing password of this was successful!", Snackbar.LENGTH_LONG).show();

          //noinspection ResultOfMethodCallIgnored
          tmp.delete();
        }

        @Override
        public void failed() {
          // copy database with unchanged mTextViewAsPassword
          Utils.copyFile(tmp, parcelFileDescriptor.getFileDescriptor(), Utils.Flag.Backup);
          getContentResolver().takePersistableUriPermission(uri, flags);
          Snackbar.make(mToolbarAsActionbar, "Your backup was successful but the password is your current password!", Snackbar.LENGTH_LONG).show();

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

      // if set new mTextViewAsPassword to copied database
      if (mEditTextAsRepeatedBackupPassword.length() > 0 && mEditTextAsBackupPassword.length() > 0) {
        // copy database to tmp-database and change mTextViewAsPassword of this...
        // unfortunately there is no better way to do this because ISqlTaskCallback'm not able to access the recently copied file
        Utils.copyFile(source, tmp, Utils.Flag.Backup);

        String path = tmp.getPath();
        DatabaseProvider.changePassword(path, mEditTextAsBackupPassword.getText().toString(), changePasswordListener);
      } else {
        // simply copy database with current master mTextViewAsPassword
        Utils.copyFile(source, parcelFileDescriptor.getFileDescriptor(), Utils.Flag.Backup);

        getContentResolver().takePersistableUriPermission(uri, flags);
        Snackbar.make(mToolbarAsActionbar, "Backup of your passwords was successful!", Snackbar.LENGTH_LONG).show();

        //noinspection ResultOfMethodCallIgnored
        tmp.delete();
      }
    } catch (Exception e) {
      Snackbar.make(mToolbarAsActionbar, "Couldn't backup your passwords", Snackbar.LENGTH_LONG).show();
    }
  }

  public TextView getHint() {
    return mTextViewAsHintForBackupPassword;
  }

  public TextInputLayout getRepeatPasswordWrapper() {
    return mTextInputLayoutAsWrapperForEditTextAsRepeatBackupPassword;
  }

  public TextInputLayout getPasswordWrapper() {
    return mTextInputLayoutAsWrapperForEditTextAsBackupPassword;
  }

  @Override
  protected View getSnackbarRelatedView() {
    return this.mToolbarAsActionbar;
  }

  @Override
  protected void onActivityChange() {

  }
}
