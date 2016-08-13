package com.typingsolutions.passwordmanager.fragments;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.BackupActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ScheduleBackupFragment extends BaseFragment<BackupActivity>
    implements EasyPermissions.PermissionCallbacks {
  static final int REQUEST_ACCOUNT_PICKER = 1000;
  static final int REQUEST_AUTHORIZATION = 1001;
  static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
  static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
  private static final String PREF_ACCOUNT_NAME = "accountName";
  private static final String[] SCOPES = { DriveScopes.DRIVE_METADATA_READONLY };

  GoogleAccountCredential mCredential;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.scheduled_backup_layout, container, false);

    GridLayout l = (GridLayout) root.findViewById(R.id.backuplayout_layout_chooseaccount);
    l.setOnClickListener(this::onChooseAccountClick);

    mCredential = GoogleAccountCredential.usingOAuth2(
        getActivity().getApplicationContext(), Arrays.asList(SCOPES))
        .setBackOff(new ExponentialBackOff());

    return root;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(
        requestCode, permissions, grantResults, this);
  }

  private void onChooseAccountClick(View v) {
    chooseAccount();
  }

  @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
  private void chooseAccount() {
    if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.GET_ACCOUNTS)) {
      String accountName = getSupportActivity().getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
      if (accountName != null) {
        mCredential.setSelectedAccountName(accountName);
        getResultsFromApi();
      } else {
        Intent intent = mCredential.newChooseAccountIntent();
        intent.putExtra("overrideTheme", 1);
        startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
      }
    } else {
      EasyPermissions.requestPermissions(this, "This app needs to access your Google account (via Contacts).", REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
    }
  }

  private void getResultsFromApi() {
    if (!isGooglePlayServicesAvailable()) {
      acquireGooglePlayServices();
    } else if (mCredential.getSelectedAccountName() == null) {
      chooseAccount();
    } else if (!isDeviceOnline()) {
      getSupportActivity().makeSnackbar("No network connection available.");
    } else {
//      new MakeRequestTask(mCredential).execute();
    }
  }

  private boolean isDeviceOnline() {
    ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }

  private boolean isGooglePlayServicesAvailable() {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
    return connectionStatusCode == ConnectionResult.SUCCESS;
  }

  private void acquireGooglePlayServices() {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
    if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
    }
  }

  void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    Dialog dialog = apiAvailability.getErrorDialog(getSupportActivity(), connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
    dialog.show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch(requestCode) {
      case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode != RESULT_OK) {
          Log.d(getClass().getSimpleName(), "This app requires Google Play Services. Please install Google Play Services on your device and relaunch this app.");
        } else {
          getResultsFromApi();
        }
        break;
      case REQUEST_ACCOUNT_PICKER:
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
          String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null) {
            SharedPreferences settings = getSupportActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.apply();
            mCredential.setSelectedAccountName(accountName);
            getResultsFromApi();
          }
        }
        break;
      case REQUEST_AUTHORIZATION:
        if (resultCode == RESULT_OK) {
          getResultsFromApi();
        }
        break;
    }
  }

  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {

  }

  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {

  }
}
