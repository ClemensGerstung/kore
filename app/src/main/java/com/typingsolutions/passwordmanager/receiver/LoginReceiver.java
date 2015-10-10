package com.typingsolutions.passwordmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.fragments.LoginPasswordFragment;
import core.UserProvider;

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

            boolean blocked = loginActivity.getLoginServiceRemote().isUserBlocked(id);
            if(!blocked) return;

            Fragment fragment = loginActivity.getSupportFragmentManager().getFragments().get(0);

            LoginPasswordFragment loginPasswordFragment = null;

            if (fragment instanceof LoginPasswordFragment) {
                loginPasswordFragment = (LoginPasswordFragment) fragment;
                loginPasswordFragment.getBackground().invalidate();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
