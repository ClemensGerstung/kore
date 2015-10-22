package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.LoginActivity;

public class ShowEnterUsernameCallback extends BaseCallback {
    private LoginActivity activity;

    public ShowEnterUsernameCallback(Context context, LoginActivity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    public void setValues(Object... values) {
        activity.switchToEnterUsernameFragment();
    }

    @Override
    public void onClick(View v) {

    }
}
