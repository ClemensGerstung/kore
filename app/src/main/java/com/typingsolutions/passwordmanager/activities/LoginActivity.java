package com.typingsolutions.passwordmanager.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.ActivityManager;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.AnimatorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import com.typingsolutions.passwordmanager.callbacks.click.LoginCallback;
import com.typingsolutions.passwordmanager.callbacks.click.RetypePasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.click.SetupCallback;
import com.typingsolutions.passwordmanager.callbacks.service.ServiceCallbackImplementation;
import com.typingsolutions.passwordmanager.services.LoginService;
import core.DatabaseProvider;
import core.Utils;
import core.data.PasswordProvider;
import ui.OutlinedImageView;

public class LoginActivity extends AppCompatActivity {
  
  public static final String SAFELOGIN = "safelogin";
  private ServiceCallbackImplementation serviceCallback = new ServiceCallbackImplementation(this);

  private Toolbar toolbar;
  private FloatingActionButton floatingActionButton_login;
  private EditText editText_password;
  private android.widget.EditText editText_setupRepeated; // for setuplayout
  private android.widget.EditText editText_setupPassword; // for setuplayout
  private CheckBox checkBox_safeLogin;
  private OutlinedImageView outlinedImageView_background;
  private CoordinatorLayout coordinatorLayout_root;
  private ProgressBar progressBar_waiter;
  private ImageView imageView_background;
  private TextView textview_rootedIndicator;

  private ILoginServiceRemote loginServiceRemote;
  private DatabaseProvider databaseProvider;
  private LoginCallback loginCallback = new LoginCallback(this, this);

  private SetupCallback setupCallback = new SetupCallback(this, this);

  private final ServiceConnection loginServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      loginServiceRemote = ILoginServiceRemote.Stub.asInterface(service);

