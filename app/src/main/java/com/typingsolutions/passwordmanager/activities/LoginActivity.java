package com.typingsolutions.passwordmanager.activities;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseAsyncTask;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.async.OpenDatabaseTask;
import com.typingsolutions.passwordmanager.callbacks.LoginCallback;
import com.typingsolutions.passwordmanager.callbacks.OpenDatabaseAsyncCallback;
import com.typingsolutions.passwordmanager.callbacks.ServiceCallbackImplementation;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import com.typingsolutions.passwordmanager.services.LoginService;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import core.Utils;
import core.data.PasswordProvider;
import ui.OutlinedImageView;

import java.io.File;

public class LoginActivity extends BaseActivity {

  public static final String SAFE_LOGIN = "safelogin";
  private ServiceCallbackImplementation mServiceCallback = new ServiceCallbackImplementation(this);
  private LoginCallback mLoginCallback = new LoginCallback(this);
  private ILoginServiceRemote mLoginServiceRemote;
  private boolean mServiceIsRegistered = false;

  private Toolbar mToolbarAsActionBar;
  private FloatingActionButton mFloatingActionButtonAsLogin;
  private EditText mEditTextAsLoginPassword;
  private CheckBox mCheckBoxAsSafeLoginFlag;
  private OutlinedImageView mOutlinedImageViewAsLockedBackground;
  private CoordinatorLayout mCoordinatorLayoutAsRootLayout;
  private ProgressBar mProgressBarAsLoadingIndicator;
  private ImageView mImageViewAsBackground;
  private TextView mTextViewAsHintForRootedDevices;

  private final ServiceConnection mLoginServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mLoginServiceRemote = ILoginServiceRemote.Stub.asInterface(service);

