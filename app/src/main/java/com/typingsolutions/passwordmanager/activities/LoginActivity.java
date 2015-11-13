package com.typingsolutions.passwordmanager.activities;

import android.app.ActivityManager;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CheckBox;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import com.typingsolutions.passwordmanager.callbacks.service.GetLockTimeServiceCallback;
import com.typingsolutions.passwordmanager.receiver.LoginReceiver;
import com.typingsolutions.passwordmanager.services.LoginService;
import ui.OutlinedImageView;

public class LoginActivity extends AppCompatActivity {

    private GetLockTimeServiceCallback serviceCallback = new GetLockTimeServiceCallback(this);

    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton_login;
    private EditText editText_password;
    private CheckBox checkBox_safeLogin;
    private OutlinedImageView outlinedImageView_background;

    private ILoginServiceRemote loginServiceRemote;
    private LoginReceiver loginReceiver;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        toolbar = (Toolbar) findViewById(R.id.loginlayout_toolbar_actionbar);
        setSupportActionBar(toolbar);

        floatingActionButton_login = (FloatingActionButton) findViewById(R.id.loginlayout_floatingactionbutton_login);
        editText_password = (EditText) findViewById(R.id.loginlayout_edittext_password);
        checkBox_safeLogin = (CheckBox) findViewById(R.id.loginlayout_checkbox_safelogin);
        outlinedImageView_background = (OutlinedImageView) findViewById(R.id.loginlayout_imageview_background);
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
