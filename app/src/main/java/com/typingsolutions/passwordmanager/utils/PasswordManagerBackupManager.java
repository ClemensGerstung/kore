package com.typingsolutions.passwordmanager.utils;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

public class PasswordManagerBackupManager extends BackupAgent {
  @Override
  public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {

  }

  @Override
  public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {

  }
}