      try {
        loginServiceRemote.registerCallback(serviceCallback);
      } catch (RemoteException e) {
        Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      try {
        Log.d(getClass().getSimpleName(), "Service disconnect");
        loginServiceRemote.unregisterCallback(serviceCallback);
        loginServiceRemote = null;
      } catch (RemoteException e) {
        Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
      }
    }
  };

  private final TextWatcher setupTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      boolean hasPassword = editText_setupPassword.getText().length() > 0;
      boolean hasRepeated = editText_setupRepeated.getText().length() > 0;

      if (hasPassword & hasRepeated) floatingActionButton_login.show();
      else floatingActionButton_login.hide();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };

  private final TextWatcher loginTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (s.length() == 0) {
        floatingActionButton_login.hide();
        loginCallback.setValues("", checkBox_safeLogin.isChecked());
      } else {
        floatingActionButton_login.show();
        loginCallback.setValues(s.toString(), checkBox_safeLogin.isChecked());
      }
    }

    @Override
    public void afterTextChanged(Editable s) {

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
      setupCallback.onClick(null);
      return true;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    databaseProvider = DatabaseProvider.getConnection(this);
    boolean needSetup = databaseProvider.needSetup();
    if (needSetup) {
      setContentView(R.layout.setup_layout);

      toolbar = (Toolbar) findViewById(R.id.setuplayout_toolbar);
      setSupportActionBar(toolbar);

      floatingActionButton_login = (FloatingActionButton) findViewById(R.id.setuplayout_floatingactionbutton_login);
      editText_setupPassword = (android.widget.EditText) findViewById(R.id.setuplayout_edittext_password);
      editText_setupRepeated = (android.widget.EditText) findViewById(R.id.setuplayout_edittext_repeatpassword);
      coordinatorLayout_root = (CoordinatorLayout) findViewById(R.id.setuplayout_coordinatorlayout_root);

      editText_setupPassword.addTextChangedListener(setupTextWatcher);
      editText_setupRepeated.addTextChangedListener(setupTextWatcher);

      editText_setupRepeated.setOnEditorActionListener(setupKeyBoardActionListener);

      floatingActionButton_login.setOnClickListener(setupCallback);
      floatingActionButton_login.hide();
      return;
    }

    // no setup -> login
    setContentView(R.layout.login_layout);

    toolbar = (Toolbar) findViewById(R.id.loginlayout_toolbar_actionbar);
    setSupportActionBar(toolbar);

    floatingActionButton_login = (FloatingActionButton) findViewById(R.id.loginlayout_floatingactionbutton_login);
    editText_password = (EditText) findViewById(R.id.loginlayout_edittext_password);
    checkBox_safeLogin = (CheckBox) findViewById(R.id.loginlayout_checkbox_safelogin);
    outlinedImageView_background = (OutlinedImageView) findViewById(R.id.loginlayout_outlinedimageview_background);
    coordinatorLayout_root = (CoordinatorLayout) findViewById(R.id.loginlayout_coordinatorlayout_root);
    progressBar_waiter = (ProgressBar) findViewById(R.id.loginlayout_progressbar_waiter);
    imageView_background = (ImageView) findViewById(R.id.loginlayout_imageview_background);
    textview_rootedIndicator = (TextView) findViewById(R.id.loginlayout_textview_rootedindicator);
    if(!Utils.isRooted())
      textview_rootedIndicator.setVisibility(View.GONE);

    SharedPreferences preferences = getPreferences(MODE_PRIVATE);
    boolean isSafe = preferences.getBoolean(SAFELOGIN, true);

    floatingActionButton_login.hide();
    floatingActionButton_login.setOnClickListener(loginCallback);
    editText_password.addTextChangedListener(loginTextWatcher);
    checkBox_safeLogin.setOnCheckedChangeListener(safeLoginCheckedChangeListener);
    checkBox_safeLogin.setChecked(isSafe);
  }

  @Override
  protected void onResume() {
    super.onResume();

    Intent intent = new Intent(this, LoginService.class);
    if (!isServiceRunning(LoginService.class))
      startService(intent);

    bindService(intent, loginServiceConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    unbindService(loginServiceConnection);
    super.onStop();
  }

  public boolean setupDatabase() {
    databaseProvider.setOnSetupListener(new DatabaseProvider.OnSetupListener() {
      @Override
      public String onSetup() {
        return editText_setupPassword.getText().toString();
      }
    });
    String password = editText_setupPassword.getText().toString();
    String repeated = editText_setupRepeated.getText().toString();

    if (password.equals(repeated)) {
      if (!databaseProvider.setup()) {
        Snackbar.make(coordinatorLayout_root, "Sorry, something went wrong", Snackbar.LENGTH_LONG).show();
      } else {
        return true;
      }
    } else {
      Snackbar
          .make(coordinatorLayout_root, "Sorry, your passwords don't match!", Snackbar.LENGTH_LONG)
          .setAction("RETYPE", new RetypePasswordCallback(this, this))
          .show();
    }
    return false;
  }

  public boolean isPasswordSafe() {
    String password = editText_setupPassword.getText().toString();
    return Utils.isSafe(password);
  }

  public void retypePassword() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (editText_setupPassword != null) {
          editText_setupPassword.setText("");
          editText_setupPassword.requestFocus();
        }
        if (editText_setupRepeated != null)
          editText_setupRepeated.setText("");
        if (editText_password != null)
          editText_password.setText("");
      }
    });
  }

  public ILoginServiceRemote getLoginServiceRemote() {
    return loginServiceRemote;
  }

  public OutlinedImageView getBackground() {
    return outlinedImageView_background;
  }

  public View getRootView() {
    return coordinatorLayout_root;
  }

  public synchronized void showWaiter() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ViewUtils.show(LoginActivity.this, progressBar_waiter, android.R.anim.fade_in);
      }
    });
  }

  public synchronized void hideWaiter() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ViewUtils.hide(LoginActivity.this, progressBar_waiter, android.R.anim.fade_out);
      }
    });
  }

  public void showInput() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        editText_password.show();
        ViewUtils.show(LoginActivity.this, checkBox_safeLogin, R.anim.checkbox_show);
        ViewUtils.show(LoginActivity.this, textview_rootedIndicator, android.support.design.R.anim.design_fab_in);

        startAnimator(imageView_background, R.animator.flip_left_in);
        startAnimator(outlinedImageView_background, R.animator.flip_left_out);
      }
    });
  }

  public void hideInput() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        editText_password.hide();
        ViewUtils.hide(LoginActivity.this, checkBox_safeLogin, R.anim.checkbox_hide);
        ViewUtils.hide(LoginActivity.this, textview_rootedIndicator, android.support.design.R.anim.design_fab_out);

        startAnimator(imageView_background, R.animator.flip_right_out);
        startAnimator(outlinedImageView_background, R.animator.flip_right_in);
      }
    });
  }

  private synchronized void startAnimator(@NonNull View view, @AnimatorRes int res) {
    Animator animator = AnimatorInflater.loadAnimator(LoginActivity.this, res);
    animator.setTarget(view);
    animator.setDuration(300);
    animator.start();
  }

  private boolean isServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }
}
