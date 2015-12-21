package com.typingsolutions.passwordmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.adapter.BackupViewPagerAdapter;
import com.typingsolutions.passwordmanager.fragments.BackupFragment;
import core.DatabaseProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;


public class BackupRestoreActivity extends AppCompatActivity {
  private Toolbar toolbar_actionbar;
  private TabLayout tablayout_tabhost;
  private ViewPager viewpager_wrapper;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backup_restore_layout);

    toolbar_actionbar = (Toolbar) findViewById(R.id.backuprestorelayout_toolbar_actionbar);
    tablayout_tabhost = (TabLayout) findViewById(R.id.backuprestorelayout_tablayout_tabhost);
    viewpager_wrapper = (ViewPager) findViewById(R.id.backuprestorelayout_viewpager_wrapper);

    setSupportActionBar(toolbar_actionbar);

    BackupViewPagerAdapter adapter = new BackupViewPagerAdapter(this, getSupportFragmentManager());
    viewpager_wrapper.setAdapter(adapter);
    tablayout_tabhost.setupWithViewPager(viewpager_wrapper);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == BackupFragment.BACKUP_REQUEST_CODE
        && resultCode == Activity.RESULT_OK) {
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

        final int flags = data.getFlags() &
            (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        getContentResolver().takePersistableUriPermission(uri, flags);
      } catch (Exception e) {
        Snackbar.make(viewpager_wrapper, "Couldn't do your backup", Snackbar.LENGTH_LONG).show();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
