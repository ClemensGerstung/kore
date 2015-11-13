package com.typingsolutions.passwordmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import core.data.UserProvider;

public class LoginReceiver extends BroadcastReceiver {
    private LoginActivity loginActivity;

    public LoginReceiver(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = UserProvider.getInstance(loginActivity).getId();

        try {
            loginActivity.getLoginServiceRemote().getBlockedTimeAsync(id);

//            boolean blocked = loginActivity.getLoginServiceRemote().isUserBlocked(id);
//            if(!blocked) return;
        } catch (RemoteException e) {
            Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }
}
