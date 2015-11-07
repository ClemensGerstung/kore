package com.typingsolutions.passwordmanager.activities;

import android.app.ActivityManager;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import com.typingsolutions.passwordmanager.callbacks.CreateUserCallback;
import com.typingsolutions.passwordmanager.callbacks.service.GetLockTimeServiceCallback;
import com.typingsolutions.passwordmanager.fragments.LoginPasswordFragment;
import com.typingsolutions.passwordmanager.fragments.LoginUsernameFragment;
import com.typingsolutions.passwordmanager.receiver.LoginReceiver;
import com.typingsolutions.passwordmanager.services.LoginService;

public class LoginActivity extends AppCompatActivity {


    private LoginUsernameFragment loginUsernameFragment = new LoginUsernameFragment();
    private LoginPasswordFragment loginPasswordFragment = new LoginPasswordFragment();

    private GetLockTimeServiceCallback serviceCallback = new GetLockTimeServiceCallback(loginPasswordFragment);

    private FloatingActionButton add;

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

        add = (FloatingActionButton) findViewById(R.id.mainlayout_floatingactionbutton_add);
        add.setOnClickListener(new CreateUserCallback(this));
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

        switchToEnterUsernameFragment();
    }

    @Override
    protected void onStop() {
        unbindService(loginServiceConnection);
        getApplicationContext().unregisterReceiver(loginReceiver);
        super.onStop();
    }

    public void switchStateOfFloatingActionButton(@DrawableRes int id, final @NonNull BaseCallback callback) {
        add.setImageResource(id);
        add.setOnClickListener(callback);
    }

    public void switchToEnterUsernameFragment() {
        loginPasswordFragment.clearEditText();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.layout_to_replace, loginUsernameFragment).commit();
    }

    public void switchToEnterPasswordFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.layout_to_replace, loginPasswordFragment).commit();
        loginUsernameFragment = new LoginUsernameFragment();
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