      try {
        mLoginServiceRemote.registerCallback(mServiceCallback);

        if (mLoginServiceRemote.isBlocked()) {
          hideInput();
        }
      } catch (RemoteException e) {
        BaseActivity.showErrorLog(getClass(), e);
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      try {
//        Log.d(getClass().getSimpleName(), "Service disconnect");
        mLoginServiceRemote.unregisterCallback(mServiceCallback);
        mLoginServiceRemote = null;
      } catch (RemoteException e) {
        BaseActivity.showErrorLog(getClass(), e);
      }
    }
  };

  private final CompoundButton.OnCheckedChangeListener safeLoginCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      final SharedPreferences preferences = LoginActivity.this.getPreferences(Context.MODE_PRIVATE);
      preferences.edit().putBoolean(LoginActivity.SAFE_LOGIN, isChecked).apply();
      PasswordProvider.getInstance(LoginActivity.this).isSafe(isChecked);
    }
  };

  private TextView.OnEditorActionListener setupKeyBoardActionListener = new TextView.OnEditorActionListener() {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
      if (actionId != EditorInfo.IME_ACTION_DONE) return false;
      mLoginCallback.onClick(null);
      return true;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // check if database exists
    // if false, it needs to be setup
    File database = getDatabasePath(DatabaseConnection.DATABASE_NAME);
    if (!database.exists()) {
      startActivity(SetupActivity.class, true);
      return;
    }

    setContentView(R.layout.login_layout);

    mToolbarAsActionBar = findCastedViewById(R.id.loginlayout_toolbar_actionbar);
    setSupportActionBar(mToolbarAsActionBar);

    mFloatingActionButtonAsLogin = findCastedViewById(R.id.loginlayout_floatingactionbutton_login);
    mEditTextAsLoginPassword = findCastedViewById(R.id.loginlayout_edittext_password);
    mCheckBoxAsSafeLoginFlag = findCastedViewById(R.id.loginlayout_checkbox_safelogin);
    mOutlinedImageViewAsLockedBackground = findCastedViewById(R.id.loginlayout_outlinedimageview_background);
    mCoordinatorLayoutAsRootLayout = findCastedViewById(R.id.loginlayout_coordinatorlayout_root);
    mProgressBarAsLoadingIndicator = findCastedViewById(R.id.loginlayout_progressbar_waiter);
    mImageViewAsBackground = findCastedViewById(R.id.loginlayout_imageview_background);
    mTextViewAsHintForRootedDevices = findCastedViewById(R.id.loginlayout_textview_rootedindicator);

    if (!Utils.isRooted())
      ((ViewManager) mTextViewAsHintForRootedDevices.getParent()).removeView(mTextViewAsHintForRootedDevices);

    SharedPreferences preferences = getPreferences(MODE_PRIVATE);
    boolean isSafe = preferences.getBoolean(SAFE_LOGIN, true);

    mFloatingActionButtonAsLogin.hide();
    mFloatingActionButtonAsLogin.setOnClickListener(mLoginCallback);
//    mEditTextAsLoginPassword.addTextChangedListener(loginTextWatcher);
    mCheckBoxAsSafeLoginFlag.setOnCheckedChangeListener(safeLoginCheckedChangeListener);
    mCheckBoxAsSafeLoginFlag.setChecked(isSafe);
    mEditTextAsLoginPassword.setOnEditorActionListener(setupKeyBoardActionListener);
  }

  @Override
  protected void onResume() {
    super.onResume();

    Intent intent = new Intent(this, LoginService.class);
    if (!isServiceRunning(LoginService.class))
      startService(intent);

    bindService(intent, mLoginServiceConnection, Context.BIND_AUTO_CREATE);
    mServiceIsRegistered = true;
  }

  @Override
  protected void onDestroy() {
    Log.d(getClass().getSimpleName(), "onDestroy");
    super.onDestroy();
    if (mServiceIsRegistered) {
      unbindService(mLoginServiceConnection);
      mServiceIsRegistered = false;
    }
  }

  public void login(String pim) {
    DatabaseConnection connection = null;

    if (pim == null || pim.length() == 0) {
      makeSnackbar("You have to enter Your magic number");
      return;
    }

    try {
      showViewAnimated(mProgressBarAsLoadingIndicator, android.R.anim.fade_in);

      if (mLoginServiceRemote.isBlocked())
        makeSnackbar("Sorry, but You're currently blocked!");

      String password = mEditTextAsLoginPassword.getText().toString();
      connection = new DatabaseConnection(this, password, Integer.parseInt(pim));

      OpenDatabaseTask openDatabaseTask = new OpenDatabaseTask();
      openDatabaseTask.registerCallback(new OpenDatabaseAsyncCallback(this));
      openDatabaseTask.execute(connection);

    } catch (Exception e) {
      makeSnackbar("Sorry, something went wrong");
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }

  public OutlinedImageView getBackground() {
    return mOutlinedImageViewAsLockedBackground;
  }

  public void showInput() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        //mEditTextAsLoginPassword.show();
        ViewUtils.show(LoginActivity.this, mCheckBoxAsSafeLoginFlag, R.anim.checkbox_show);
        ViewUtils.show(LoginActivity.this, mTextViewAsHintForRootedDevices, android.support.design.R.anim.design_fab_in);
        ViewUtils.show(LoginActivity.this, mEditTextAsLoginPassword, android.support.design.R.anim.design_fab_in);

        startAnimator(mImageViewAsBackground, R.animator.flip_left_in);
        startAnimator(mOutlinedImageViewAsLockedBackground, R.animator.flip_left_out);
      }
    });
  }

  public void hideInput() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        //mEditTextAsLoginPassword.hide();
        ViewUtils.hide(LoginActivity.this, mCheckBoxAsSafeLoginFlag, R.anim.checkbox_hide);
        ViewUtils.hide(LoginActivity.this, mTextViewAsHintForRootedDevices, android.support.design.R.anim.design_fab_out);
        ViewUtils.hide(LoginActivity.this, mEditTextAsLoginPassword, android.support.design.R.anim.design_fab_out);

        startAnimator(mImageViewAsBackground, R.animator.flip_right_out);
        startAnimator(mOutlinedImageViewAsLockedBackground, R.animator.flip_right_in);
      }
    });
  }

  public void hideWaiter() {
    hideViewAnimated(mProgressBarAsLoadingIndicator, android.R.anim.fade_out);
  }

  @Override
  protected View getSnackbarRelatedView() {
    return mFloatingActionButtonAsLogin;
  }
}
