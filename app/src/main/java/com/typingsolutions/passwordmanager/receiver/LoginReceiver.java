package com.typingsolutions.passwordmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.typingsolutions.passwordmanager.LoginActivity;
import com.typingsolutions.passwordmanager.LoginPasswordFragment;
import com.typingsolutions.passwordmanager.service.LoginService;
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
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            Fragment fragment = loginActivity.getSupportFragmentManager().getFragments().get(0);

            LoginPasswordFragment loginPasswordFragment = null;

            if (fragment instanceof LoginPasswordFragment) {
                loginPasswordFragment = (LoginPasswordFragment) fragment;

                if(intent.getBooleanExtra(LoginService.INTENT_RESET_FLAG, false)) {
                    loginPasswordFragment.getBackground().invalidate();
                }

                loginPasswordFragment.getBackground().invalidate();
            }
        }
    }
}
