package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.view.View;
import com.typingsolutions.passwordmanager.LoginActivity;
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

            // TODO: getFragment

            switch (e.getState()) {
                case LoginException.BLOCKED:
                    // loginPasswordFragment.lock(user.getId());

                    break;
                case LoginException.WRONG:
                    // clear EditText

                    break;
                default:
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
