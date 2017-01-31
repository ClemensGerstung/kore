package com.typingsolutions.passwordmanager.fragments;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.typingsolutions.passwordmanager.AlertBuilder;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.BackupActivity;
import com.typingsolutions.passwordmanager.utils.BackupScheduleHelper;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.Arrays;
import java.util.List;


public class ScheduleBackupFragment extends BaseFragment<BackupActivity>
    implements EasyPermissions.PermissionCallbacks {
  static final int REQUEST_AUTHORIZATION = 1001;
  static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
  static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
  public static final String PREF_ACCOUNT_NAME = "accountName";
  public static final String PREF_SCHEDULE_TIME = "schedule";
  private static final String PREF_SCHEDULE_ENABLED = "enable";
  public static final String[] SCOPES = {DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA};

  private Account[] mAvailableGoogleAccounts;

  private GridLayout mGridLayoutAsChooseAccount;
  private GridLayout mGridLayoutAsChooseScheduling;
  private GridLayout mGridLayoutAsEverythinDone;
  private SwitchCompat mSwitchCompatAsSwitcher;
  private ExpandableLinearLayout mLinearLayoutAsContainer;
  private TextView mTextViewAsAccountName;
  private TextView mTextViewAsSchedule;
  private String[] mScheduleTimes = new String[]{"Every day", "Once a week", "Once a month"};
  private SharedPreferences mPreferences;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.scheduled_backup_layout, container, false);

    mGridLayoutAsChooseAccount = (GridLayout) root.findViewById(R.id.backuplayout_layout_chooseaccount);
    mGridLayoutAsChooseScheduling = (GridLayout) root.findViewById(R.id.backuplayout_layout_choosescheduling);
    mTextViewAsSchedule = (TextView) root.findViewById(R.id.backuplayout_textview_scheduled);
    mGridLayoutAsEverythinDone = (GridLayout) root.findViewById(R.id.backuplayout_gridlayout_everythingdone);
    mSwitchCompatAsSwitcher = (SwitchCompat) root.findViewById(R.id.backuplayout_switch_schedulebackup);
    mLinearLayoutAsContainer = (ExpandableLinearLayout) root.findViewById(R.id.backuplayout_expandablelayout_schedulerwrapper);
    mTextViewAsAccountName = (TextView) root.findViewById(R.id.backuplayout_textview_username);

    mPreferences = getSupportActivity().getPreferences(Context.MODE_PRIVATE);
    mSwitchCompatAsSwitcher.setChecked(mPreferences.getBoolean(PREF_SCHEDULE_ENABLED, false));
    mGridLayoutAsChooseAccount.setOnClickListener(this::onChooseAccountClick);
    mGridLayoutAsChooseScheduling.setOnClickListener(this::onChooseScheduleClick);
    mSwitchCompatAsSwitcher.setOnCheckedChangeListener(this::onSwitchChecked);

    init();

    return root;
  }

  private void onSwitchChecked(CompoundButton button, boolean checked) {
    if(checked) mLinearLayoutAsContainer.expand();
    else mLinearLayoutAsContainer.collapse();

    mPreferences.edit()
        .putBoolean(PREF_SCHEDULE_ENABLED, checked)
        .apply();

    notifyChange(null, -1);
  }

  private void init() {
    if (isGooglePlayServicesAvailable()) {
      acquireGooglePlayServices();

      String username = mPreferences.getString(PREF_ACCOUNT_NAME, null);
      int index = mPreferences.getInt(PREF_SCHEDULE_TIME, -1);

      notifyChange(username, index);

    } else {
      AlertBuilder.create(getContext())
          .setMessage("Google Play Services are not available.\nPlease install them and try again.")
          .setPositiveButton("ok", null)
          .show();
    }
  }

  private void notifyChange(@Nullable String username, int time) {
    boolean userOK = false;
    boolean timeOK = false;

    if (username != null) {
      mTextViewAsAccountName.setText(username);
      mPreferences.edit().putString(PREF_ACCOUNT_NAME, username).apply();
      userOK = true;
    }

    if (time >= 0) {
      String scheduledTime = mScheduleTimes[time];
      mTextViewAsSchedule.setText(scheduledTime);
      mPreferences.edit().putInt(PREF_SCHEDULE_TIME, time).apply();
      timeOK = true;
    }

    if(!userOK) {
      userOK = mPreferences.getString(PREF_ACCOUNT_NAME, null) != null;
    }

    if(!timeOK) {
      timeOK = mPreferences.getInt(PREF_SCHEDULE_TIME, -1) >= 0;
    }

    if(userOK && timeOK && mSwitchCompatAsSwitcher.isChecked()) {
      mLinearLayoutAsContainer.setExpanded(true);
      mGridLayoutAsEverythinDone.setVisibility(View.VISIBLE);

      BackupScheduleHelper.schedule(getContext(), time, true);
    } else {
      mGridLayoutAsEverythinDone.setVisibility(View.GONE);

      BackupScheduleHelper.cancel(getContext());
    }
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
    AlertBuilder.create(getContext())
        .setItems(this::onChooseScheduleDialogItemClick, mScheduleTimes)
        .show();
  }

  private void onChooseScheduleDialogItemClick(DialogInterface dialog, AdapterView<?> parent, View view, int position, long id) {
    dialog.dismiss();
    notifyChange(null, position);
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
      notifyChange(accountName, -1);

      GoogleAccountCredential credential = GoogleAccountCredential
          .usingOAuth2(getContext().getApplicationContext(), Arrays.asList(ScheduleBackupFragment.SCOPES))
          .setBackOff(new ExponentialBackOff());

      credential.setSelectedAccountName(accountName);

      new PermissionTask(credential).execute();
    } else {
      Intent addAccountIntent = new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT)
          .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      addAccountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
      getContext().startActivity(addAccountIntent);
    }
    dialog.dismiss();
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

  private class PermissionTask extends AsyncTask<Void, Void, Void> {
    private com.google.api.services.drive.Drive mService = null;
    private Exception mLastError = null;

    public PermissionTask(GoogleAccountCredential credential) {
      HttpTransport transport = AndroidHttp.newCompatibleTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      mService = new Drive.Builder(transport, jsonFactory, credential)
          .setApplicationName("kore")
          .build();
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        FileList result = mService.files().list()
            .setPageSize(10)
            .setFields("nextPageToken, items(id, name)")
            .execute();
      } catch (Exception e) {
        mLastError = e;
        cancel(true);
      }
      return null;
    }

    @Override
    protected void onCancelled() {
      if (mLastError == null) {
        return;
      }

      if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
        showGPlayErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
      } else if (mLastError instanceof UserRecoverableAuthIOException) {
        startActivityForResult(
            ((UserRecoverableAuthIOException) mLastError).getIntent(),
            REQUEST_AUTHORIZATION);
      }
    }
  }
}
