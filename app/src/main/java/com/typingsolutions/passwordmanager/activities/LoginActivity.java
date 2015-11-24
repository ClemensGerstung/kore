package com.typingsolutions.passwordmanager.activities;

import android.app.ActivityManager;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
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
import android.widget.CheckBox;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import com.typingsolutions.passwordmanager.callbacks.SetupCallback;
import com.typingsolutions.passwordmanager.callbacks.service.GetLockTimeServiceCallback;
import com.typingsolutions.passwordmanager.receiver.LoginReceiver;
import com.typingsolutions.passwordmanager.services.LoginService;
import core.DatabaseProvider;
import ui.OutlinedImageView;

public class LoginActivity extends AppCompatActivity {

  private GetLockTimeServiceCallback serviceCallback = new GetLockTimeServiceCallback(this);

  private Toolbar toolbar;
  private FloatingActionButton floatingActionButton_login;
  private EditText editText_password;
  private android.widget.EditText editText_setupRepeated; // for setuplayout
  private android.widget.EditText editText_setupPassword; // for setuplayout
  private CheckBox checkBox_safeLogin;
  private OutlinedImageView outlinedImageView_background;
  private CoordinatorLayout coordinatorLayout_root;

  private ILoginServiceRemote loginServiceRemote;
  private LoginReceiver loginReceiver;
  private DatabaseProvider databaseProvider;

  private ServiceConnection loginServiceConnection = new ServiceConnection() {
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

  private TextWatcher setupTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      boolean hasPassword = editText_setupPassword.getText().length() > 0;
      boolean hasRepeated = editText_setupRepeated.getText().length() > 0;

      if(hasPassword & hasRepeated) floatingActionButton_login.show();
      else floatingActionButton_login.hide();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    databaseProvider = DatabaseProvider.getConnection(this);
    if (databaseProvider.needSetup()) {
      setContentView(R.layout.setup_layout);

      toolbar = (Toolbar) findViewById(R.id.setuplayout_toolbar);
      setSupportActionBar(toolbar);

      floatingActionButton_login = (FloatingActionButton) findViewById(R.id.setuplayout_floatingactionbutton_login);
      editText_setupPassword = (android.widget.EditText) findViewById(R.id.setuplayout_edittext_password);
      editText_setupRepeated = (android.widget.EditText) findViewById(R.id.setuplayout_edittext_repeatpassword);
      coordinatorLayout_root = (CoordinatorLayout) findViewById(R.id.setuplayout_coordinatorlayout_root);

      editText_setupPassword.addTextChangedListener(setupTextWatcher);
      editText_setupRepeated.addTextChangedListener(setupTextWatcher);

      floatingActionButton_login.setOnClickListener(new SetupCallback(this, this));
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
    outlinedImageView_background = (OutlinedImageView) findViewById(R.id.loginlayout_imageview_background);

    floatingActionButton_login.hide();

  }

  @Override
  protected void onResume() {
    super.onResume();

    Intent intent = new Intent(this, LoginService.class);
    if (!isServiceRunning(LoginService.class))
      startService(intent);

    bindService(intent, loginServiceConnection, Context.BIND_AUTO_CREATE);

    loginReceiver = new LoginReceiver(this);
    IntentFilter intentFilter = new IntentFilter(LoginService.INTENT_ACTION);
    getApplicationContext().registerReceiver(loginReceiver, intentFilter);
  }

  @Override
  protected void onStop() {
    unbindService(loginServiceConnection);
    getApplicationContext().unregisterReceiver(loginReceiver);
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
        Snackbar.make(floatingActionButton_login, "Sorry, something went wrong", Snackbar.LENGTH_LONG).show();
      } else {
        return true;
      }
    } else {
      Snackbar.make(floatingActionButton_login, "Sorry, your passwords don't match!", Snackbar.LENGTH_LONG).show();
      editText_setupRepeated.setText("");
      editText_setupRepeated.requestFocus();
    }
    return false;
  }

  @Deprecated
  public void switchStateOfFloatingActionButton(@DrawableRes int id, final @NonNull BaseCallback callback) {
    floatingActionButton_login.setImageResource(id);
    floatingActionButton_login.setOnClickListener(callback);
  }

  public ILoginServiceRemote getLoginServiceRemote() {
    return loginServiceRemote;
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
