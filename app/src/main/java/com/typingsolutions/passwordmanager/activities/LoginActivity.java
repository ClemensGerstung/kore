package com.typingsolutions.passwordmanager.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseTextWatcher;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.ServiceCallbackImplementation;
import com.typingsolutions.passwordmanager.services.LoginService;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import core.DatabaseProvider;
import core.Utils;
import core.data.PasswordProvider;
import ui.OutlinedImageView;

public class LoginActivity extends BaseActivity {

  public static final String SAFELOGIN = "safelogin";
  private ServiceCallbackImplementation serviceCallback = new ServiceCallbackImplementation(this);

  private Toolbar mToolbarAsActionBar;
  private FloatingActionButton mFloatingActionButtonAsLogin;
  private EditText mEditTextAsLoginPassword;
  private CheckBox mCheckBoxAsSafeLoginFlag;
  private OutlinedImageView mOutlinedImageViewAsLockedBackground;
  private CoordinatorLayout mCoordinatorLayoutAsRootLayout;
  private ProgressBar mProgressBarAsLoadingIndicator;
  private ImageView mImageViewAsBackground;
  private TextView mTextViewAsHintForRootedDevices;

  private ILoginServiceRemote loginServiceRemote;
  private DatabaseProvider databaseProvider;

//  private LoginCallback loginCallback = new LoginCallback(this, this);
//  private SetupPasswordCallback setupCallback = new SetupPasswordCallback(this, this);

  private final ServiceConnection loginServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      loginServiceRemote = ILoginServiceRemote.Stub.asInterface(service);

