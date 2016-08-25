package com.typingsolutions.passwordmanager.activities;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.typingsolutions.passwordmanager.AlertBuilder;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.async.OpenDatabaseTask;
import com.typingsolutions.passwordmanager.callbacks.LoginCallback;
import com.typingsolutions.passwordmanager.callbacks.LoginSafeLoginCheckBoxChangeCallback;
import com.typingsolutions.passwordmanager.callbacks.OpenDatabaseAsyncCallback;
import com.typingsolutions.passwordmanager.callbacks.ServiceCallbackImplementation;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;
import com.typingsolutions.passwordmanager.services.LoginService;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import core.Utils;
import ui.OutlinedImageView;

import java.io.File;
import java.lang.reflect.Field;

public class LoginActivity extends BaseActivity {

  public static final String SAFE_LOGIN = "safelogin";
  private ServiceCallbackImplementation mServiceCallback = new ServiceCallbackImplementation(this);
  private LoginCallback mLoginCallback = new LoginCallback(this);
  private ILoginServiceRemote mLoginServiceRemote;
  private boolean mServiceIsRegistered = false;

//  private AppBarLayout mAppBarLayoutAsWrapper;
//  private Toolbar mToolbarAsActionBar;
  private FloatingActionButton mFloatingActionButtonAsLogin;
  private EditText mEditTextAsLoginPassword;
  private CheckBox mCheckBoxAsSafeLoginFlag;
  private OutlinedImageView mOutlinedImageViewAsLockedBackground;
  private CoordinatorLayout mCoordinatorLayoutAsRootLayout;
  private ProgressBar mProgressBarAsLoadingIndicator;
  private ImageView mImageViewAsBackground;
  private TextView mTextViewAsHintForRootedDevices;

  private final ServiceConnection mLoginServiceConnection = new LoginServiceConnection();
  private OpenDatabaseAsyncCallback openDatabaseAsyncCallback = new OpenDatabaseAsyncCallback(this);
  private LoginSafeLoginCheckBoxChangeCallback safeLoginCheckedChangeListener = new LoginSafeLoginCheckBoxChangeCallback(this);


  private TextView.OnEditorActionListener setupKeyBoardActionListener = new TextView.OnEditorActionListener() {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
      if (actionId != EditorInfo.IME_ACTION_DONE) return false;
      mLoginCallback.onClick(mFloatingActionButtonAsLogin);
      //LoginActivity.this.startActivity(PasswordOverviewActivity.class, true);
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

//    mToolbarAsActionBar = findCastedViewById(R.id.loginlayout_toolbar_actionbar);
//    setSupportActionBar(mToolbarAsActionBar);

//    mAppBarLayoutAsWrapper = findCastedViewById(R.id.loginlayout_appbarlayout_wrapper);
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

//    mFloatingActionButtonAsLogin.hide();

    mFloatingActionButtonAsLogin.setOnClickListener(mLoginCallback);
//    mEditTextAsLoginPassword.addTextChangedListener(loginTextWatcher);
    mCheckBoxAsSafeLoginFlag.setOnCheckedChangeListener(safeLoginCheckedChangeListener);
    mCheckBoxAsSafeLoginFlag.setChecked(isSafe);
    mEditTextAsLoginPassword.setOnEditorActionListener(setupKeyBoardActionListener);

    mImageViewAsBackground.setImageBitmap(getBitmap(this, R.mipmap.verified, 2, 1));
    mOutlinedImageViewAsLockedBackground.setImageBitmap(getBitmap(this, R.mipmap.unverified, 2, 1));


  }

  @Override
  protected void onResume() {
    super.onResume();

    Intent intent = new Intent(getApplicationContext(), LoginService.class);
    startService(intent);
    bindService(intent, mLoginServiceConnection, Context.BIND_AUTO_CREATE);
    mServiceIsRegistered = true;

//    ViewCompat.setElevation(mAppBarLayoutAsWrapper, getResources().getDimension(R.dimen.dimen_sm));
  }

  @Override
  protected void onDestroy() {

    //mImageViewAsBackground.destroyDrawingCache();
    //mOutlinedImageViewAsLockedBackground.destroyDrawingCache();

    super.onDestroy();
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
      connection = new DatabaseConnection(this.getBaseContext(), password, Integer.parseInt(pim));

      OpenDatabaseTask openDatabaseTask = new OpenDatabaseTask();

      openDatabaseTask.registerCallback(openDatabaseAsyncCallback);
      openDatabaseTask.execute(connection);

    } catch (Exception e) {
      makeSnackbar("Sorry, something went wrong");
    } finally {
      if (connection != null) {
        connection.close();
        connection = null;
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

  public void increaseTries() {
    try {
      mLoginServiceRemote.increaseTries();
    } catch (RemoteException e) {
      showErrorLog(getClass(), e);
    }
  }

  public int getRemainingTries() {
    try {
      return mLoginServiceRemote.getRemainingTries();
    } catch (RemoteException e) {
      showErrorLog(getClass(), e);
    }
    return -1;
  }

  public void stopLoginService() {
    try {
      mLoginServiceRemote.stop();
    } catch (RemoteException e) {
      showErrorLog(getClass(), e);
    }
  }

  public boolean safeLogin() {
    return mCheckBoxAsSafeLoginFlag.isChecked();
  }

  @Override
  protected View getSnackbarRelatedView() {
    return mFloatingActionButtonAsLogin;
  }

  @Override
  protected void onActivityChange() {

    clearText(mEditTextAsLoginPassword);

    AlertDialog lastCreated = AlertBuilder.getLastCreated();
    if (lastCreated == null) return;
    EditText alertEditText = (EditText) lastCreated.findViewById(R.id.loginlayout_edittext_pim);
    clearText(alertEditText);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mServiceIsRegistered) {
      unbindService(mLoginServiceConnection);
      mServiceIsRegistered = false;
    }
  }

  private class LoginServiceConnection implements ServiceConnection {
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
  }
}
