package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.fragments.LoginPasswordFragment;
import core.data.UserProvider;
import core.exceptions.LoginException;

public class FakeLoginCallback extends BaseCallback {
    private LoginActivity loginActivity;

    public FakeLoginCallback(Context context, LoginActivity loginActivity) {
        super(context);
        this.loginActivity = loginActivity;
    }

    @Override
    public void setValues(Object... values) {

    }

    @Override
    public void onClick(View v) {
        try {
            UserProvider.getInstance(context).fakeLogin(loginActivity.getLoginServiceRemote(), 100);
        } catch (Exception e) {
            if (e instanceof LoginException) {
                LoginException exception = (LoginException) e;
                Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();

                Fragment fragment = loginActivity.getSupportFragmentManager().getFragments().get(0);

                if (fragment instanceof LoginPasswordFragment) {
                    LoginPasswordFragment loginPasswordFragment = (LoginPasswordFragment) fragment;

                    if (exception.getState() == LoginException.WRONG)
                        loginPasswordFragment.retypePassword();

                }
            } else {
//                Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
                Snackbar.make(v, "Sorry, something went wrong", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
