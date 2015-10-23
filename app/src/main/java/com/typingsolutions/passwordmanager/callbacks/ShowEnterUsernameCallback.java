package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.fragments.LoginUsernameFragment;

public class ShowEnterUsernameCallback extends BaseCallback {
    private LoginActivity activity;

    public ShowEnterUsernameCallback(Context context, LoginActivity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    public void setValues(Object... values) {

    }

    @Override
    public void onClick(View v) {
        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(LoginUsernameFragment.REMEMBER, false);
        editor.apply();

        activity.switchToEnterUsernameFragment();
    }
}
