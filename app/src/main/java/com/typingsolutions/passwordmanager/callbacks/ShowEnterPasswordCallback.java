package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.fragments.LoginPasswordFragment;
import com.typingsolutions.passwordmanager.fragments.LoginUsernameFragment;
import core.UserProvider;

public class ShowEnterPasswordCallback extends BaseCallback {

    private LoginActivity activity;
    private String username;

    public ShowEnterPasswordCallback(Context context, LoginActivity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        FloatingActionButton floatingActionButton = (FloatingActionButton) v;
        if (!UserProvider.getInstance(context).userExists(username)) {
            Snackbar.make(v, "Sorry, your name doesn't exist", Snackbar.LENGTH_LONG)
                    .setAction("CREATE", new CreateUserCallback(context, username))
                    .show();
            return;
        }

        UserProvider.getInstance(context).setUsername(username);

        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        boolean rememberUser = preferences.getBoolean(LoginUsernameFragment.REMEMBER, false);

        if (rememberUser) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(LoginUsernameFragment.REMEMBERED_USERNAME, username);
        }

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.layout_to_replace, new LoginPasswordFragment())
                .commit();

        floatingActionButton.setImageResource(R.drawable.add);
        floatingActionButton.setOnClickListener(new CreateUserCallback(context));
    }

    @Override
    public void setValues(Object... values) {
        if (values[0] instanceof String) {
            username = (String) values[0];
        }
    }
}
