package com.typingsolutions.passwordmanager.activities;

import android.app.Service;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import com.typingsolutions.passwordmanager.callbacks.CreateUserCallback;
import com.typingsolutions.passwordmanager.fragments.LoginUsernameFragment;
import com.typingsolutions.passwordmanager.receiver.LoginReceiver;
import com.typingsolutions.passwordmanager.services.LoginService;

public class LoginActivity extends AppCompatActivity {


    private LoginUsernameFragment loginUsernameFragment = new LoginUsernameFragment();
    private FloatingActionButton add;

    private ILoginServiceRemote loginServiceRemote;
    private LoginReceiver loginReceiver;

    private ServiceConnection loginServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            loginServiceRemote = ILoginServiceRemote.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            loginServiceRemote = null;
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
//        startService(intent);
        bindService(intent, loginServiceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter(LoginService.INTENT_ACTION);
        loginReceiver = new LoginReceiver(this);

        getApplicationContext().registerReceiver(loginReceiver, intentFilter);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.layout_to_replace, loginUsernameFragment).commit();
    }

    @Override
    protected void onPause() {
        unbindService(loginServiceConnection);
        getApplicationContext().unregisterReceiver(loginReceiver);

        super.onPause();
    }

    public void switchStateOfFloatingActionButton(@DrawableRes int id, final @NonNull BaseCallback callback) {
        add.setImageResource(id);
        add.setOnClickListener(callback);
    }

    public ILoginServiceRemote getLoginServiceRemote() {
        return loginServiceRemote;
    }
}
