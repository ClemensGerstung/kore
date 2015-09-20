package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.view.View;
import com.typingsolutions.passwordmanager.LoginActivity;

public class LoginCallback extends BaseCallback {
    private LoginActivity loginActivity;

    public LoginCallback(Context context, LoginActivity activity) {
        super(context);
        this.loginActivity = activity;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setValues(Object... values) {

    }
}
