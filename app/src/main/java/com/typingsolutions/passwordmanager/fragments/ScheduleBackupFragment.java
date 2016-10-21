package com.typingsolutions.passwordmanager.fragments;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.TextView;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.typingsolutions.passwordmanager.AlertBuilder;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.BackupActivity;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ScheduleBackupFragment extends BaseFragment<BackupActivity>
    implements EasyPermissions.PermissionCallbacks {
  static final int REQUEST_ACCOUNT_PICKER = 1000;
  static final int REQUEST_AUTHORIZATION = 1001;
  static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
  static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
  public static final String PREF_ACCOUNT_NAME = "accountName";
  public static final String PREF_SCHEDULE = "schedule";
  private static final String PREF_SCHEDULE_ENABLED = "enable";
  public static final String[] SCOPES = {DriveScopes.DRIVE_FILE};

  private GoogleAccountCredential mCredential;
  private Account[] mAvailableGoogleAccounts;

  private GridLayout mGridLayoutAsChooseAccount;
  private GridLayout mGridLayoutAsChooseScheduling;
  private GridLayout mGridLayoutAsEverythinDone;
  private SwitchCompat mSwitchCompatAsSwitcher;
  private ExpandableLinearLayout mLinearLayoutAsContainer;
  private TextView mTextViewAsAccountName;
  private TextView mTextViewAsSchedule;
  private String[] mScheduleTimes = new String[]{"Every day", "Once a week", "First day of month"};
  private SharedPreferences mPreferences;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.scheduled_backup_layout, container, false);

    mCredential = GoogleAccountCredential
        .usingOAuth2(getActivity().getApplicationContext(), Arrays.asList(SCOPES))
        .setBackOff(new ExponentialBackOff());

    mGridLayoutAsChooseAccount = (GridLayout) root.findViewById(R.id.backuplayout_layout_chooseaccount);
    mGridLayoutAsChooseAccount.setOnClickListener(this::onChooseAccountClick);

    mGridLayoutAsChooseScheduling = (GridLayout) root.findViewById(R.id.backuplayout_layout_choosescheduling);
    mGridLayoutAsChooseScheduling.setOnClickListener(this::onChooseScheduleClick);
    mTextViewAsSchedule = (TextView) root.findViewById(R.id.backuplayout_textview_scheduled);

    mGridLayoutAsEverythinDone = (GridLayout) root.findViewById(R.id.backuplayout_gridlayout_everythingdone);


    mSwitchCompatAsSwitcher = (SwitchCompat) root.findViewById(R.id.backuplayout_switch_schedulebackup);
    mLinearLayoutAsContainer = (ExpandableLinearLayout) root.findViewById(R.id.backuplayout_expandablelayout_schedulerwrapper);
    mTextViewAsAccountName = (TextView) root.findViewById(R.id.backuplayout_textview_username);

    mSwitchCompatAsSwitcher.setOnCheckedChangeListener(this::onSwitchChecked);
    mPreferences = getSupportActivity().getPreferences(Context.MODE_PRIVATE);

    String accountName = mPreferences.getString(PREF_ACCOUNT_NAME, null);
    if (accountName != null) {
      mTextViewAsAccountName.setText(accountName);
    }

    int index = mPreferences.getInt(PREF_SCHEDULE, -1);
    String scheduledTime = index >= 0 ? mScheduleTimes[index] : null;
    if(scheduledTime != null) {
      mTextViewAsSchedule.setText(scheduledTime);
    }

    if(accountName != null && scheduledTime != null) {
      mGridLayoutAsEverythinDone.setVisibility(View.VISIBLE);
    }

    mSwitchCompatAsSwitcher.setChecked(mPreferences.getBoolean(PREF_SCHEDULE_ENABLED, false));

    return root;
  }

  private void onSwitchChecked(CompoundButton button, boolean checked) {
    mLinearLayoutAsContainer.toggle();


    mPreferences.edit()
        .putBoolean(PREF_SCHEDULE_ENABLED, checked)
        .apply();

    // TODO: enable alarm
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  private void onChooseAccountClick(View v) {
    chooseAccount();
  }

  private void onChooseScheduleClick(View v) {
    AlertBuilder.create(getContext()).setItems(this::onChooseScheduleDialogItemClick, mScheduleTimes).show();
  }

  private void onChooseScheduleDialogItemClick(DialogInterface dialog, AdapterView<?> parent, View view, int position, long id) {
    dialog.dismiss();
    String time = mScheduleTimes[position];
    SharedPreferences settings = getSupportActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putInt(PREF_SCHEDULE, position);
    editor.apply();

    mTextViewAsSchedule.setText(time);

    String accountName = mPreferences.getString(PREF_ACCOUNT_NAME, null);
    if (accountName != null) {
      mGridLayoutAsEverythinDone.setVisibility(View.VISIBLE);
    }
  }

  @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
  private void chooseAccount() {
    if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.GET_ACCOUNTS)) {
      AccountManager accountManager = AccountManager.get(getContext());
      mAvailableGoogleAccounts = accountManager.getAccountsByType("com.google");

      String[] names = new String[mAvailableGoogleAccounts.length + 1];
      for (int i = 0; i < mAvailableGoogleAccounts.length; i++) {
        names[i] = mAvailableGoogleAccounts[i].name;
      }
      names[names.length - 1] = "Add account";

      AlertBuilder.create(getContext())
          .setItems(this::onAccountSelected, names)
          .show();
    } else {
      EasyPermissions.requestPermissions(this,
          "This app needs to access your Google account (via Contacts).",
          REQUEST_PERMISSION_GET_ACCOUNTS,
          Manifest.permission.GET_ACCOUNTS);
    }
  }

  void onAccountSelected(DialogInterface dialog, AdapterView<?> parent, View view, int position, long id) {
    if (position < parent.getCount() - 1) {
      String accountName = mAvailableGoogleAccounts[position].name;
      SharedPreferences settings = getSupportActivity().getPreferences(Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString(PREF_ACCOUNT_NAME, accountName);
      editor.apply();
      mCredential.setSelectedAccountName(accountName);
      mTextViewAsAccountName.setText(accountName);

      String scheduledTime = mPreferences.getString(PREF_SCHEDULE, null);
      if(scheduledTime != null) {
        mGridLayoutAsEverythinDone.setVisibility(View.VISIBLE);
      }
    } else {
      Intent addAccountIntent = new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT)
          .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      addAccountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
      getContext().startActivity(addAccountIntent);
    }
    dialog.dismiss();
  }

  private boolean isDeviceOnline() {
    ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    // TODO: check WIFI
    return (networkInfo != null && networkInfo.isConnected());
  }

  private boolean isGooglePlayServicesAvailable() {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
    return connectionStatusCode == ConnectionResult.SUCCESS;
  }

  private void acquireGooglePlayServices() {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(getContext());

    if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
      showGPlayErrorDialog(connectionStatusCode);
    }
  }

  void showGPlayErrorDialog(final int connectionStatusCode) {
    GoogleApiAvailability
        .getInstance()
        .getErrorDialog(getSupportActivity(),
                        connectionStatusCode,
                        REQUEST_GOOGLE_PLAY_SERVICES)
        .show();
  }

  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {

  }

  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {

  }
}
