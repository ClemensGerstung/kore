package com.typingsolutions.passwordmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import com.typingsolutions.passwordmanager.callbacks.CreateUserCallback;
import com.typingsolutions.passwordmanager.service.LoginService;

public class LoginActivity extends AppCompatActivity {

    private LoginUsernameFragment loginUsernameFragment = new LoginUsernameFragment();
    private FloatingActionButton add;

    private ILoginServiceRemote loginServiceRemote;

    private ServiceConnection loginServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            loginServiceRemote = ILoginServiceRemote.Stub.asInterface(service);
            //TODO: register IServiceCallback
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //TODO: unregister IServiceCallback
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        add = (FloatingActionButton) findViewById(R.id.mainlayout_floatingactionbutton_add);
        add.setOnClickListener(new CreateUserCallback(this));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_to_replace, loginUsernameFragment)
                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, LoginService.class);
        bindService(intent, loginServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(loginServiceConnection);
    }

    public void switchStateOfFloatingActionButton(@DrawableRes int id, final @NonNull BaseCallback callback) {
        add.setImageResource(id);
        add.setOnClickListener(callback);
    }

    public ILoginServiceRemote getLoginServiceRemote() {
        return loginServiceRemote;
    }
}
