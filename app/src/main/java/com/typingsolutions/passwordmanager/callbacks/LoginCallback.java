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
        try {
            User user = UserProvider.getInstance(context).login(loginActivity.getLoginServiceRemote(), password);
        } catch (UserProviderException | NoSuchAlgorithmException | RemoteException e) {
            Snackbar.make(v, "Sorry, your name doesn't exist", Snackbar.LENGTH_LONG).show();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setValues(Object... values) {
        if (values[0] instanceof String) {
            password = (String) values[0];
        }
    }
}
