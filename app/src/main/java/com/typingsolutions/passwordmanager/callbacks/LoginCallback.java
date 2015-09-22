package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import com.typingsolutions.passwordmanager.LoginActivity;
import com.typingsolutions.passwordmanager.LoginPasswordFragment;
import core.User;
import core.UserProvider;
import core.exceptions.LoginException;
import core.exceptions.UserProviderException;

import java.security.NoSuchAlgorithmException;

public class LoginCallback extends BaseCallback {
    private LoginActivity loginActivity;
    private String password;

    public LoginCallback(Context context, LoginActivity activity) {
        super(context);
        this.loginActivity = activity;
    }

    @Override
    public void onClick(View v) {
        User user = null;
        try {
            user = UserProvider.getInstance(context).login(loginActivity.getLoginServiceRemote(), password);
        } catch (UserProviderException | NoSuchAlgorithmException | RemoteException e) {
            Snackbar.make(v, "Sorry, something went wrong", Snackbar.LENGTH_LONG).show();
        } catch (LoginException e) {
            Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();

            Fragment fragment = loginActivity.getSupportFragmentManager().getFragments().get(0);

            LoginPasswordFragment loginPasswordFragment = null;

            if (fragment instanceof LoginPasswordFragment) {
                loginPasswordFragment = (LoginPasswordFragment) fragment;

                if (e.getState() == LoginException.WRONG) {
                    loginPasswordFragment.retypePassword();
                }
            }


        }
    }

    @Override
    public void setValues(Object... values) {
        if (values[0] instanceof String) {
            password = (String) values[0];
        }
    }
}