      try {
        loginServiceRemote.registerCallback(serviceCallback);

        if (loginServiceRemote.isBlocked()) {
          hideInput();
        }
      } catch (RemoteException e) {
        BaseActivity.showErrorLog(this.getClass(), e);
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      try {
//        Log.d(getClass().getSimpleName(), "Service disconnect");
        loginServiceRemote.unregisterCallback(serviceCallback);
        loginServiceRemote = null;
      } catch (RemoteException e) {
        BaseActivity.showErrorLog(this.getClass(), e);
      }
    }
  };

  private final TextWatcher setupTextWatcher = new BaseTextWatcher<LoginActivity>(this) {
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//      mButtonAsSetupButton.setEnabled(mEditTextAsRepeatSetupPassword.getText().equals(mEditTextAsSetupPassword.getText()));
    }
  };

  private final TextWatcher loginTextWatcher = new BaseTextWatcher<LoginActivity>(this) {
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (s.length() == 0) {
        mFloatingActionButtonAsLogin.hide();
//        loginCallback.setValues("", mCheckBoxAsSafeLoginFlag.isChecked());
      } else {
        mFloatingActionButtonAsLogin.show();
//        loginCallback.setValues(s.toString(), mCheckBoxAsSafeLoginFlag.isChecked());
      }
    }
  };

  private final CompoundButton.OnCheckedChangeListener safeLoginCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      final SharedPreferences preferences = LoginActivity.this.getPreferences(Context.MODE_PRIVATE);
      preferences.edit().putBoolean(LoginActivity.SAFELOGIN, isChecked).apply();
      PasswordProvider.getInstance(LoginActivity.this).isSafe(isChecked);
    }
  };

  private TextView.OnEditorActionListener setupKeyBoardActionListener = new TextView.OnEditorActionListener() {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
      if (actionId != EditorInfo.IME_ACTION_DONE) return false;
//      setupCallback.onClick(null);
      return true;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(DatabaseProvider.getConnection(this).needSetup()) {
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
    boolean isSafe = preferences.getBoolean(SAFELOGIN, true);

    mFloatingActionButtonAsLogin.hide();
    //mFloatingActionButtonAsLogin.setOnClickListener(loginCallback);
    mEditTextAsLoginPassword.addTextChangedListener(loginTextWatcher);
    mCheckBoxAsSafeLoginFlag.setOnCheckedChangeListener(safeLoginCheckedChangeListener);
    mCheckBoxAsSafeLoginFlag.setChecked(isSafe);

  }

  @Override
  protected void onResume() {
    super.onResume();

    startAndBindService(LoginService.class, loginServiceConnection, Context.BIND_AUTO_CREATE);
//    Intent intent = new Intent(this, LoginService.class);
//    if (!isServiceRunning(LoginService.class))
//      startService(intent);
//
//    bindService(intent, loginServiceConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
//    unbindService(loginServiceConnection);
    super.onStop();
  }

//  public boolean setupDatabase() {
//    databaseProvider.setOnSetupListener(new DatabaseProvider.OnSetupListener() {
//      @Override
//      public String onSetup() {
//        return mEditTextAsRepeatSetupPassword.getText().toString();
//      }
//    });
//    String password = mEditTextAsRepeatSetupPassword.getText().toString();
//    String repeated = mEditTextAsSetupPassword.getText().toString();
//
//    if (password.equals(repeated)) {
//      if (!databaseProvider.setup()) {
//        Snackbar.make(mCoordinatorLayoutAsRootLayout, "Sorry, something went wrong", Snackbar.LENGTH_LONG).show();
//      } else {
//        return true;
//      }
//    } else {
//      Snackbar
//          .make(mCoordinatorLayoutAsRootLayout, "Sorry, your passwords don't match!", Snackbar.LENGTH_LONG)
//          //.setAction("RETYPE", new RetypePasswordCallback(this, this))
//          .show();
//    }
//    return false;
//  }

//  public boolean isPasswordSafe() {
//    String password = mEditTextAsRepeatSetupPassword.getText().toString();
//    return Utils.isSafe(password);
//  }

//  public void retypePassword() {
//    runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
//        if (mEditTextAsRepeatSetupPassword != null) {
//          mEditTextAsRepeatSetupPassword.setText("");
//          mEditTextAsRepeatSetupPassword.requestFocus();
//        }
//        if (mEditTextAsSetupPassword != null)
//          mEditTextAsSetupPassword.setText("");
//        if (mEditTextAsLoginPassword != null)
//          mEditTextAsLoginPassword.setText("");
//      }
//    });
//  }

  public ILoginServiceRemote getLoginServiceRemote() {
    return loginServiceRemote;
  }

  public OutlinedImageView getBackground() {
    return mOutlinedImageViewAsLockedBackground;
  }

  public View getRootView() {
    return mCoordinatorLayoutAsRootLayout;
  }

  public synchronized void showWaiter() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ViewUtils.show(LoginActivity.this, mProgressBarAsLoadingIndicator, android.R.anim.fade_in);
      }
    });
  }

  public synchronized void hideWaiter() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ViewUtils.hide(LoginActivity.this, mProgressBarAsLoadingIndicator, android.R.anim.fade_out);
        mFloatingActionButtonAsLogin.setEnabled(true);
      }
    });
  }

  public void showInput() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mEditTextAsLoginPassword.show();
        ViewUtils.show(LoginActivity.this, mCheckBoxAsSafeLoginFlag, R.anim.checkbox_show);
        ViewUtils.show(LoginActivity.this, mTextViewAsHintForRootedDevices, android.support.design.R.anim.design_fab_in);

        startAnimator(mImageViewAsBackground, R.animator.flip_left_in);
        startAnimator(mOutlinedImageViewAsLockedBackground, R.animator.flip_left_out);
      }
    });
  }

  public void hideInput() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mEditTextAsLoginPassword.hide();
        ViewUtils.hide(LoginActivity.this, mCheckBoxAsSafeLoginFlag, R.anim.checkbox_hide);
        ViewUtils.hide(LoginActivity.this, mTextViewAsHintForRootedDevices, android.support.design.R.anim.design_fab_out);

        startAnimator(mImageViewAsBackground, R.animator.flip_right_out);
        startAnimator(mOutlinedImageViewAsLockedBackground, R.animator.flip_right_in);
      }
    });
  }


  @Override
  protected View getSnackbarRelatedView() {
    return this.mFloatingActionButtonAsLogin;
  }
}
