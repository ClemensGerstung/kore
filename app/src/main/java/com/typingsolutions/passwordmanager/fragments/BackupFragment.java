package com.typingsolutions.passwordmanager.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.typingsolutions.passwordmanager.*;
import com.typingsolutions.passwordmanager.activities.BackupActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.database.BackupDatabaseConnection;
import core.Utils;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackupFragment extends BaseFragment<BackupActivity> {

  private Button mButtonAsDoBackup;
  private SwitchCompat mSwitchAsSetPassword;
  private ExpandableLinearLayout mExpandableAsPasswordWrapper;
  private TextInputEditText mEditTextAsEnterPassword;
  private TextInputEditText mEditTextAsRepeatPassword;
  private LinearLayout mLinearLayoutAsLoader;
  private LinearLayout mLinearLayoutAsContent;
  private Looper mLooper;
  private Handler mHandler;
  private String mPassword;
  private Uri mURI;
  private boolean mStartToFileSelector = false;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.simple_backup_layout, container, false);

    mButtonAsDoBackup = (Button) root.findViewById(R.id.backuplayout_button_dobackup);
    mSwitchAsSetPassword = (SwitchCompat) root.findViewById(R.id.backuplayout_switch_setpassword);
    mExpandableAsPasswordWrapper = (ExpandableLinearLayout) root.findViewById(R.id.backuplayout_expandablelayout_inputwrapper);
    mEditTextAsEnterPassword = (TextInputEditText) root.findViewById(R.id.backuplayout_edittext_enterpassword);
    mEditTextAsRepeatPassword = (TextInputEditText) root.findViewById(R.id.backuplayout_edittext_repeatpassword);
    mLinearLayoutAsContent = (LinearLayout) root.findViewById(R.id.backuplayout_linearlayout_content);
    mLinearLayoutAsLoader = (LinearLayout) root.findViewById(R.id.backuplayout_linearlayout_loader);

    mSwitchAsSetPassword.setOnCheckedChangeListener(this::toggleExpandableLayout);
    mButtonAsDoBackup.setOnClickListener(this::doBackupClick);

    HandlerThread thread = new HandlerThread("BackupThread", Process.THREAD_PRIORITY_MORE_FAVORABLE);
    thread.start();
    mLooper = thread.getLooper();
    mHandler = new Handler(mLooper);
    mStartToFileSelector = false;

    return root;
  }

  @Override
  public void onPause() {
    if (!mHandler.hasMessages(0) && !mStartToFileSelector) {
      mLooper.quit();
    }

    super.onPause();
  }

  private void toggleExpandableLayout(CompoundButton btn, boolean checked) {
    mExpandableAsPasswordWrapper.toggle();
  }

  private void doBackupClick(View view) {
    String password1 = mEditTextAsEnterPassword.getText().toString();
    String password2 = mEditTextAsRepeatPassword.getText().toString();

    if (mSwitchAsSetPassword.isChecked() && (password1.isEmpty() || password2.isEmpty())) {
      getSupportActivity().makeSnackbar("Please enter a password for the backup");
      return;
    }

    if (mSwitchAsSetPassword.isChecked() && !password1.equals(password2)) {
      getSupportActivity().makeSnackbar("The passwords must be equal");
      return;
    }

    if (mSwitchAsSetPassword.isChecked() &&
        !password1.isEmpty() &&
        !Utils.isSafe(password1)) {
      AlertBuilder.create(getContext())
          .setPositiveButton("got it")
          .setNegativeButton("change")
          .setMessage("The password doesn't seems to be save")
          .setCallback(new AlertCallback(getSupportActivity()))
          .show();
      return;
    }

    startFileSelector();
  }

  public void startFileSelector() {
    mStartToFileSelector = true;
    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String fileName = String.format("kore-backup-%s.encrypt", dateFormat.format(new Date()));
    intent.putExtra(Intent.EXTRA_TITLE, fileName);
    intent.setType("*/*");
    startActivityForResult(intent, BackupActivity.BACKUP_REQUEST_CODE);
  }

  @Override
  @AfterPermissionGranted(0x100)
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode != BackupActivity.BACKUP_REQUEST_CODE || resultCode != Activity.RESULT_OK) {
      return;
    }
    if (data == null) return;
    mURI = data.getData();

    final int flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    if (!EasyPermissions.hasPermissions(getContext(), permissions)) {
      EasyPermissions.requestPermissions(getActivity(), "asdf", 0x100, permissions);
      return;
    }

    String original = mEditTextAsEnterPassword.getText().toString();
    mPassword = original.isEmpty() ? getSupportActivity().getConnection().getPassword() : original;
    mHandler.post(this::copy);

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void copy() {
    getSupportActivity().startAnimatorOnUiThread(mLinearLayoutAsContent, R.animator.fade_out);
    getSupportActivity().startAnimatorOnUiThread(mLinearLayoutAsLoader, R.animator.fade_in);

    PasswordContainer[] passwords = new PasswordContainer[getSupportActivity().containerCount()];
    getSupportActivity().getItems().toArray(passwords);
    File file = getContext().getDatabasePath(BackupDatabaseConnection.NAME);
    if (file.exists()) file.delete();

    BackupDatabaseConnection connection = new BackupDatabaseConnection(getContext(), mPassword, 0x400);
    connection.load(passwords);
    connection.close();

    try {
      ParcelFileDescriptor parcelFileDescriptor = getContext().getContentResolver().openFileDescriptor(mURI, "w");

      FileInputStream inputStream = new FileInputStream(file);
      FileOutputStream outputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

      byte[] buffer = new byte[512];
      int length;
      int size = 0;

      ByteBuffer byteBuffer = ByteBuffer.allocate(8);
      byteBuffer.putLong(new Date().getTime());
      byte[] time = byteBuffer.array();

      MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");

      while ((length = inputStream.read(buffer)) > 0) {
        shaDigest.update(buffer, 0, length);
      }

      byte[] hash = shaDigest.digest();

      inputStream.close();
      inputStream = new FileInputStream(file);

      outputStream.write(time, 0, time.length);
      outputStream.write(hash, 0, hash.length);

      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
        size += length;
      }

      inputStream.close();
      outputStream.close();


      getSupportActivity().makeSnackbar("Backup done!");
    } catch (Exception e) {
      getSupportActivity().makeSnackbar(e.getMessage());
      BaseActivity.showErrorLog(getClass(), e);
    } finally {
      file.delete();
      mLooper.quit();
      getSupportActivity().startAnimatorOnUiThread(mLinearLayoutAsContent, R.animator.fade_in);
      getSupportActivity().startAnimatorOnUiThread(mLinearLayoutAsLoader, R.animator.fade_out);
    }
  }

  private class AlertCallback extends BaseDialogCallback<BackupActivity> {

    public AlertCallback(BackupActivity activity) {
      super(activity);
    }

    @Override
    public void OnPositiveButtonPressed(DialogInterface dialog) {
      startFileSelector();
    }
  }
}
